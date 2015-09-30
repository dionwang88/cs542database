package test;

import java.util.Arrays;

import project.DBManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class DBManagerTest {
	public static void main(String[] args){
		DBManager dbmanager = DBManager.getInstance();
		Logger logger = (Logger) LogManager.getLogger();
		logger.error("ABC");
		dbmanager.clear();
		byte[] data = new byte[512];
		for(int i=0;i<data.length;i++){
			data[i] = (byte) ((i)%127);
		}
		for (int key = 0; key < 8; key++) {
			dbmanager.Put(key, data);
			byte[] mydata = dbmanager.Get(key);
			System.out.println(Arrays.toString(data).equals(Arrays.toString(mydata)));
		}
			System.out.println("Current used space is " + dbmanager.get_DATA_USED());

}
}
