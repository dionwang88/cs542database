package BPlusTree;


public abstract class BNode<K extends Comparable<K>,V> {
	protected K keys[];
	protected int size; // number of keys
	abstract public Split<K,V> put (K key, V value);


}
