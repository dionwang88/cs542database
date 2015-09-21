package project;

import java.util.List;

/**
 * @title: Storage data management interface
 * @author wangqian
 *
 */

public interface Storage {
	/**
	 * write the data into file
	 * @param fileName: data file name
	 * @param data
	 * @throws Exception 
	 */
	public void writeData(String fileName, byte[] data) throws Exception;
	
	/**
	 * read the data file into memory
	 * @param fileName
	 * @return data list
	 */
	public List<byte[]> readData(String fileName);

	/**
	 * write the index into metadata
	 * @param fileName
	 * @param metadata
	 * @throws Exception 
	 */
	public void writeMetaData(String fileName, byte[] metadata) throws Exception;
	
	/**
	 * read indexes from metadata
	 * @param fileName
	 * @return index list
	 */
	public List<Index> readMetaData(String fileName);
}
