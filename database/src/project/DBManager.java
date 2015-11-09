package project;

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

/**
 * DBManager is to manager database, including manage storage data and the clusteredIndex
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
	 * clusteredIndex is to be contain the clusteredIndex in the metadata
	 * Key is the index key
	 * List is the index of that key
	 */
	private Map<Integer, Index> clusteredIndex;
	/**
	 * table id
	 * Index indexes<int tid, Index physical address for this attribute> for certain table
	 */
	private Map<Integer,Map<String, AttrIndex>> attrIndexes;
	/**
	 * table id
	 * meta info. for certain table
	 */
	private Map<Integer,List<Pair>> tabMetadata;

	//Locker controls the concurrency of the database.
	private DbLocker Locker;
	private Storage DBStorage;
	private IndexHelper indexHelper;

	//---------------Method------------------------
	private DBManager(String dbName) {DB_NAME=dbName;}
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
				System.out.println("File "+DBManager.getDBName()
						+" doesn't exist\nWould you like to create a new one now?(Y/N)");
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String is_YorN;
				try {
					is_YorN = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Fail to create database!");
					return null;
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

	public int get_DATA_USED() {return DATA_USED;}
	public void set_DATA_USED(int size) {DATA_USED = size;}
	public int get_INDEXES_USED() {return INDEXES_USED;}
	public void set_INDEXES_USED(int size) {
		INDEXES_USED = size;
		set_METADATA_USED();
	}
	public void set_METADATA_USED(){
        try {
            METADATA_USED=indexHelper.hastabToBytes(attrIndexes.get(0)).length+
                    indexHelper.indexToBytes(clusteredIndex).length+
                    indexHelper.tabMetaToBytes(tabMetadata).length;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	//public int get_METADATA_USED(){return METADATA_USED;}
	public int getFreeSpace() {return DATA_SIZE - DATA_USED;}

	public byte[] getData() {return data;}
	public void setData(byte[] database) {this.data = database;}
	private static int[] getIndexSize(Index index) {
		int[] result = new int[2];
		result[1] += Index.getReservedSize() + 1 + 1 + Index.getKeySize()+
				2 * Integer.BYTES * index.getIndexList().size();
		for (Pair<Integer, Integer> p : index.getIndexList()) {
			result[0] += p.getRight();
		}
		return result;
	}
	public Map<Integer, Index> getClusteredIndex() {return clusteredIndex;}
	private void indexToSize() {
		int indexSize = 0;
		int dataSize =0;
		int[] tmp;
		for (Map.Entry<Integer, Index> m : clusteredIndex.entrySet()) {
			tmp = getIndexSize(m.getValue());
			dataSize += tmp[0];
			indexSize += tmp[1];
		}
		this.set_DATA_USED(dataSize);
		this.set_INDEXES_USED(indexSize);
	}

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
			clusteredIndex = indexHelper.bytesToIndex(metadata);
			tabMetadata=indexHelper.bytesToTabMeta(metadata);
			attrIndexes = new Hashtable<>();
			attrIndexes=indexHelper.bytesToHashtab(metadata);
			indexToSize();
			logger.info("Free Space left is:" + (DATA_SIZE - DATA_USED));
			logger.info("Free Meta Space left is:" + (METADATA_SIZE - METADATA_USED));
			logger.info("Metadata read in Memory");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to read MataData into memory");
		}
	}
	public void createTab(String tableName, List<Pair> attr){
		List<Pair> pairs = new ArrayList<>();
		//generate tid
		int tid=0;
		while(tabMetadata.keySet().contains(tid))
			tid++;
		pairs.add(new Pair<>(tid,tableName.toLowerCase()));
		pairs.addAll(attr);
		tabMetadata.put(tid, pairs);
		attrIndexes.put(tid, new Hashtable<>());
	}
	public void clear() {
		// for clearing the database
		try {
			Locker.writeLock();
			//Setting the metadata buffer in memory to an empty Hashtable
			tabMetadata=new Hashtable<>();
			clusteredIndex =new Hashtable<>();
			attrIndexes =new Hashtable<>();
			logger.info("Clear : Metadata buffer updated");
			set_METADATA_USED();
			DBStorage.writeMetaData(DB_NAME, dbManager);
			logger.info("Metadata updated on disk");
			set_DATA_USED(0);
			tabMetadata.clear();
			//System.out.println("Database Cleared!");
			logger.info("Current Free Space is " + this.getFreeSpace());
			tabMetadata.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Locker.writeUnlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//*******three main methods*********
	public void Put(int tid,int key, byte[] data) {
		/**
		 * In order to avoid during saving period rebooting, we save the data file first and then save the metadata.
		 * Saving process:
		 * 		1. There is only one thread to save data. The saving thread will occupy the data file. Other threads
		 * 			including reading threads will wait until the saving thread complete.
		 * 		2. Locate the saving index:  call the method of findFreeSpaceIndex
		 * 		3. Save the data to data part.
		 * 		4. Save the clusteredIndex information to metadata part.
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
			if (clusteredIndex.containsKey(key)){
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
			Index tmpIndex = new Index();
			tmpIndex.setKey(key);
			tmpIndex.setTID(tid);
			tmpIndex.setPhysAddrList(index_pairs);
			int indexSize = indexHelper.getIndexSize(index_pairs);
			if (indexSize + METADATA_USED > METADATA_SIZE) {
				System.out.println("Not enough metadata space left. Put Attempt with key "
						+ key + " Failed.");
				return;
			}
			clusteredIndex.put(key, tmpIndex);
			set_INDEXES_USED(get_INDEXES_USED() + indexSize);
			logger.info("Metadata buffer updated");

			// Writing the database onto the disk
			DBStorage.writeData(DB_NAME, this.data);
			set_DATA_USED(get_DATA_USED() + data.length);
			System.out.println("Data related to key is " + key + ", and size is "
					+ data.length + " have written to " + DB_NAME);

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

	public byte[] Get(int tid,int key) {
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
		byte[] dataBuffer = new byte[Storage.DATA_SIZE];
		byte[] returnData = null;
		try {
			Locker.ReadLock();
			logger.info("Attempting to get data mapped to key :" + key);
			if (clusteredIndex.containsKey(key)) {
                if(clusteredIndex.get(key).getTID()!=tid) return null;
				List<Pair<Integer, Integer>> index = clusteredIndex.get(key).getIndexList();
				//extracting the mapped data from the data in memory;
				int start = 0;
				for (Pair<Integer, Integer> p : index) {
					System.arraycopy(data, p.getLeft(), dataBuffer, start, p.getRight());
					start += p.getRight();
				}
				returnData = new byte[start];
				System.arraycopy(dataBuffer, 0, returnData, 0, start);
				logger.info("Data with key " + key + " is " + Arrays.toString(returnData));
			} else {
				System.out.println("No data with key "+ key +" exists in database.");
			}
		} catch (Exception e) {
			System.out.println("Interrupted while reading data");
			e.printStackTrace();
		} finally{
			Locker.ReadUnlock();
		}

		return returnData;
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
			if (!clusteredIndex.containsKey(key)) {
				System.out.println("No data with key " + key + " exists in database.Failed to remove.");
			} else {
				// Removing the key in the metadata buffer and update the metadata file
				int[] tmp = getIndexSize(clusteredIndex.get(key));
				clusteredIndex.remove(key);
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

    public byte[] Get(int key){return Get(0,key);}

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
			if(((String)l.get(i).getLeft()).toLowerCase().equals(Attr_name.toLowerCase())) break;
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

	//fetch the key according to the attributes by using index.
	public List getKeyFromAttr(List<String> AttrNames,List<String> AttrValues) throws Exception {
		int tid=0;
		List res=new ArrayList<>();
		String attrs = "";
		if (AttrNames.size() > 1) {
			for (String s : AttrNames)
				attrs = attrs + "|" + s.toLowerCase();
			attrs +="|";
		}else
			attrs = AttrNames.get(0).toLowerCase();

		if(attrIndexes.get(tid).containsKey(attrs)){
			int hashValue=0;
			for(int i=0;i<AttrValues.size();i++) hashValue+=AttrNames.get(0).hashCode();
			return ((AttrIndex) attrIndexes.get(tid).get(attrs)).get(hashValue);
		}
		else{
			throw new Exception("No Attribute Index!");
		}
	}

	//give table id, the condition and project attributes, print the results
	public void printQuery(int tid,List<String> attrNames,Condition c) throws Exception {

		int queryHashVal=0;
		//not table found
		if(tid==-1){
			System.out.println("No table(s) found");
			return;
		}
		//extract attribute names from condition
		List<String> addedAttrNames=new ArrayList<>();
		try {
			if (c.throwCondition()!=null)
				for(String[] ss:c.throwCondition())
					if(ss.length==4) {
						addedAttrNames.add(ss[1]);
						queryHashVal+=ss[2].hashCode();
					}
		} catch (Exception e) {
			System.out.println("Unclear condition(s)!");
			return;
		}

		//if all the attr are Index or No where-condition
		boolean all_in=!addedAttrNames.isEmpty();
		if(!isAttrIndex((ArrayList<String>) attrNames)) all_in=false;
		//if so
		if(all_in){
			String attrs = "";
			if (attrNames.size() > 1) {
				for (String s : attrNames){
					attrs = attrs + "|" + s.toLowerCase();
				}
				attrs +="|";
			}else{
				attrs = attrNames.get(tid);
			}
			AttrIndex attrIndex= attrIndexes.get(tid).get(attrs);
			List keys=attrIndex.get(queryHashVal);
			for(int i=0;i<keys.size();i++){
				int key= (int) keys.get(i);
				if (!Condition.handleCondition(c.throwCondition(),dbManager,key,tid)) continue;
				boolean isFirst = true;
				System.out.print(key + ": ");
				for (String attrName : attrNames) {
					if (isFirst) {
						System.out.print(getAttribute(key, attrName));
						isFirst = false;
					} else
						System.out.print("|" + getAttribute(key, attrName));
				}
				System.out.print('\n');
			}
		}
		//if nor, or no index on the attribute(s)
		else {
			for (int key : clusteredIndex.keySet()) {
				if (!Condition.handleCondition(c.throwCondition(),dbManager,key,tid)) continue;
				boolean isFirst = true;
				System.out.print(key + ": ");
				for (String attrName : attrNames) {
					if (isFirst) {
						System.out.print(getAttribute(key, attrName));
						isFirst = false;
					} else
						System.out.print("|" + getAttribute(key, attrName));
				}
				System.out.print('\n');
			}
		}
	}
	//do projection
	public List<String> tabProject(String attrNames){
		int tid=0;
		List<String> res = new ArrayList<>();
		if(attrNames.trim().equals("*")){
			for(int i =1;i<tabMetadata.get(tid).size();i++)
				res.add((String) tabMetadata.get(tid).get(i).getLeft());
		}
		else {
			String[] strings = attrNames.toLowerCase().split(",");
			for (String s : strings)
				res.add(s.toLowerCase().trim());
		}
		return res;
	}
	//fetch table metadata
	public Map<Integer,List<Pair>> getTabMeta(){return tabMetadata;}
	//fetch the attribute based on the rid and attribute name
	public Map<Integer,Map<String, AttrIndex>> getAttrIndex(){return attrIndexes;}

	//read csv file
	public void ReadFile(String Filepath, int TabID, String regSep) {
		byte[] byteData;
		BufferedReader br = null;
		String line;
		List<Pair> schema = tabMetadata.get(TabID);
		int[] AttrType = new int[schema.size() - 1];
		int[] AttrLength = new int[schema.size() - 1];
		for (int i = 1; i< schema.size(); i ++){
			Pair p = schema.get(i);
			AttrType[i-1] = (int) ((Pair) p.getRight()).getLeft();
			AttrLength[i-1] =(int) ((Pair) p.getRight()).getRight();
		}
		try{
			br = new BufferedReader(new FileReader(Filepath));
			int i = 1;
			while ((line = br.readLine()) != null) {
				String[] record = line.split(regSep);
				byteData = null;
				for (int j = 0; j < record.length; j ++){
					if (AttrType[j] == 1) {
						if (AttrLength[j] > record[j].length()){
							for (; AttrLength[j]>record[j].length();){
								record[j] = record[j] +" ";
							}
						}else{
							System.out.println("Attribute Size exceeded.");
						}
						if (byteData == null) byteData = record[j].getBytes();
						else byteData = IndexHelperImpl.concat(byteData, record[j].getBytes());
					}
					else{
						int v;
						if(record[j].trim().equals("")) v=0;
						else {
							v = Integer.parseInt(record[j].trim());
						}
							if (byteData == null) byteData = IndexHelperImpl.intToByte(v);
							else byteData = IndexHelperImpl.concat(byteData, IndexHelperImpl.intToByte(v));
					}
				}
				this.Put(0,i++, byteData);
			}
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
		System.out.println("Reading " + Filepath + " is Done");
	}
	public void ReadFile(String Filepath, int TabID){ReadFile(Filepath, TabID, "@");}

	// create index on certain attribute names
	public void createIndex(String tableName,String str_AttrNames){
		int tid=0;

		ArrayList<String> AttrNames = new ArrayList<>();
		String[] strings = str_AttrNames.toLowerCase().split(",");
		for (String s : strings)
			AttrNames.add(s.trim());

		Collections.sort(AttrNames);

		AttrIndex<String> attrindex = new AttrIndex<>(AttrNames);
		String attrs = "";
		if (AttrNames.size() > 1) {
			for (String s : AttrNames){
				attrs = attrs + "|" + s.toLowerCase();
			}
			attrs +="|";
		}else{
			attrs = AttrNames.get(tid).toLowerCase();
		}
		this.attrIndexes.get(tid).put(attrs, attrindex);
		try {
			DBStorage.writeMetaData(DB_NAME,dbManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//attribute has index or not
	private boolean isAttrIndex(ArrayList<String> attrNames){
		int tid=0;
		String attrs = "";
		if (attrNames.size() > 1) {
			for (String s : attrNames){
				attrs = attrs + "|" + s.toLowerCase();
			}
			attrs +="|";
		}else{
			attrs = attrNames.get(tid).toLowerCase();
		}
		return attrIndexes.get(tid).containsKey(attrs);
	}
	//used and may be not useful in the future
	private boolean isAttribute(String attrName){
		int tid=0;
		List<Pair> t_meta=tabMetadata.get(tid);
		for(int i=1;i<t_meta.size();i++) {
			if(((String)tabMetadata.get(tid).get(i).getLeft()).toLowerCase().equals(attrName.toLowerCase()))
				return true;
		}
		return false;
	}
}
