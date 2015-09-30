package project;

import java.util.List;
import java.util.Map;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

/**
 * DBManager is to manager database, including manage storage data and the indexes
 * @author wangqian
 *
 */
public class DBManager {
	
	Logger logger = (Logger) LogManager.getLogger(DBManager.class);

	private static DBManager dbManager = null;
	
	// database is to be contain the data
	private byte[] data;
	
	// Keep track of how much space is used in data
	private int DATA_USED;
	
	// Keep track of how much space is used in metadata
	private int INDEXES_USED;
	
	// Size of data and metadata
	private static final int DATA_SIZE = Storage.DATA_SIZE;
	
	public static final int METADATA_SIZE = Storage.METADATA_SIZE;

	// Names of Data and Metadata
	private static final String DBDATA_NAME = "data.db";
	
	private static final String DBMETA_NAME = "data.meta";
	
	/**
	 * indexes is to be contain the indexes in the metadata
	 * Key is the index key
	 * List is the index of that key
	 */

	private Map<Integer, Index> indexes;
	
	//Locker controls the concurrency of the database.
	private DbLocker Locker;
	
	/**
	 *  DBstorage for reading and writing the data
	 *  indexHelper for manipulating the indexes
	 */
	private Storage DBstorage;
	private IndexHelper indexHelper;
	
	private DBManager(){}
	
	/**
	 * Singleton Object
	 * @return
	 */
	public static DBManager getInstance(){
		if (dbManager == null){
			// Instantiate and Initialization of DBManager
			dbManager = new DBManager();
			dbManager.DBstorage = new StorageImpl();
			dbManager.indexHelper = new IndexHelperImpl();
			dbManager.Locker = new DbLocker();
			dbManager.readDatabase();
		}
		
		return dbManager;
	}
	
