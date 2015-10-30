package project;

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

/**
 * DBManager is to manager database, including manage storage data and the clusteredIndexes
 * @author wangqian
 *
 */
public class DBManager {
	Logger logger = (Logger) LogManager.getLogger();
   
	private static DBManager dbManager = null;

	private byte[] data;// database is to be contain the data
	private int DATA_USED;// Keep track of how much space is used in data
	private int INDEXES_USED;// Keep track of how much space is used in index metadata
	private int METADATA_USED;// Keep track of how much space is used in metadata
	private static final int DATA_SIZE = Storage.DATA_SIZE;// Size of data
	public static final int METADATA_SIZE = Storage.METADATA_SIZE;// Size of metadata
	private static String DB_NAME;// Names of Data and Metadata
	
	/**
	 * clusteredIndexes is to be contain the clusteredIndexes in the metadata
	 * Key is the index key
	 * List is the index of that key
	 */
	private Map<Integer, Index> clusteredIndexes;

	private Map<String, Object> unclusteredIndexes;

	private Map<Integer,List<Pair>> tabMetadata;
	
	//Locker controls the concurrency of the database.
	private DbLocker Locker;
	private Storage DBStorage;
	private IndexHelper indexHelper;


	//Method---------------------------------------
	private DBManager(String dbname) {DB_NAME=dbname;}
	public static void close(){dbManager=null;}
	public static String getDBName(){return DB_NAME;}
	public static DBManager getInstance(String dbName){
		if (dbManager == null){
			// Instantiate and Initialization of DBManager
			dbManager = new DBManager(dbName);
			dbManager.DBStorage = new StorageImpl();
			dbManager.indexHelper = new IndexHelperImpl();
			dbManager.Locker = new DbLocker();
			if(!new File(dbName).isFile()) {
				System.out.println("File "+DBManager.getDBName()+" doesn't exist\nWould you like to create a new one now?(Y/N)");
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String is_YorN= null;
				try {
					is_YorN = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (is_YorN.toLowerCase().equals("y")){
					dbManager.readDatabase();
				}
				else dbManager=null;
			}
			else dbManager.readDatabase();
		}
		
		return dbManager;
	}
	public static DBManager getInstance(){return getInstance("cs542.db");}
	public byte[] getData() {return data;}
	public void setData(byte[] database) {this.data = database;}
	public void readDatabase(){
		//Read the database and upload the data into memory
		byte[] metadata;
		try{
			data = DBStorage.readData(DB_NAME);
			logger.info("Data read in memory");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to read Data into memory");
		}
		try{
			metadata = DBStorage.readMetaData(DB_NAME);
			clusteredIndexes = indexHelper.bytesToIndex(metadata);
			tabMetadata=indexHelper.bytesToTabMeta(metadata);
			unclusteredIndexes = indexHelper.bytesToHashtab(metadata);
			indexToSize();
			logger.info("Free Space left is:" + (DATA_SIZE - DATA_USED));
			logger.info("Free Meta Space left is:" + (METADATA_SIZE - METADATA_USED));
			logger.info("Metadata read in Memory");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to read MataData into memory");
		}
	}
	//*******three main methods*********
	public void Put(int key, byte[] data) {
		/**
		 * In order to avoid during saving period rebooting, we save the data file first and then save the metadata.
		 * Saving process:
		 * 		1. There is only one thread to save data. The saving thread will occupy the data file. Other threads
		 * 			including reading threads will wait until the saving thread complete.
		 * 		2. Locate the saving index:  call the method of findFreeSpaceIndex
		 * 		3. Save the data to data part.
		 * 		4. Save the clusteredIndexes information to metadata part.
		 * 		5. Update the metadata buffer in memory
		 * 
		 * Params:  key -- data key
		 * 			data -- data information
		 *
		 */ 
		try {
			Locker.writeLock();			
			logger.info("Attempting to put key: " + key + "to database");
            //if key already exists in the database, update it by removing it first.
			if (clusteredIndexes.containsKey(key)){
				Remove(key);
			}
			// if database is going to be out of volume, block the put attempt.
			if (data.length + get_DATA_USED() > DATA_SIZE) {
				System.out.println("Not enough data space left. Put Attempt with key "
					+ key + " Failed.");
				return;
			}
					// Getting the index list of free space in data array;
					List<Pair<Integer,Integer>> index_pairs = indexHelper.findFreeSpaceIndex(data.length);
					indexHelper.splitDataBasedOnIndex(data, index_pairs);
					// Updating the Index map. If the metadata is out of volume, block the putting attempt.
					Index tmpindex = new Index();
					tmpindex.setKey(key);
					tmpindex.setIndexes(index_pairs);
					int indexsize = indexHelper.getIndexSize(index_pairs);
					if (indexsize + METADATA_USED > METADATA_SIZE) {
						System.out.println("Not enough metadata space left. Put Attempt with key "
					+ key + " Failed.");
						return;
					}
					clusteredIndexes.put(key, tmpindex);
					set_INDEXES_USED(get_INDEXES_USED() + indexsize);
					logger.info("Metadata buffer updated");
					
					// Writing the database onto the disk
					
					DBStorage.writeData(DB_NAME, this.data);
			set_DATA_USED(get_DATA_USED() + data.length);
			System.out.println("Data related to key is " + key + ", and size is " + data.length + " have written to " + DB_NAME);

					DBStorage.writeMetaData(DB_NAME, dbManager);
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
			if (clusteredIndexes.containsKey(key)) {
				List<Pair<Integer, Integer>> index = clusteredIndexes.get(key).getIndexes();
				//extracting the mapped data from the data in memory;
				int start = 0;
				for (Pair<Integer, Integer> p : index) {
					System.arraycopy(data, p.getLeft(), databuffer, start, p.getRight());
					start += p.getRight();
				}
				returndata = new byte[start];
				System.arraycopy(databuffer, 0, returndata, 0, start);
				logger.info("Data with key " + key + " is " + Arrays.toString(returndata));
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
			if (!clusteredIndexes.containsKey(key)) {
				System.out.println("No data with key " + key + " exists in database.Failed to remove.");
			} else {
				// Removing the key in the metadata buffer and update the metadata file
				int[] tmp = getPairSize(clusteredIndexes.get(key));
				clusteredIndexes.remove(key);
				this.set_DATA_USED(get_DATA_USED() - tmp[0]);
				this.set_INDEXES_USED(get_INDEXES_USED() - tmp[1]);
				logger.info("Metadata buffer updated");
				DBStorage.writeMetaData(DB_NAME, dbManager);
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

	//retrieve attribute value according to the rid and attribute name
	public Object getAttribute(int key, String Attr_name){
		/**
		 * Now we assume only one table so tid is always 0;
		 * Need to be modified in the future.
		 */
		Object returnObj=null;
		byte[] record=Get(key);
		List<Pair> l=tabMetadata.get(0);
		int type=-1,length=0,offset=0;
		for(int i=1;i<l.size();i++){
			Pair p= (Pair) l.get(i).getRight();
			offset+=length;
			type= (int) p.getLeft();
			length= (int) p.getRight();
			if(l.get(i).getLeft()==Attr_name)break;
		}
		byte[] tmp= new byte[length];
		if(type==0){
			System.arraycopy(record,offset,tmp,0,length);
			returnObj=IndexHelperImpl.byteToInt(tmp,0);
		}
		if(type==1){
			System.arraycopy(record,offset,tmp,0,length);
			try {
				returnObj=new String(tmp,"UTF-8").trim();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return returnObj;
	}
	
	private void indexToSize() {
		int indexSize = 0;
		int dataSize =0;
		int[] tmp;
		for (Map.Entry<Integer, Index> m : clusteredIndexes.entrySet()) {
			tmp = getPairSize (m.getValue());
			dataSize += tmp[0];
			indexSize += tmp[1];
		}
		this.set_DATA_USED(dataSize);
		this.set_INDEXES_USED(indexSize);
	}
	
	private static int[] getPairSize(Index index) {
		int[] result = new int[2];
		result[1] += Index.getReservedSize() + 1 + Index.getKeySize()+
				2 * Integer.BYTES * index.getIndexes().size();
		for (Pair<Integer, Integer> p : index.getIndexes()) {
			result[0] += p.getRight();
		}
		return result;
	}

	public Map<Integer, Index> getIndexBuffer() {
		return clusteredIndexes;
	}

	public void createTabMete(String tableName,List<Pair> attr){
		List<Pair> pairs = new ArrayList<>();
		//generate tid
		int tid=0;
		while(tabMetadata.keySet().contains(tid))
			tid++;
		pairs.add(new Pair<>(tid,tableName));
		pairs.addAll(attr);
		tabMetadata.put(tid, pairs);
	}

	public Map<Integer,List<Pair>> getTabMeta(){return tabMetadata;}

	public Map getUnclstrIndex(){return unclusteredIndexes;}

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
		set_METADATA_USED();
	}

	public void set_METADATA_USED(){
		METADATA_USED=indexHelper.hastabToBytes(unclusteredIndexes).length+
				indexHelper.indexToBytes(clusteredIndexes).length+
				indexHelper.tabMetaToBytes(tabMetadata).length;
		}

	public int get_METADATA_USED(){return METADATA_USED;}
	
	public int getFreeSpace() {
		return DATA_SIZE - DATA_USED;
	}
	
	public void clear() {
		// for clearing the database
		try {
			Locker.writeLock();
			//Setting the metadata buffer in memory to an empty Hashtable
			tabMetadata=new Hashtable<>();
			clusteredIndexes=new Hashtable<>();
			unclusteredIndexes=new Hashtable<>();
			logger.info("Clear : Metadata buffer updated");
			set_METADATA_USED();
			DBStorage.writeMetaData(DB_NAME, dbManager);
			logger.info("Metadata updated on disk");
			set_DATA_USED(0);
			tabMetadata.clear();
			//System.out.println("Database Cleared!");
			logger.info("Current Free Space is " + this.getFreeSpace());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Locker.writeUnlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public  void  ReadFile(String Filepath, int Tabid) {
		byte[] bytedata = null;
		BufferedReader br = null;
		String line = "";
		String sep = "@"; //use | as separator
		List<Pair> schema = tabMetadata.get(Tabid);
		int[] AttrType = new int[schema.size() - 1];
		int[] AttrLength = new int[schema.size() - 1];
		for (int i = 1; i< schema.size(); i ++){
			Pair<String,Pair<Integer,Integer>> p = schema.get(i);
			AttrType[i-1] = p.getRight().getLeft();
			AttrLength[i-1] = p.getRight().getRight();
		}
		try{
			br = new BufferedReader(new FileReader(Filepath));
			int i = 1;
			while ((line = br.readLine()) != null) {
				String[] record = line.split(sep);
				for (int j = 0; j < record.length; j ++){
					if (AttrType[j] == 1) {
						if (AttrLength[j] > record[j].length()){
							for (; AttrLength[j]>record[j].length();){
								record[j] = record[j] +" ";
							}
						}else{
							System.out.println("Attribute Size exceeded.");
						}
						if (bytedata == null) bytedata = record[j].getBytes();
						else bytedata = concat(bytedata, record[j].getBytes());
					}
					else{
						int v = Integer.parseInt(record[j]);
						if (bytedata == null) bytedata = IndexHelperImpl.intToByte(v);
						else bytedata = concat(bytedata, IndexHelperImpl.intToByte(v));
					}
				}
				this.Put(i, bytedata);
				i++;
			}
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	System.out.println("Reading" + Filepath + " Done");
	}
	
	private byte[] concat(byte[] a, byte[] b) {
		   int aLen = a.length;
		   int bLen = b.length;
		   byte[] c= new byte[aLen+bLen];
		   System.arraycopy(a, 0, c, 0, aLen);
		   System.arraycopy(b, 0, c, aLen, bLen);
		   return c;
		}

}
