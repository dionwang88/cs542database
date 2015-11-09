package test;

import project.index.Index;
import project.storage.Pair;

import java.util.ArrayList;
import java.util.List;

public class TestIndex {
	public static void main(String[] args){
		Index index = new Index();
		index.setKey(1);
		Pair<Integer, Integer> p1 = new Pair<Integer, Integer>(1,2);
		Pair<Integer, Integer> p2 = new Pair<Integer, Integer>(4,3);
		List<Pair<Integer, Integer>> l = new ArrayList<Pair<Integer, Integer>>();
		l.add(p1);
		l.add(p2);
		index.setPhysAddrList(l);
		
		System.out.println(index.toString());
		List<Pair<Integer, Integer>> indexes = index.getPhysAddrList();
		
		for( Pair<Integer, Integer> p : indexes){
			System.out.println(p.getLeft() + "," + p.getRight());
		}
	}
}
