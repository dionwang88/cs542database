package project;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public interface IndexHelper {
	/**
	 * find the indexes list of free spaces based on the data size get all free space based on the delete sign and 
		 * amount blocks to find some enough space to save the data.
	 * @param size : the size of data that to be saved
	 * @return The indexes list of data file
	 */
	public List<Integer> findFreeSpaceIndex(int size);
	
	/**
	 * This method is to split the original data into pieces based on the findFreeSpaceIndex method in order to save a big data
	 * @param data: the data to be saved
	 * @param indexes: the indexes indicate the free space can be used to save the data
	 * @return the list of pieces of data. Key is the index, value is the data array
	 */
	public List<Map<Integer,byte[]>> splitDataBasedOnIndex(byte[] data, List<Integer> indexes);
	
	/**
	 * convert the index Hashtable to byte array
	 * @param indexes
	 * @return the meta data byte array
	 */
	public byte[] indexToBytes(Hashtable<Integer, Index> indexes);
	
	/**
	 * convert the meta data to the index Hashtable
	 * @param metadata
	 * @return The Hashtable of indexes, key is the index key, value is the index object
	 */
	public Hashtable<Integer, Index> bytesToIndex(byte[] metadata);
}
