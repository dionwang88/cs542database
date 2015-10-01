package test;

import project.DBManager;

public class DBManagerTest {
	public static void main(String[] args) {
		DBManager dbmanager = DBManager.getInstance();
		byte[] data = new byte[256 * 1000];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) ((i) % 127);
		}
		for (int key = 0; key < 16; key++) {
			dbmanager.Put(key, data);
			System.out.println("Put data key: " + key + ", size:" + data.length);
		}
		for (int key = 0; key < 1; key++) {
			byte[] mydata = dbmanager.Get(0);
			System.out.println("Get data key:" + key + ",size:" + mydata.length);
		}
		dbmanager.readDatabase();
	}
}
