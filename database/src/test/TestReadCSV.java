package test;

import project.DBManager;
import project.DBTool;
import project.Pair;
import java.util.ArrayList;
import java.util.List;

public class TestReadCSV {
    public static void readcsv(String name,DBManager dbmanager){


        String schema="title char 80,year int,format char 80," +
                "genre char 80,director char 80,writer char 80,country char 80," +
                "studio char 80,price char 80,CatalogNo char 80";

        try {
            dbmanager.createTab(name, schema);
            dbmanager.ReadFile("movies.txt", DBTool.tabNameToID(dbmanager,name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        DBManager dbmanager = DBManager.getInstance();
        dbmanager.clear();
        readcsv("movies",dbmanager);
    }
}