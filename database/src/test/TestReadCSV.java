package test;

import project.DBManager;
import project.DBTool;
import project.Pair;
import java.util.ArrayList;
import java.util.List;

public class TestReadCSV {
    public static void readcsv(String name,DBManager dbmanager,String regsep,String schema,String path){
        try {
            dbmanager.createTab(name, schema);
            dbmanager.ReadFile(path, DBTool.tabNameToID(dbmanager,name),regsep);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        DBManager dbmanager = DBManager.getInstance();
        dbmanager.clear();
        readcsv("movies",dbmanager,"@",
        		"Title char 80,year int,format char 30,genre char 30,director char 60,writer char 60,country char 30,Studio char 80,Price char 10,Catalogno char 10",
        		"data/movies.txt");
        //readcsv("Country",dbmanager,"\\s*,(?=([^\"]*\"[^\"]*\")*[^\"]*$)","Code char 5,Name char 45,Continent char 20,Region char 30," +
        //        "SurfaceArea int,IndepYear int,Population int,LifeExpectancy float," +
        //        "GNP int,GNPOID int,LocalName char 50,GovernmentForm char 50,HeadOfState char 50,Capital int,Code2 char 5");
    }
}