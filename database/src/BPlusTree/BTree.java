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
		print(btree);
		btree.put("B", 2);
		btree.put("J", 10);
		print(btree);
		btree.put("A", 1);
		btree.put("E", 5);
		btree.put("W", 23);
		print(btree);
		btree.put("M", 13);
		print(btree);
		btree.put("N", 14);
		print(btree);
		btree.put("F", 6);
		print(btree);
		btree.put("Q", 17);
		print(btree);
		btree.put("R", 18);
		btree.put("K", 11);
		btree.put("D", 4);
		btree.put("G", 7);
		print(btree);
		btree.put("H", 8);
		print(btree);
		btree.put("U", 21);
		print(btree);
		btree.put("V", 22);
		print(btree);
		btree.put("O", 15);
		print(btree);
		btree.put("P", 16);
		print(btree);
		btree.put("T", 20);
		print(btree);
		btree.put("X", 24);
		btree.put("I", 9);
		btree.put("C", 3);
		print(btree);
		btree.put("L", 12);
		print(btree);

		System.out.println(btree.get("A"));

	}
	static void print(BTree bt){
		List<Integer> s = bt.Range(null, "Z", true, true);
		System.out.println(s.toString());
	}

}
