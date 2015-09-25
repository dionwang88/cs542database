package test;

import project.Index;
import project.IndexHelper;
import project.IndexHelperImpl;
import project.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vincent on 9/25/15.
 */
public class TestByteIndex {
    public static void main(String args[]){
        //initial an index
        Index index = new Index();
        index.setKey(256*256*256*127+256*256*127+256*127+127);//not max key; but max in form of byte[]
        index.setIndex_num(3);
        Pair<Integer, Integer> p1 = new Pair<Integer, Integer>(256,30);
        Pair<Integer, Integer> p2 = new Pair<Integer, Integer>(256*256,40);
        Pair<Integer, Integer> p3 = new Pair<Integer, Integer>(256*256*256,50);
        List<Pair<Integer, Integer>> l = new ArrayList<Pair<Integer, Integer>>();
        l.add(p1);
        l.add(p2);
        l.add(p3);
        index.setIndexes(l);

        //put index in map
        Map<Integer,Index> map=new HashMap<>();
        map.put(index.getKey(), index);
        index.setKey(1);
        map.put(index.getKey(), index);

        //modified IndexHelperImpl from protected to public
        IndexHelper indexHelper = new IndexHelperImpl();
        byte[] bytecode =indexHelper.indexToBytes(map);
        for (int i = 0; i <bytecode.length ; i++) {
            System.out.print(bytecode[i]+" ");
        }
        System.out.println();
        System.out.println("length of metadata is: "+bytecode.length);


    }
}
