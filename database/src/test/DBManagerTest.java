package test;

import java.util.Arrays;

import project.DBManager;
import project.IndexHelperImpl;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class DBManagerTest {
	public static void main(String[] args){
		DBManager dbmanager = DBManager.getInstance();
		Logger logger = (Logger) LogManager.getLogger();
		byte[] data = new byte[256 * 1000];
		for(int i=0;i<data.length;i++){
			data[i] = (byte) ((i)%127);
		}
			dbmanager.Put(0, data);
			System.out.println("Current used space is " + dbmanager.get_DATA_USED());
			System.out.println("Current used meta space is " + dbmanager.get_INDEXES_USED());
			System.out.println("Now loading from disk.");
			dbmanager.readDatabase();
			byte[] mydata = dbmanager.Get(0);
			System.out.println(dbmanager.getIndexBuffer());
	

}
}
