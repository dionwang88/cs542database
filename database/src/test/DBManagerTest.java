package test;

import java.util.ArrayList;
import java.util.List;

import project.DBManager;
import project.Pair;
;
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
		int[] type = {1,0,1,1,1,1,1,1,1,0};
		List<Pair> attrs = new ArrayList<>();
		for (int i = 0; i < type.length; i++){
			int length = 0;
			if (type[i] == 1) length = 80;
			else length = 4;
			attrs.add(new Pair(AttrNames[i],new Pair(type[i],length)));
		}
		dbmanager.clear();
		dbmanager.createTab("Movies", attrs);
		dbmanager.ReadFile("movies.txt", 0);
		System.out.println(dbmanager.getAttribute(2,"Title"));
		ArrayList<String> a = new ArrayList<>();
		a.add("Year");
		dbmanager.CreateIndex(a);

	}
}
