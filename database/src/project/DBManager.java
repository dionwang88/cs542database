package project;

import java.util.Hashtable;

/**
 * DBManager is to manager database, including manage storage data and the indexes
 * @author wangqian
 *
 */
public class DBManager {
	
	private static DBManager dbManager = null;
	
	// database is to be contain the data
	private byte[] data;
	 
	/**
	 * indexes is to be contain the indexes in the metadata
	 * Key is the index key
	 * List is the index of that key
	 */
	private Hashtable<Integer, Index> indexes;
	
	protected DBManager(){
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

	public void readDatabase() {
		/**
		 * Read the database and upload the data into memory
		 */
		
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
	
}
