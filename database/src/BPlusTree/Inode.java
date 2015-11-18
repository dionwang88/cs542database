package BPlusTree;

import java.util.ArrayList;
import java.util.List;
public class Inode<K extends Comparable<K>,V> extends BNode<K,V> {

	ArrayList<BNode<K,V>> values;
	int max;
	Inode(int number){
		keys = (K[])new Comparable[number];
		max = number;
		values = new ArrayList<BNode<K,V>>(number + 1);
	}
	Inode(int number, Split<K,V> s){
		this(number);
		keys[0] = s.middlekey;
		values.add(s.left);
		values.add(s.right);
		size ++;
	}

	
	/**
	 * Returns the position which the corresponding key is bigger than the given key.
	 * @param key
	 * @return
	 */
	public int searchkey(K key){
		if (size < 20) {
			for (int i =0; i < size; i++){
				if (keys[i].compareTo(key) > 0){
					return i;
				}
			}
			return size;
		}else{ //Binary search
			if (keys[0].compareTo(key) > 0) return 0;
			else if (keys[size].compareTo(key) < 0) return size;
			int start =0;
			int end = size;
			while (start != end ) {
				int mid = (start + end) / 2;
				int cmp = keys[mid].compareTo(key);
				if (cmp < 0){
					start = mid;
				}else if (cmp >= 0){
					end = mid;
			}
			}
			return start;
		}
	}
		
	
	public Split<K,V> put(K key, V value) {
		int loc = searchkey(key);
		Split<K,V> s = null;
		s = values.get(loc).put(key, value);
		if (s != null) { // needs to add a key
			if (isFull()){
				Split<K,V> result = split(s);
				return result;
			}
			if (values.size() < max + 1) {
				values.add(null);
			}
			if (loc < size){
			for (int i = size -1; i >= loc ; i--) {
				keys[i+1] = keys[i];
				values.set(i+2, values.get(i+1));
			}
			}
			values.set(loc+1, s.right);
			keys[loc] = s.middlekey;
			values.set(loc, s.left);
			size ++;
		}
		return null;
		// else not affected
	}
	
	
	public BNode<K,V> get(K key) {
		int loc = searchkey(key);
		return values.get(loc);
	}
	
	BNode<K,V> getleft(){
		return values.get(0);
	}


	public void remove(K key) {
		int loc = searchkey(key);
		if (loc > 0){
			for (int i = loc; i < size; i ++){
				keys[i] = keys[i+1];
			}
			values.remove(loc);
		}
	}


	public Split<K, V> split(Split<K,V> lower) {
		//Return a split that has the mid key. 
		int mid = this.size / 2;
		Inode<K,V> Sibling = new Inode<K,V>(BTree.NUMBER_PER_NODE);
		int sNum = this.size - mid;
		if (keys[mid].compareTo(lower.middlekey) > 0 ){
			//Split first,  up the middle key
			System.arraycopy(keys, mid , Sibling.keys, 0, sNum);
			int loc = searchkey(lower.middlekey);
			List<BNode<K,V>> tmp = values.subList(mid + 1 , this.size + 1);
			for (int i = mid + 1 ; i > loc ; i--) {
				if (i <size ){
				keys[i] = keys[i-1];
				values.set(i, values.get(i-1));
				}
			}
			keys[loc] = lower.middlekey;
			this.values.set(loc, lower.left);
			this.values.set(loc + 1, lower.right);
			Sibling.values.add(values.get(mid + 1));
			Sibling.values.addAll(tmp);
		}else{
			
			int loc = searchkey(lower.middlekey);
			int p = 0;
			for (int j = mid + 1; j < loc; j ++){
				Sibling.keys[p] = keys[j];
				Sibling.values.add(values.get(j));
				p++;
			}
			Sibling.keys[p] = lower.middlekey;
			Sibling.values.add(lower.left);
			Sibling.values.add(lower.right);
			p++;
			for (int j = loc ; j < size; j ++){
				Sibling.keys[p] = keys[j];
				Sibling.values.add(values.get(j));
				p++;
			}
		}
		Sibling.size = sNum;
		this.size = mid; // leave the middle one alone, which will be inserted to the parent
		transform();
		return new Split<K,V>(keys[mid], this,Sibling);
	}
	
	private void transform(){
		if (this.max < BTree.NUMBER_PER_NODE){
			this.max = BTree.NUMBER_PER_NODE;
			K[] tmp = (K[])new Comparable[max];
			System.arraycopy(keys, 0, tmp, 0, 2);
			keys = tmp;
		}
	}


	public boolean isFull() {
		return size == max;
	}

	

}
