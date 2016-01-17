package project.BPlusTree;

public class Split<K extends Comparable<K>,V> {
	K middlekey;
	BNode<K,V> left;
	BNode<K,V> right;
	
	public Split(K k, BNode<K,V> l, BNode<K,V> r) {
		middlekey = k;
		left = l;
		right = r;
	}

}
