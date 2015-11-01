package test;

import java.util.ArrayList;
import java.util.List;
import project.Condition;
import project.DBManager;
import project.Pair;

public class DBManagerTest {
	public static void main(String[] args) {
		DBManager dbManager=DBManager.getInstance();

		dbManager.printQuery("movies", dbManager.tabProject("year"), new Condition(""));

		ArrayList<String> a = new ArrayList<>();

		dbManager.CreateIndex("tiTle,year");

        System.out.println(dbManager.getTabMeta());
		System.out.println(dbManager.getAttrIndex());
        System.out.println(true^true);

	}
}
