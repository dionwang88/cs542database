package BPlusTree;

import java.util.Set;
import java.util.Map.Entry;
public class RBTreeTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RBTree<String, Integer> t = new RBTree<String, Integer>();
		t.put("S", 8);
		t.put("E", 4);
		t.put("A", 12);
		t.put("R", 5);
		t.put("C", 11);
		t.put("H", 9);
		t.put("X", 10);
		t.put("M", 3);
		t.put("P", 0);
		t.put("L", 7);
		System.out.println(t.ceilingEntry("R").getKey());
		Set<Entry<String, Integer>> m = t.BiggerThanMedian();
		for (Entry<String, Integer> e : m){
			System.out.println(e.getKey() + " " + e.getValue());
		}
		System.out.println("dsdsdsds");
		for (Entry<String, Integer> e : t.entrySet()){
			System.out.println(e.getKey() + " " + e.getValue());
		}
		
	}

}
