package project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;


/**
 * This class provides the methods of how to manipulate the index
 * @author wangqian
 *
 */
public class IndexHelperImpl implements IndexHelper {
	
	Logger logger = (Logger) LogManager.getLogger();
	
	private DBManager dbmanager = DBManager.getInstance();
	
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
	/**
	 * find the indexes list of free spaces based on the data size get all free space based on the delete sign and 
	 * amount blocks to find some enough space to save the data.
	 * In pair object, left is start index, right is the size
	 */
	@Override
	public List<Pair<Integer,Integer>> findFreeSpaceIndex(int size) {	
		// Temp variable to record the left size of the data
		int left_size = size;
		List<Pair<Integer, Integer>> list_freeSpace = new ArrayList<Pair<Integer, Integer>>();
		List<Integer> index_list = this.getSortedIndexList();

		if(index_list.size() == 0){ //if the index has no pair index
			Pair<Integer,Integer> p = new Pair<Integer,Integer>(0, size); 
			list_freeSpace.add(p);
		}else if(index_list.size() == 2){// if the index list has only one pair index
			Pair<Integer,Integer> p = new Pair<Integer,Integer>(index_list.get(1)+1, size); 
			list_freeSpace.add(p);
		}else{ // if the index list has more than one pair index
			int i = 2;
			int second = index_list.get(1) + 1; // the next position to the end index of first pair index 
			while( i < index_list.size()){
				int third = index_list.get(i);
				// the length of free space
				int length = third - second;
				if(length != 0){// If between two pairs have free space
					if(length >= left_size){// if free space can contain the data
						Pair<Integer,Integer> p = new Pair<Integer,Integer>(second, left_size); 
						list_freeSpace.add(p);
						return list_freeSpace;
					}else{// if the free space cannot contain the data, then will to find next free space
						Pair<Integer,Integer> p = new Pair<Integer,Integer>(second, length); 
						list_freeSpace.add(p);
						left_size = left_size - length;
					}
				}
				i = i + 2; // add 2 to point the next index start point
				second = index_list.get(i - 1) + 1;
			}
		}
		
		return list_freeSpace;
	}
	/**
	 * Transform the index buffer to the Map type sorted by start index
	 * @return
	 */
	public List<Integer> getSortedIndexList(){
		// This list is to save all the index pairs(start, length).
		List<Integer> index_list = new ArrayList<Integer>();
		// Get the index buffer object
		Map<Integer, Index> indexBuffer = this.dbmanager.getIndexBuffer();
		// To get all the index list
		for (Entry<Integer, Index> entry : indexBuffer.entrySet()) {
			Index index = entry.getValue();
			List<Pair<Integer, Integer>> lst_p = index.getIndexes();
			for(Pair<Integer, Integer> pair : lst_p){
				//transform the index pair to start, end
				index_list.add(pair.getLeft());
				index_list.add(pair.getLeft()+pair.getRight());
			}
		}
		// Sort the index list
		Collections.sort(index_list);
		return index_list;
	}

	@Override
	public List<Map<Integer, byte[]>> splitDataBasedOnIndex(byte[] data_to_save, List<Pair<Integer,Integer>> indexes) {
		/**
		 * Split the data into several parts based on the free indexes list
		 */
		byte[] db_data = dbmanager.getData();
		int index_in_data_to_save = 0;
		
		for (Pair<Integer, Integer> pair : indexes){
			int start = pair.getLeft();
			int length = pair.getRight();
			while(index_in_data_to_save < length){
				db_data[start] = data_to_save[index_in_data_to_save];
				start = start + 1;
			}
			index_in_data_to_save = index_in_data_to_save + length;
		}
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
	public Map<Integer, Index> bytesToIndex(byte[] metadata) {
		return null;
	}
	
	@Override
	public void addIndex(Index index) {
		
	}
	
	@Override
	public void removeIndex(Integer Key) {
		
	}
	
	@Override
	public void updateIndex(Index index) {
		
	}
	@Override
	public Map<Integer, Index> getIndexesBuffer() {
		// TODO Auto-generated method stub
		return null;
	}
}
