package BPlusTree;
import BPlusTree.BNode;
import BPlusTree.Lnode;
import java.util.List;
public class BTree<K extends Comparable<K>,V> {
	static final int NUMBER_PER_ROOT = 2;
	static final int NUMBER_PER_NODE = 3;
	BNode<K,V> root;
	
	BTree() {
		root = new Lnode<K,V>();
	}
	
	BTree(K key, V val){
		this();
		root.put(key, val);
	}
	
	public V get(K key) {
		BNode<K,V> N = root;
		while (N instanceof Inode) {
			N = ((Inode<K,V>)N).get(key);
		}
		return ((Lnode<K,V>)N).get(key);
	}
	
	public void put(K key, V val){
		Split<K,V> result = root.put(key, val);
		if (result != null){
			root = new Inode<K,V>(NUMBER_PER_ROOT,result);
		}
	}
	
	public List<V> Range(K start, K end, boolean stricts, boolean stricte){
		BNode<K,V> Ns = root;
		if (start !=null){
		while (Ns instanceof Inode) {
			Ns = ((Inode<K,V>)Ns).get(start);
		}
		return ((Lnode<K,V>) Ns).sweep(start, stricts, end, stricte);
		}else if (end !=null){
			while (Ns instanceof Inode) {
				Ns = ((Inode<K,V>)Ns).getleft();
			}
			return ((Lnode<K,V>) Ns).sweep(null, false, end, stricte);	
		}else{
			return null;
		}
	}
	

	public static void main(String[] args) {
		BTree<String,Integer> btree = new BTree<String,Integer>();
		btree.put("S", 19);
		btree.put("B", 2);
		btree.put("J", 10);
		btree.put("A", 1);
		btree.put("E", 5);
		btree.put("W", 23);
		btree.put("M", 13);
		btree.put("N", 14);
		btree.put("F", 6);
		btree.put("Q", 17);
		btree.put("R", 18);
		btree.put("K", 11);
		btree.put("D", 4);
		btree.put("G", 7);
		btree.put("H", 8);
		btree.put("U", 21);
		btree.put("V", 22);
		btree.put("O", 15);
		btree.put("P", 16);
		btree.put("T", 20);
		btree.put("X", 24);
		btree.put("I", 9);
		btree.put("C", 3);
		btree.put("L", 12);

		System.out.println(btree.get("A"));
		List<Integer> s = btree.Range(null, "O", true, true);
		System.out.println(s.toString());

	}

}
