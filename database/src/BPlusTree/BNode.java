package BPlusTree;
import java.util.List;

public abstract class BNode<K extends Comparable<K>,V> {
	protected List<K> keys;
	protected int size; // number of keys
	abstract public Split<K,V> put (K key, V value);


}
