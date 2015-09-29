package project;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

/**
 * DBManager is to manager database, including manage storage data and the indexes
 * @author wangqian
 *
 */
public class DBManager {
	
	Logger logger = (Logger) LogManager.getLogger();

	private static DBManager dbManager = null;
	
	// database is to be contain the data
	private byte[] data;
	
	private static final String DBDATA_NAME = "data.db";
	
	private static final String DBMETA_NAME = "data.meta";
	/**
	 * indexes is to be contain the indexes in the metadata
	 * Key is the index key
	 * List is the index of that key
	 */

	private Map<Integer, Index> indexes;
	
	/**
	 * Locker controls the concurrency of the database.
	 */
	private DbLocker Locker;
	private Storage DBstorage;
	private IndexHelper indexHelper;
	
	private DBManager(){}
	
	/**
	 * Singleton Object
	 * @return
	 */
	public static DBManager getInstance(){
		if (dbManager == null){
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
			
			System.out.println("Attempting to put key: " + key + " data : " + data + "to database");  
			if (indexes.containsKey(key)){
					System.out.println("Violation of Primary keys; Key already"
							+ "exists in database.");
				}
			else{
					// Getting the index list of free space in data array;
					List<Pair<Integer,Integer>> index_pairs = indexHelper.findFreeSpaceIndex(data.length);
					indexHelper.splitDataBasedOnIndex(data, index_pairs);
					
					indexes.put(key, indexHelper.getIndex(index_pairs,key));
					System.out.println("Metadata buffer updated");
					
					// Writing the database onto the disk
					
					DBstorage.writeData(DBDATA_NAME, this.data);
					System.out.println("Data wrote to " + DBDATA_NAME);
					System.out.println("*************************");
					System.out.println(indexes);
					
					byte[] metadata = indexHelper.indexToBytes(indexes);
					
					DBstorage.writeMetaData(DBMETA_NAME, metadata);
					System.out.println("Metadata updated on disk");
					System.out.println("*************************");
					System.out.println(indexHelper.indexToBytes(indexes).length);
				}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally{
			Locker.writeUnlock();
		}
	}

	public byte[] Get(int key) {
		/**
		 * Returns the data that is mapped to the given key; If no
		 * such key exists in database, return null
		 * 
		 * Querying Process:
		 * 		  1. There can be multiple threads reading the database. No threads
		 * 			 can write the database when there is at least one thread reading.
		 *  
		 *  Params:  key -- data key
		 * 
		 */
		byte[] databuffer = null;
		System.out.println("Attempting to get data mapped to key :" + key);
		try {
			Locker.readLock();
			if (indexes.containsKey(key)) {
				System.out.println();
				System.out.println("&&&&&&&&&&&&&&&&&&&&&&");
				System.out.println(indexes);
				databuffer = indexHelper.indexToBytes(indexes);
				System.out.println(databuffer);
				System.out.println();
			} else {
				System.out.println("No data with such key exists in database.");
			}
		} catch (Exception e) {
			System.out.println("Interrupted while reading data");
			e.printStackTrace();
		} finally{
			Locker.readUnlock();
		}

		return databuffer;	
	}

	public void Remove(int key) {
		/**
		 * Removes the mapped key - data relation in the database;
		 * If no such key exists, this attempt will be recorded to log.
		 * 
		 * Params:  key -- data key
		 */
		System.out.println("Attempting to remove the data with key :" + key);
		try {
			Locker.writeLock();
			if (!indexes.containsKey(key)) {
				System.out.println("No data with such key exists in database.Failed to remove.");
			} else {
				// Removing the key in the metadata buffer and update the metadata file
				indexes.remove(key);
				System.out.println("Metadata buffer updated");
				
				DBstorage.writeMetaData(DBMETA_NAME, indexHelper.indexToBytes(indexes));
				System.out.println("Metadata updated on disk");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			Locker.writeUnlock();
		}
		
	}

	public void readDatabase() {
		/**
		 * Read the database and upload the data into memory
		 */
		byte[] metadata;
		try{
			data = DBstorage.readData(DBDATA_NAME);
			System.out.println("Data read in memory");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to read Data into memory");
		}
		try{
			metadata = DBstorage.readMetaData(DBMETA_NAME);
			indexes = indexHelper.bytesToIndex(metadata);
			System.out.println("Metadata read in Memory");
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

}
