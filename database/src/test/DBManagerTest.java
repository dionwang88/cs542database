package test;

import java.util.Arrays;

import project.DBManager;
import project.IndexHelperImpl;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class DBManagerTest {
	public static void main(String[] args){
		DBManager dbmanager = DBManager.getInstance();
		byte[] data = new byte[256 * 1000];
		for(int i=0;i<data.length;i++){
			data[i] = (byte) ((i)%127);
		}
		for (int key = 0; key < 16; key ++){
			dbmanager.Put(key, data);
			byte[] mydata = dbmanager.Get(0);
		}
			System.out.println("Now loading.");
		dbmanager.readDatabase();

}
}
