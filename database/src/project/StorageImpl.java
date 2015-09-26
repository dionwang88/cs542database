package project;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * Implementation of the Storage Interface.
 * Data Structure: 	first is 4m space is to save the real data, second is 1m space is to save the metadata information.
 *
 * Metadatafile:    first byte is the sign of delete (1 is deleted, 0 is not deleted); 
 * 					second byte is the key;
 * 					third byte is the total number of the indexes if the data cannot save in a single consecutive space
 * 					fourth byte is the index of the data;
 * 					fifth byte is the amount of bytes
 * 					if the data have to save in several parts, then there will have other indexes and amount of bytes information
 * 					for example: (0,1,1,10,1024) indicates this index metadata is a valid, data key is 1, there has only 1 index pair
 * 					, index in data file is 10, data size is 1024.
 * Data File:		Total size is 4m bytes.
 *
 * 
 * @author wangqian
 *
 */
public class StorageImpl implements Storage {

	public StorageImpl(){

	}
	
	@Override
	public byte[] readData(String fileName) throws IOException {
		InputStream inputstream = new FileInputStream(fileName);
		byte[] data = IOUtils.toByteArray(inputstream);
		return data;
	}
	
	@Override
	public void writeData(String fileName, byte[] data) throws Exception {
		// Verify the data size cannot exceed the DATA_SIZE
		if(data.length > DATA_SIZE) 
			throw new Exception("The data size is exceed the requirement!");
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(fileName);
			fos.write(data);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(fos != null) fos.close();
		}
	}

	@Override
	public void writeMetaData(String fileName, byte[] metadata) throws Exception {
		// Verify the data size cannot exceed the METADATA_SIZE
		if (metadata.length > METADATA_SIZE)
			throw new Exception("The metadata size is exceed the requirement!");
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(fileName);
			fos.write(metadata);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(fos != null) fos.close();
		}
	}

	@Override
	/**
	 * 1. An index start sign
	 * 2. Key
	 * 3. The index in the data array
	 * 4. The amount of bytes of this index in the data array
	 */
	public byte[] readMetaData(String fileName) throws IOException {
		InputStream inputstream = new FileInputStream(fileName);
		byte[] data = IOUtils.toByteArray(inputstream);
		
		return data;
	}

}
