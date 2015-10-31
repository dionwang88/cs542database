package test;

import project.Addr;
import project.Pair;

import java.util.ArrayList;
import java.util.List;

public class TestIndex {
	public static void main(String[] args){
		Addr addr = new Addr();
		addr.setKey(1);
		Pair<Integer, Integer> p1 = new Pair<Integer, Integer>(1,2);
		Pair<Integer, Integer> p2 = new Pair<Integer, Integer>(4,3);
		List<Pair<Integer, Integer>> l = new ArrayList<Pair<Integer, Integer>>();
		l.add(p1);
		l.add(p2);
		addr.setPhysAddrList(l);
		
		System.out.println(addr.toString());
		List<Pair<Integer, Integer>> indexes = addr.getPhysAddrList();
		
		for( Pair<Integer, Integer> p : indexes){
			System.out.println(p.getLeft() + "," + p.getRight());
		}
	}
}
