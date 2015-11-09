package test;

import java.util.ArrayList;

import project.Condition;
import project.DBManager;
import project.DBTool;

public class DBManagerTest {
	public static void main(String[] args) {
		DBManager dbManager=DBManager.getInstance();

		try {
			dbManager.printQuery(DBTool.getTabID(dbManager,"movies"), dbManager.tabProject("year"), new Condition(""));
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayList<String> a = new ArrayList<>();

		dbManager.createIndex("movies","tiTle,year");

        System.out.println(dbManager.getTabMeta());
		System.out.println(dbManager.getAttrIndex());
        System.out.println(true^true);

	}
}
