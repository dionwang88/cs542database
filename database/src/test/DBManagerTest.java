package test;

import project.DBManager;

public class DBManagerTest {
	public static void main(String[] args){
		byte[] data = new byte[10];
		for(int i=0;i<data.length;i++){
			data[i] = (byte) (i%127);
		}
		for (int key = 21;key<30;key++){
			DBManager dbmanager = DBManager.getInstance();
			dbmanager.Put(key, data);
			
			byte[] mydata = dbmanager.Get(key);
			System.out.println(mydata.length);
		}
	}
}
