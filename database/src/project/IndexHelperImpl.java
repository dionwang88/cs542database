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
	
	/**
	 * When load indexes from metadata, save indexes into indexBuffer object.
	 */
	private Map<Integer, List<Index>> indexBuffer = null;

	public Map<Integer, List<Index>> getIndexBuffer() {
		return indexBuffer;
	}
	public void setIndexBuffer(Map<Integer, List<Index>> indexBuffer) {
		this.indexBuffer = indexBuffer;
	}
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
	public List<Pair<Integer,Integer>> findFreeSpaceIndex(int size) {
		/**
		 * find the indexes list of free spaces based on the data size get all free space based on the delete sign and 
		 * amount blocks to find some enough space to save the data.
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
	 * 1. An index start sign
	 * 2. Key
	 * 3. The index in the data array
	 * 4. The amount of bytes of this index in the data array
	 */
	@Override
	public Map<Integer, List<Index>> bytesToIndex(byte[] metadata) {
		return null;
	}
	
	@Override
	public Map<Integer, List<Index>> getIndexesBuffer() {
		return null;
	}
	
	@Override
	public void addIndex(Index index) {
		
	}
	
	@Override
	public void removeIndex(Integer Key) {
		
	}
}
