package BPlusTree;
import java.util.*;


public class Lnode<K extends Comparable<K>,V> extends BNode<K,V>{

	List<V> values;
	Lnode<K,V> nextSib;

	
	Lnode(){
		size = 0;
		keys = new ArrayList<K>(BTree.NUMBER_PER_NODE);
		values = new ArrayList<V>(BTree.NUMBER_PER_NODE);
		nextSib = null;
		//Initializing the ArrayLists
		for (int i = 0; i < BTree.NUMBER_PER_NODE; i++){
			keys.add(null);
			values.add(null);
		}
	}
	Lnode(K key, V val){
		this();
		this.put(key, val);
	}
	
	private int searchkey(K key){
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (keys.get(i).compareTo(key) >= 0) {
					return i;
				}
			}
		}
		return size;
	}
		
	@Override
	public Split<K,V> put(K key, V val) {
		Split<K,V> result = null;
		int loc = searchkey(key);
		int mid = this.size / 2;
		if (isFull()) {
			if (loc <= mid){
				result = split(mid);
				this.insertnonfull(key, val);
				result.middlekey = keys.get(mid);
			}else{
				result = split(mid + 1);
				((Lnode<K,V>)result.right).insertnonfull(key, val);
				result.middlekey = this.nextSib.keys.get(0);
			}
		}else{
			this.insertnonfull(key, val);
		}
		return result;
	}
	
	private void insertnonfull(K key, V val){
		int loc = searchkey(key);
		//Updating the value of the same key
		if (loc < size && keys.get(loc).equals(key)){
			values.set(loc, val);
		}
		// Moving all keys and values right to the insertion place to the right
		for (int i = size -1; i >= loc ; i--) {
			keys.set(i+1, keys.get(i));
			values.set(i+1, values.get(i));
			}
		// Setting key and value
		keys.set(loc, key);
		values.set(loc, val);
		size++;
	}
	
	public V get(K key){
		int loc = searchkey(key);
		if (loc < size) return values.get(loc);
		else return null;
	}
	
	
	public Split<K, V> split(int k) {
		//Split the Leaf according to the key position
		int sNum = this.size - k;
		Lnode<K,V> Sibling = new Lnode<K,V>();
		Sibling.size = sNum;
		// Needs future modification for elegancy and efficiency.
		for (int i = k; i < keys.size(); i++){
			Sibling.keys.set(i - k, keys.get(i));
			Sibling.values.set(i - k, values.get(i));
		}
		this.size = k;
		Sibling.nextSib = this.nextSib;
		nextSib = Sibling;
		return new Split<K,V>(null, this,Sibling); // The middle key here is useless
	}

	private boolean isFull() {
		return size == BTree.NUMBER_PER_NODE;
	}
	
	List<V> sweep(K start, boolean stricts, K end, boolean stricte){
		ArrayList<V> result = new ArrayList<V>();
		Lnode<K,V> current = this;
		int loc;
		if (start == null){ 
			loc = 0;
		}else{
			loc = searchkey(start);
			if (!stricts) loc = loc + 1;
		}
		while (current != null){
			for (int i =loc; i < current.size; i ++) {
				int cmp = end.compareTo(current.keys.get(i));
				if (cmp > 0){ result.add(current.values.get(i));
				}else{
					if (cmp == 0 && stricte) result.add(current.values.get(i));
				return result;
				}
			}
			current = current.nextSib;
			loc = 0;
		}
		return result;
	}



}
