package project;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * DBManager is to manager database, including manage storage data and the indexes
 * @author wangqian
 *
 */
public class DBManager {
	
	private String DBDATA_NAME = "cs542.db";
	private String METADATA_NAME = "cs542.meta";
	
	/**
	 * The used index size in metadata file.
	 */
	private int INDEX_USED = 0;
	/**
	 * The used data size in data file.
	 */
	private int DATA_USED = 0;
	
	private static DBManager dbManager = null;
	
	// database is to be contain the data
	private byte[] data;
	/**
	 * When load indexes from metadata, save indexes into indexBuffer object.
	 */
	private Map<Integer, Index> indexBuffer = null;

	public Map<Integer, Index> getIndexBuffer() {
		return indexBuffer;
	}
	public void setIndexBuffer(Map<Integer, Index> indexBuffer) {
		this.indexBuffer = indexBuffer;
	}
	 
	/**
	 * indexes is to be contain the indexes in the metadata
	 * Key is the index key
	 * List is the index of that key
	 */
	private Hashtable<Integer, Index> indexes;
	
	protected DBManager(){
		this.loadDatabase();
	}
	
	/**
	 * Singleton Object
	 * @return
	 */
	public static DBManager getInstance(){
		if (dbManager == null){
			dbManager = new DBManager();
		}
		
		return dbManager;
	}
	
	/**
	 * Before save the data, should validate the size of the data, should not exceed the size of data file, and should not
	 * exceed the free space of data file.
	 * @param key
	 * @param data
	 */
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
		
	}

	public byte[] Get(int key) {
		return null;
	}

	public void Remove(int key) {

	}

	public void loadDatabase() {
		/**
		 * Read the database and upload the data and indexes into memory
		 * When finish loading data, should set the DATA_USED variable
		 */
		Storage storage = new StorageImpl();
		try {
			this.setData(storage.readData(DBDATA_NAME));
//			this.setIndexBuffer(indexBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] database) {
		this.data = database;
	}

	public Hashtable<Integer, Index> getIndexes() {
		return indexes;
	}

	public void setIndexes(Hashtable<Integer, Index> indexes) {
		this.indexes = indexes;
	}

	public int getINDEX_USED() {
		return INDEX_USED;
	}

	public void setINDEX_USED(int iNDEX_USED) {
		INDEX_USED = iNDEX_USED;
	}

	public int getDATA_USED() {
		return DATA_USED;
	}

	public void setDATA_USED(int dATA_USED) {
		DATA_USED = dATA_USED;
	}
	
}
