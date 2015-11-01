package test;

import project.DBManager;
import project.Pair;
import java.util.ArrayList;
import java.util.List;

public class TestReadCSV {
    public static void main(String[] args) {
        DBManager dbmanager = DBManager.getInstance();
        String[] AttrNames = {"Title", "Year", "Format", "Genre", "Director", "Writer", "Country", "Studio", "Price", "CatalogNo"};
        int[] type = {1, 0, 1, 1, 1, 1, 1, 1, 1, 1};
        List<Pair> attrs = new ArrayList<>();
        for (int i = 0; i < type.length; i++) {
            int length;
            if (type[i] == 1) length = 80;
            else length = 4;
            attrs.add(new Pair(AttrNames[i], new Pair(type[i], length)));
        }
        dbmanager.clear();
        dbmanager.createTab("Movies", attrs);
        dbmanager.ReadFile("movies.txt", 0);
    }
}
