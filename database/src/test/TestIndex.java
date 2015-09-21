package test;

import project.Pair;

import java.util.ArrayList;
import java.util.List;

import project.Index;

public class TestIndex {
	public static void main(String[] args){
		Index index = new Index();
		index.setKey(1);
		index.setIndex_num(2);
		Pair<Integer, Integer> p1 = new Pair<Integer, Integer>(1,2);
		Pair<Integer, Integer> p2 = new Pair<Integer, Integer>(4,3);
		List<Pair<Integer, Integer>> l = new ArrayList<Pair<Integer, Integer>>();
		l.add(p1);
		l.add(p2);
		index.setIndexes(l);
		
		System.out.println(index.toString());
		List<Pair<Integer, Integer>> indexes = index.getIndexes();
		
		for( Pair<Integer, Integer> p : indexes){
			System.out.println(p.getLeft() + "," + p.getRight());
		}
	}
}
