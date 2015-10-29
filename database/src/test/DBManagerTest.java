package test;

import java.util.ArrayList;
import java.util.List;

import project.DBManager;
import project.Pair;

public class DBManagerTest {
	public static void main(String[] args) {
		DBManager dbmanager = DBManager.getInstance();
		/**
		 * 

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
		*/
		String[] AttrNames = {"Title", "Year", "Format", "Genre", "Director", "Writer", "Country", "Studio", "Price", "Catalog No"};
		int[] type = {1,0,1,1,1,1,1,1,0,0};
		List<Pair> attrs = new ArrayList<>();
		for (int i = 0; i < type.length; i++){
			int length = 0;
			if (type[i] == 0) length = 30;
			else length = 99999;
			attrs.add(new Pair(AttrNames[i],new Pair(type[i],length)));
		}
		dbmanager.createTabMete("Movies", attrs);
		dbmanager.ReadFile("/Users/Xiang/Documents/GitHub/cs542database/database/movies.txt", 1);
	}
}
