package project.BPlusTree;

import java.util.ArrayList;
import java.util.List;
public class Inode<K extends Comparable<K>,V> extends BNode<K,V> {

	List<BNode<K,V>> values;
	int max;

	Inode(int number){
		keys = new ArrayList<K>(number + 1);
		max = number;
		values = new ArrayList<BNode<K,V>>(number + 2);
		for (int i = 0; i <= max ; i++){
			keys.add(null);
			values.add(null);
		}
		values.add(null);
	}
	Inode(int number, Split<K,V> s){
		this(number);
		keys.set(0, s.middlekey);
		values.set(0, s.left);
		values.set(1, s.right);
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
				if (keys.get(i).compareTo(key) > 0){
					return i;
				}
			}
			return size;
		}else{ //Binary search
			if (keys.get(0).compareTo(key) > 0) return 0;
			else if (keys.get(size).compareTo(key) < 0) return size;
			int start =0;
			int end = size;
			while (start != end ) {
				int mid = (start + end) / 2;
				int cmp = keys.get(mid).compareTo(key);
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
		int mid = this.size / 2;
		Split<K,V> s, result ;
		s = values.get(loc).put(key, value);
		if (s != null) { // needs to add a key
			// Moving keys and values right
			if (loc < size){
			for (int i = size -1; i >= loc ; i--) {
				keys.set(i+1, keys.get(i));
				values.set(i+2, values.get(i+1));
			}
			}
			values.set(loc+1, s.right);
			keys.set(loc, s.middlekey);
			values.set(loc, s.left);
			size ++;
			if (isOverflown()){
				result = split();
				return result;
			}
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
				keys.set(i, keys.get(i+1));
			}
			values.remove(loc);
		}
	}


	public Split<K, V> split() {
		//Return a split that has the mid key.
		int mid = this.size / 2;
		int sNum = this.size - mid - 1;
		List<K> tmpkeys;
		List<BNode<K,V>> tmpvals;
		Inode<K,V> Sibling = new Inode<K,V>(BTree.NUMBER_PER_NODE);
		tmpkeys = keys.subList(mid + 1, this.size);
		for (int i = mid + 1; i < this.size; i++){
			Sibling.keys.set(i - mid - 1, keys.get(i));
			Sibling.values.set(i - mid - 1, values.get(i));
		}
		Sibling.values.set(this.size - mid - 1, values.get(this.size));
		this.size = mid;
		Sibling.size = sNum;
		transform();
		return new Split<K,V>(keys.get(mid), this,Sibling);
	}
	
	private void transform(){
		if (this.max < BTree.NUMBER_PER_NODE){
			this.max = BTree.NUMBER_PER_NODE;
			List<K> tmp = new ArrayList<K>(max + 1);
			tmp.addAll(keys.subList(0, BTree.NUMBER_PER_ROOT));
			for (int i = tmp.size(); i <= max; i ++){
				tmp.add(null);
			}
			values.add(null);
			keys = tmp;
		}
	}


	public boolean isOverflown() {
		return size == max + 1;
	}

	

}
