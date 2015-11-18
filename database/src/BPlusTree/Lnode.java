package BPlusTree;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.List;


public class Lnode<K extends Comparable<K>,V> extends BNode<K,V>{

	Lnode<K,V> nextSib;
	final V[] values = (V[])new Object[BTree.NUMBER_PER_NODE];
	
	Lnode(){
		size = 0;
		final K[] k = (K[])new Comparable[BTree.NUMBER_PER_NODE];
		this.keys = k;
		nextSib = null;
	}
	Lnode(K key, V val){
		this();
		this.put(key, val);
	}
	
	private int searchkey(K key){
		for (int i =0; i < size; i++){
			if (keys[i].compareTo(key) >= 0){
				return i;
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
				result.middlekey = this.keys[mid];
			}else{
				result = split(mid + 1);
				((Lnode<K,V>)result.right).insertnonfull(key, val);
				result.middlekey = this.nextSib.keys[0];
			}
		}else{
			this.insertnonfull(key, val);
		}
		return result;
	}
	
	private void insertnonfull(K key, V val){
		Split<K,V> result = null;
		int loc = searchkey(key);
		//Overloading
		if (loc < size && keys[loc].equals(key)){
			values[loc] = val;
		}
		for (int i = size -1; i >= loc ; i--) {
				keys[i+1] = keys[i];
				values[i+1] = values[i];
			}
		keys[loc] = key;
		values[loc] = val;
		size++;
	}
	
	public V get(K key){
		int loc = searchkey(key);
		if (loc < size) return values[loc];
		else return null;
	}
	
	
	public Split<K, V> split(int k) {
		//Split the Leaf according to the key position
		int sNum = this.size - k;
		Lnode<K,V> Sibling = new Lnode<K,V>();
		Sibling.size = sNum;
		System.arraycopy(this.keys, k, Sibling.keys, 0, sNum);
		System.arraycopy(this.values, k, Sibling.values, 0, sNum);
		this.size = k;
		Sibling.nextSib = this.nextSib;
		nextSib = Sibling;
		return new Split<K,V>(keys[size/2], this,Sibling); // This key is flawed
	}

	public boolean isFull() {
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
				int cmp = end.compareTo(current.keys[i]);
				if (cmp > 0){ result.add(current.values[i]);
				}else{
					if (cmp == 0 && stricte) result.add(current.values[i]);
				return result;
				}
			}
			current = current.nextSib;
			loc = 0;
		}
		return result;
	}



}
