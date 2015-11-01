package test;

import java.util.ArrayList;
import java.util.List;
import project.Condition;
import project.DBManager;
import project.Pair;

public class DBManagerTest {
	public static void main(String[] args) {
		DBManager dbManager=DBManager.getInstance();

		dbManager.printQuery("movies", dbManager.tabProject("Year"), new Condition(""));

		ArrayList<String> a = new ArrayList<>();
		//a.add("title");
		a.add("Year");

		dbManager.CreateIndex(a);

		System.out.println(dbManager.getAttrIndex());






	}
}
