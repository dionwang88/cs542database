package test;

import project.DBManager;
import project.Storage;

public class DBManagerTest {
	public static void main(String[] args){
		byte[] data = new byte[1];
		for(int i=0;i<data.length;i++){
			data[i] = (byte) (i%127);
		}
		int key = 30;
		DBManager dbmanager = DBManager.getInstance();
		dbmanager.Put(key, data);
		
		byte[] mydata = dbmanager.Get(key);
		System.out.println(mydata.length);
	}
}
