package test;

import project.DBManager;
import project.DBTool;
import project.Pair;
import project.Storage;

import java.awt.*;
import java.util.*;

/**
 * Created by vincent on 10/1/15.
 */
public class TestShow {
    public static void main(String args[]){
        if (args.length!=1){
            System.out.println("1 Argument is expected, but actual # of arguments is "+args.length);
        }else{
            DBManager dbmanager=DBManager.getInstance();
            DBTool.showWrapped(dbmanager);
        }
    }
}
