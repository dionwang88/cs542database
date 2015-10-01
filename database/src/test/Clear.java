package test;

import project.DBManager;

public class Clear {

	public static void main(String[] args) {
		DBManager dbmanager = DBManager.getInstance();
		dbmanager.clear();
	}
}
