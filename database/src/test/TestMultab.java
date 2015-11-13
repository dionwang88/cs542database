package test;

import project.DBManager;

/**
 * Created by vincent on 11/9/15.
 */
public class TestMultab {
    public static void main(String[] args){
        DBManager dbmanager = DBManager.getInstance();
        dbmanager.clear();
        TestReadCSV.readcsv("data/movies",dbmanager);
        TestReadCSV.readcsv("data/city",dbmanager);
        TestReadCSV.readcsv("movies3",dbmanager);
    }
}
