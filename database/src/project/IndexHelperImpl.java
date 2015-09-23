package project;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;


/**
 * This class provides the methods of how to manipulate the index
 * @author wangqian
 *
 */
public class IndexHelperImpl implements IndexHelper {
	Logger logger = (Logger) LogManager.getLogger();
	
	private static IndexHelperImpl indexHelper = null;

	protected IndexHelperImpl() {
		logger.info("Create IndexHelper Object.");
	}
	/**
	 * Singleton Object
	 * @return
	 */
	public static IndexHelperImpl getInstance(){
		if(indexHelper == null){
			indexHelper = new IndexHelperImpl();
		}
		return indexHelper;
	}

	@Override
	public List<Integer> findFreeSpaceIndex(int size) {
		/**
		 * find the indexes list of free spaces based on the data size get all free space based on the delete sign and 
		 * amount blocks to find some enough space to save the data. For example, if the indexes in meta data are (0,0,1,0,1000), 
		 * (0,1,1,4001,1000), (0,1,1,8001,1000),then we can know that there has a 6000 bytes (1001-4000 and 5001-8000) free space 
		 * between those two data. If the data size is greater than the free space, then return a not enough space error message.
		 */
		return null;
	}

	@Override
	public List<Map<Integer, byte[]>> splitDataBasedOnIndex(byte[] data, List<Integer> indexes) {
		/**
		 * Split the data into several parts based on the free indexes list
		 */
		return null;
	}
	@Override
	public byte[] indexToBytes(Map<Integer, Index> indexes) {
		return null;
	}
	
	/**
	 * An index start sign
	 * 1. Delete Sign
	 * 2. Key
	 * 3. Total number of the indexes
	 * 4. The index in the data array
	 * 5. The amount of bytes of this index in the data array
	 */
	@Override
	public Map<Integer, Index> bytesToIndex(byte[] metadata) {
		return null;
	}
	
	public static void main(String[] args){
		IndexHelperImpl ih = new IndexHelperImpl();
		
	}
}
