package project;

import java.util.List;
import java.util.Map;

public interface IndexHelper {
	/**
	 * The index start sign in the metad ata file
	 */
	byte INDEX_START_SIGN = -1;
	byte TAB_START_SIGN = -2;
	byte TAB_META_RESERVED=3;
	
	/**
	 * find the indexes list of free spaces based on the data size get all free space based on the delete sign and 
		 * amount blocks to find some enough space to save the data.
	 * @param size : the size of data that to be saved
	 * @return The indexes list of data file
	 * @throws Exception 
	 */
	List<Pair<Integer, Integer>> findFreeSpaceIndex(int size) ;
	
	/**
	 * Get the index buffer in Map type
	 * key is the start index of local part on data file
	 * value is the end index of local part on data file
	 * @return
	 */
	List<Integer> getSortedIndexList();
	/**
	 * This method is to split the original data into pieces based on the findFreeSpaceIndex method in order to save a big data
	 * @param data: the data to be saved
	 * @param indexes: the indexes indicate the free space can be used to save the data
	 * @return the list of pieces of data. Key is the index, value is the data array
	 */
	void splitDataBasedOnIndex(byte[] data_to_save, List<Pair<Integer,Integer>> indexes);
	
	/**
	 * convert the index Hashtable to byte array
	 * @param indexes
	 * @return the meta data byte array
	 */
	byte[] indexToBytes(Map<Integer, Index> indexes);
	
	/**
	 * convert the meta data to the index Hashtable
	 * @param metadata
	 * @return The Hashtable of indexes, key is the index key, value is the index object
	 */
	Map<Integer, Index> bytesToIndex(byte[] metadata);
	
	/**
	 * Get the index buffer from memory
	 * @return
	 */
	Map<Integer, Index> getIndexesBuffer();
	
	int getIndexSize(List<Pair<Integer,Integer>> pairs_list);

	byte[] tabMatatoBytes(Map<String,List<Pair>> tabMetadata);
	Map<String,List<Pair>>  bytesToTabMeta(byte[] metadata);
	/**
	 * Add a new index into indexes buffer
	 */
	void addIndex(Index index);
	/**
	 * Update an index into indexes buffer
	 * @param index
	 */
	void updateIndex(Index index);
	/**
	 * Remove an index from indexes buffer
	 * @param Key
	 */
	void removeIndex(Integer Key);
}