	public void Put(int key, byte[] data) {
		/**
		 * In order to avoid during saving period rebooting, we save the data file first and then save the metadata.
		 * Saving process:
		 * 		1. There is only one thread to save data. The saving thread will occupy the data file. Other threads
		 * 			including reading threads will wait until the saving thread complete.
		 * 		2. Locate the saving index:  call the method of findFreeSpaceIndex
		 * 		3. Save the data to data part.
		 * 		4. Save the indexes information to metadata part.
		 * 		5. Update the metadata buffer in memory
		 * 
		 * Params:  key -- data key
		 * 			data -- data information
		 *
		 */ 
		try {
			Locker.writeLock();			
			logger.info("Attempting to put key: " + key + "to database");
			// if database is going to be out of volume, block the put attempt.
			if (data.length + DATA_USED > DATA_SIZE) {
				System.out.println("Not enough data space left. Put Attempt with key "
					+ key + " Failed.");
				return;
			}
            //if key already exists in the database, update it by removing it first.
			if (indexes.containsKey(key)){
				Remove(key);
				}
					// Getting the index list of free space in data array;
					List<Pair<Integer,Integer>> index_pairs = indexHelper.findFreeSpaceIndex(data.length);
					indexHelper.splitDataBasedOnIndex(data, index_pairs);
					// Updating the Index map. If the metadata is out of volume, block the putting attempt.
					Index tmpindex = new Index();
					tmpindex.setKey(key);
					tmpindex.setIndexes(index_pairs);
					int indexsize = indexHelper.getIndexSize(index_pairs);
					if (indexsize + INDEXES_USED > METADATA_SIZE) {
						System.out.println("Not enough metadata space left. Put Attempt with key "
					+ key + " Failed.");
						return;
					}
					indexes.put(key, tmpindex);
					set_INDEXES_USED(get_INDEXES_USED() + indexsize);
					logger.info("Metadata buffer updated");
					
					// Writing the database onto the disk
					
					DBstorage.writeData(DBDATA_NAME, this.data);
					set_DATA_USED(get_DATA_USED() + data.length);
					System.out.println("Data related to key " + key +" wrote to " + DBDATA_NAME);
					
					byte[] metadata = indexHelper.indexToBytes(indexes);
					
					DBstorage.writeMetaData(DBMETA_NAME, metadata);
					logger.info("Metadata updated on disk");
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally{
			try {
			Locker.writeUnlock();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public byte[] Get(int key) {
		/**
		 * Returns the data that is mapped to the given key; If no
		 * such key exists in database, return null and Print a message
		 * to the console.
		 * 
		 * Querying Process:
		 * 		  1. There can be multiple threads reading the database. No threads
		 * 			 can write the database when there is at least one thread reading.
		 *  
		 *  Params:  key -- data key
		 * 
		 */
		byte[] databuffer = new byte[Storage.DATA_SIZE];
		byte[] returndata = null;
		try {
			Locker.ReadLock();
			logger.info("Attempting to get data mapped to key :" + key);
			if (indexes.containsKey(key)) {
				List<Pair<Integer, Integer>> index = indexes.get(key).getIndexes();
				//extracting the mapped data from the data in memory;
				int start = 0;
				for (Pair<Integer, Integer> p : index) {
					System.arraycopy(data, p.getLeft(), databuffer, start, p.getRight());
					start += p.getRight();
				}
				returndata = new byte[start];
				System.arraycopy(databuffer, 0, returndata, 0, start);
				logger.info("Data with key " + key + " is " + returndata.toString());
			} else {
				System.out.println("No data with key "+ key +" exists in database.");
			}
		} catch (Exception e) {
			System.out.println("Interrupted while reading data");
			e.printStackTrace();
		} finally{
			Locker.ReadUnlock();
		}

		return returndata;	
	}

	public void Remove(int key) {
		/**
		 * Removes the mapped key - data relation in the database;
		 * If no such key exists, print an error message to the console.
		 * 
		 * Params:  key -- data key
		 */
		try {
			Locker.writeLock();
			logger.info("Attempting to remove the data with key :" + key);
			if (!indexes.containsKey(key)) {
				System.out.println("No data with key " + key +" exists in database.Failed to remove.");
			} else {
				// Removing the key in the metadata buffer and update the metadata file
				List<Pair<Integer, Integer>> l = indexes.get(key).getIndexes();
				int size = 0;
				for (Pair<Integer, Integer> p : l) {
					size += p.getRight();
				}
				indexes.remove(key);
				this.set_DATA_USED(get_DATA_USED() - size);
				logger.info("Metadata buffer updated");
				DBstorage.writeMetaData(DBMETA_NAME, indexHelper.indexToBytes(indexes));
				logger.info("Metadata updated on disk");
				System.out.println("Data with key " + key + " is removed.");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			try {
			Locker.writeUnlock();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
	}

	public void readDatabase() {
		//Read the database and upload the data into memory
		byte[] metadata;
		try{
			data = DBstorage.readData(DBDATA_NAME);
			logger.info("Data read in memory");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to read Data into memory");
		}
		try{
			metadata = DBstorage.readMetaData(DBMETA_NAME);
			indexes = indexHelper.bytesToIndex(metadata);
			System.out.println("Free Space left is:" + (DATA_SIZE - DATA_USED));
			System.out.println("Free Meta Space left is:" + (METADATA_SIZE - INDEXES_USED));
			logger.info("Metadata read in Memory");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] database) {
		this.data = database;
	}

	public Map<Integer, Index> getIndexBuffer() {
		return indexes;
	}
	
	public int get_DATA_USED() {
		return DATA_USED;
	}
	
	public void set_DATA_USED(int size) {
		DATA_USED = size;
	}
	
	public int get_INDEXES_USED() {
		return INDEXES_USED;
	}
	
	public void set_INDEXES_USED(int size) {
		INDEXES_USED = size;
	}
	
	public int getfreespace() {
		return Storage.DATA_SIZE - DATA_USED;
	}
	
	public void clear() {
		// for clearing the database
		try {
			Locker.writeLock();
			//Setting the metadata buffer in memory to an empty Hashtable
			indexes = new Hashtable<Integer, Index>();
			logger.info("Clear : Metadata buffer updated");
			set_INDEXES_USED(0);
			DBstorage.writeMetaData(DBMETA_NAME, indexHelper.indexToBytes(indexes));
			logger.info("Metadata updated on disk");
			set_DATA_USED(0);
			System.out.println("Database Cleared!");
			System.out.println("Current Free Space is " + this.getfreespace());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Locker.writeUnlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
