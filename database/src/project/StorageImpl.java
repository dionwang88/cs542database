package project;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;

import com.sun.corba.se.spi.ior.WriteContents;
import com.sun.deploy.util.ArrayUtil;
import com.sun.tools.javac.util.ArrayUtils;
import org.apache.commons.io.IOUtils;

import static project.Storage.DATA_SIZE;

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
		byte[] data = readOutDataBase(fileName);
		return Arrays.copyOfRange(data,0,DATA_SIZE);
	}
	
	@Override
	public void writeData(String fileName, byte[] data) throws Exception {
		// Verify the data size cannot exceed the DATA_SIZE
		if(data.length > DATA_SIZE) 
			throw new Exception("The data size is exceed the requirement!");
		writeIntoDataBase(fileName,data,true);
	}

	@Override
	public void writeMetaData(String fileName, byte[] metadata) throws Exception {
		// Verify the data size cannot exceed the METADATA_SIZE
		if (metadata.length > METADATA_SIZE)
			throw new Exception("The metadata size is exceed the requirement!");
		writeIntoDataBase(fileName,metadata,false);
	}

	@Override
	/**
	 * 1. An index start sign
	 * 2. Key
	 * 3. The index in the data array
	 * 4. The amount of bytes of this index in the data array
	 */
	public byte[] readMetaData(String fileName) throws IOException {
		byte[] data = readOutDataBase(fileName);
		return Arrays.copyOfRange(data,DATA_SIZE,data.length);
	}

	private byte[] readOutDataBase(String fileName) throws IOException{
		InputStream inputstream = null;
		byte[] out;
		try {
			inputstream = new FileInputStream(fileName);
			out=IOUtils.toByteArray(inputstream);
		} catch (FileNotFoundException e) {
			System.out.println("File "+DBManager.getDBName()+" doesn't exist\ncreating a new file now.");
			FileOutputStream fos=null;
			try{
				fos = new FileOutputStream(fileName);
				byte[] empty=new byte[Storage.DATA_SIZE];
				for (int i = 0; i <empty.length;i++) {
					empty[i]=0;
				}
				fos.write(empty);
			}catch(Exception ee){
				ee.printStackTrace();
			}finally{
				if(fos != null) fos.close();
			}
			out=readOutDataBase(fileName);
		}
		return out;
	}

	private void writeIntoDataBase(String fileName,byte[] writeContent, boolean isData) throws IOException{
		byte[] out;
		if(isData){
			out = readOutDataBase(fileName);
			for (int i = 0; i < writeContent.length; i++)
				out[i]=writeContent[i];
		}
		else{
			out=new byte[DATA_SIZE+writeContent.length];
			byte[] data=readData(fileName);
			System.arraycopy(data,0,out,0,data.length);
			System.arraycopy(writeContent,0,out,data.length,writeContent.length);
		}
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(fileName);
			fos.write(out);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(fos != null) fos.close();
		}
	}

}
