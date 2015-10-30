package project;

import java.io.FileNotFoundException;
import java.io.IOException;
/**
 * @title: Storage data management interface
 * @author wangqian
 *
 */

public interface Storage {
	/**
	 * The size of a block
	 */
	public static final int BLOCK_SIZE = 128;
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
	 * @throws Exception 
	 */
	public void writeMetaData(String fileName, DBManager dbm) throws Exception;
	
	/**
	 * read indexes from metadata
	 * @param fileName
	 * @return index list
	 * @throws IOException 
	 */
	public byte[] readMetaData(String fileName) throws IOException;
}
