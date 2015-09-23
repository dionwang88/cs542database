package project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * @title: Storage data management interface
 * @author wangqian
 *
 */

public interface Storage {
	
	// The size of data file
	public static final int DATA_SIZE = 4096000;
	// The size of metadata file
	public static final int METADATA_SIZE = 1024000;
	
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
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	public byte[] readData(String fileName) throws IOException;

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
