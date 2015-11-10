package test;

import project.DBManager;

/**
 * Created by vincent on 11/9/15.
 */
public class TestMultab {
    public static void main(String[] args){
        DBManager dbmanager = DBManager.getInstance();
        dbmanager.clear();
        TestReadCSV.readcsv("movies",dbmanager);
        TestReadCSV.readcsv("movies2",dbmanager);
        TestReadCSV.readcsv("movies3",dbmanager);
    }
}
