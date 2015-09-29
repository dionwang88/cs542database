package test;

import java.io.IOException;

import project.Storage;
import project.StorageImpl;

public class TestStorage {
	public static void testWriteData(){
		byte[] data = new byte[Storage.DATA_SIZE];
		for(int i=0;i<data.length;i++){
			data[i] = 111;
		}
		Storage storage = new StorageImpl();
		try {
			storage.writeData("data.db", data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testReadData(){
		Storage storage = new StorageImpl();
		try {
			byte[] d = storage.readData("data.db");
			System.out.println(d[5]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		TestStorage.testReadData();
	}
}
