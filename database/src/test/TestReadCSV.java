package test;

import project.DBManager;
import project.DBTool;
import project.Pair;
import java.util.ArrayList;
import java.util.List;

public class TestReadCSV {
    public static void readcsv(String name,DBManager dbmanager,String regsep){


        String schema="Code char 5,Name char 40,Continent char 20,Region char 30," +
                "SurfaceArea int,IndepYear int,Population int,LifeExpectancy float," +
                "GNP int,GNPOID int,LocalName char 50,GovernmentForm char 50,HeadOfState char 50,Capital int,Code2 char 5";

        try {
            dbmanager.createTab(name, schema);
            dbmanager.ReadFile("data/Country.csv", DBTool.tabNameToID(dbmanager,name),regsep);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        DBManager dbmanager = DBManager.getInstance();
        dbmanager.clear();
        readcsv("Country",dbmanager,"\\s*,(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    }
}