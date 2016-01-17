package project.BPlusTree;

import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.NoSuchElementException;
public class RBTree<K extends Comparable<K>, V> implements Map<K, V>{
	
    private static final boolean RED   = true;
    private static final boolean BLACK = false;
	TreeNode root;
	
	RBTree () {
	}
	
	RBTree (K key, V val) {
		root = new TreeNode(key, val, RED, 1);
	}
	
	private RBTree (TreeNode t) {
		root = t;
	}
	
	public TreeEntry<K,V> getRoot() {
		return root.getEntry();
	}
	
	@Override
	public V get(Object key){
		TreeNode current = root;
		while (current != null) {
			int cmp = ((K)key).compareTo(current.key);
			if (cmp < 0) current= current.left;
			else if (cmp > 0) current = current.right;
			else return current.value;
		}
		return null;
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m){
		if (m != null) {
		for (Entry<? extends K, ? extends V> e : m.entrySet()) {
			this.put(e.getKey(), e.getValue());
		}
		}
	}
	
	
	@Override
	public Set<Entry<K, V>> entrySet() {
		return Traverse(root);
	}
	
	public Set<Entry<K, V>> Traverse(TreeNode t){
		Set<Entry<K, V>> s = new HashSet<Entry<K, V>>();
		s.add(new TreeEntry<K, V>(t.key, t.value));
		if (isLeaf(t)) {
			return s;
		}
		if (t.left != null) s.addAll(Traverse(t.left));
		if (t.right != null) s.addAll(Traverse(t.right));
		return s;
	}
	
	public Set<K> keySet(){
		Set<K> s = new HashSet<K>();
		Set<Entry<K, V>> entries = entrySet();
		for (Entry<K, V> e : entries) {
			s.add(e.getKey());
		}
		return s;
	}
	
	public Set<V> values(){
		Set<V> s = new HashSet<V>();
		Set<Entry<K, V>> entries = entrySet();
		for (Entry<K, V> e : entries) {
			s.add(e.getValue());
		}
		return s;
	}
	
	@Override
	public boolean containsKey(Object key){
		return get(key) == null;
	}
	
	public boolean containsValue(Object val){
		return values().contains(val);
	}
	@Override
	public V put(K key, V val) {
		root = this.put(root, key, val);
		root.color = BLACK;
		return val;
	}
	
	private TreeNode put(TreeNode t, K key, V val) {
		if (t == null) return new TreeNode(key, val, RED, 1);
		
		int cmp = key.compareTo(t.key);
		if (cmp < 0 ) t.setLeft(this.put(t.left, key, val));
		else if (cmp > 0) t.setRight(this.put(t.right, key, val));
		else t.setVal(val);
		
		//fixing all right-leaning links
		if (isRed(t.right) && !isRed(t.left)) t = rotateLeft(t);
		if (isRed(t.left) && isRed(t.left.left)) t = rotateRight(t);
		if (isRed(t.left)  &&  isRed(t.right))     flipColors(t);
		t.N = size(t.left) + size(t.right) + 1;
		return t;
	}
	
    public void deleteMin() {
        if (isEmpty()) throw new NoSuchElementException("BST underflow");

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = deleteMin(root);
        if (!isEmpty()) root.color = BLACK;
        // assert check();
    }

    // delete the key-value pair with the minimum key rooted at h
    private TreeNode deleteMin(TreeNode h) { 
        if (h.left == null)
            return null;

        if (!isRed(h.left) && !isRed(h.left.left))
            h = moveRedLeft(h);

        h.left = deleteMin(h.left);
        return balance(h);
    }


    /**
     * Removes the largest key and associated value from the symbol table.
     * @throws NoSuchElementException if the symbol table is empty
     */
    public void deleteMax() {
        if (isEmpty()) throw new NoSuchElementException("BST underflow");

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = deleteMax(root);
        if (!isEmpty()) root.color = BLACK;
        // assert check();
    }

    // delete the key-value pair with the maximum key rooted at h
    private TreeNode deleteMax(TreeNode h) { 
        if (isRed(h.left))
            h = rotateRight(h);

        if (h.right == null)
            return null;

        if (!isRed(h.right) && !isRed(h.right.left))
            h = moveRedRight(h);

        h.right = deleteMax(h.right);

        return balance(h);
    }
	
	
	
	
	  /**
     * Removes the key and associated value from the symbol table
     * (if the key is in the symbol table).
     * @param key the key
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public V remove(Object key) { 
        if (!containsKey(key)) {
            System.err.println("symbol table does not contain " + key);
            return null;
        }

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;
        
        root = delete(root, (K)key);
        if (!isEmpty()) root.color = BLACK;
        return null;
        // assert check();
    }
	
	
    // delete the key-value pair with the given key rooted at h
    private TreeNode delete(TreeNode h, K key) { 
        // assert get(h, key) != null;

        if (key.compareTo(h.key) < 0)  {
            if (!isRed(h.left) && !isRed(h.left.left))
                h = moveRedLeft(h);
            h.left = delete(h.left, key);
        }
        else {
            if (isRed(h.left))
                h = rotateRight(h);
            if (key.compareTo(h.key) == 0 && (h.right == null))
                return null;
            if (!isRed(h.right) && !isRed(h.right.left))
                h = moveRedRight(h);
            if (key.compareTo(h.key) == 0) {
                TreeNode x = min(h.right);
                h.key = x.key;
                h.value = x.value;
                // h.val = get(h.right, min(h.right).key);
                // h.key = min(h.right).key;
                h.right = deleteMin(h.right);
            }
            else h.right = delete(h.right, key);
        }
        return balance(h);
    }
	
	
	
	private TreeNode rotateLeft (TreeNode t) {
		TreeNode Rotated = t.right;
		t.right = Rotated.left;
		Rotated.left = t;
		Rotated.setColor(t.color);
		t.setColor(RED);
		Rotated.N = t.N;
		t.N = 1 + size(t.left) + size (t.right);
		return Rotated;
	}
	
    private TreeNode rotateRight(TreeNode t) {
        // assert (h != null) && isRed(h.left);
        TreeNode Rotated = t.left;
        t.left = Rotated.right;
        Rotated.right = t;
        Rotated.setColor(t.color);
        t.setColor(RED);
		Rotated.N = t.N;
		t.N = 1 + size(t.left) + size (t.right);
        return Rotated;
    }
    private void flipColors(TreeNode t) {
    	t.setColor(RED);
    	t.left.setColor(BLACK);
    	t.right.setColor(BLACK);
    }
    
 // Assuming that h is red and both h.left and h.left.left
    // are black, make h.left or one of its children red.
    private TreeNode moveRedLeft(TreeNode h) {
        // assert (h != null);
        // assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);

        flipColors(h);
        if (isRed(h.right.left)) { 
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
            flipColors(h);
        }
        return h;
    }
    
    // Assuming that h is red and both h.right and h.right.left
    // are black, make h.right or one of its children red.
    private TreeNode moveRedRight(TreeNode h) {
        // assert (h != null);
        // assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
        flipColors(h);
        if (isRed(h.left.left)) { 
            h = rotateRight(h);
            flipColors(h);
        }
        return h;
    }
    
    // restore red-black tree invariant
    private TreeNode balance(TreeNode h) {
        // assert (h != null);

        if (isRed(h.right))                      h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.left) && isRed(h.right))     flipColors(h);

        h.N = size(h.left) + size(h.right) + 1;
        return h;
    }
    
    @Override
    public void clear() {
    	root = null;
    	
    }
    
    private RBTree<K,V> NodetoTree(TreeNode t){
    	return new RBTree<K,V>(t);
    }
	
    public RBTree<K,V> floorTree(K key) {
		TreeNode current = root;
		while (current != null) {
			int cmp = key.compareTo(current.key);
			if (cmp < 0) current= current.left;
			else if (cmp > 0) current = current.right;
			else return NodetoTree(current.left);
		}
		return null;
    }
    
    public Map.Entry<K,V> floorEntry(K key) {
    	return floorTree(key).getRoot();
    }
    
    
    public RBTree<K,V> ceilingTree(K key) {
		TreeNode current = root;
		TreeNode parent;
		while (current != null) {
			int cmp = key.compareTo(current.key);
			parent = current;
			if (cmp < 0) current= current.left;
			else if (cmp > 0) current = current.right;
			else return NodetoTree(current.right); 

		}
		return null;
    }
    
    public Map.Entry<K,V> ceilingEntry(K key) {
    	return ceilingTree(key).getRoot();
    }
    
    
    public Map.Entry<K, V> FindMedian(){
    	if (!this.isEmpty()) {
    	int median = (this.size() + 1) / 2;
    	TreeNode current = root;
    	int csize = size(current);
    	while (csize != median && csize != median +1) {
    		if (size(current.left) < size(current.right)) {
    			current = current.right;
    		}else{
    			current = current.left;
    		}
    		csize = size(current);
    	}
    	if (csize == median){
    		return current.getEntry();
    	}else {
    		
    	}
    	}
    	return null;
    }
    
    
    public Set<Entry<K, V>> BiggerThanMedian() {
    	if (!this.isEmpty()) {
    	int seek = (this.size () + 1) / 2; 
    	TreeNode current = root;
    	TreeNode parent = root;
		Set<Entry<K,V>> result;
    	int csize = size(current);
    	while (Math.abs(csize - seek) > 1){
        	int diff = size(current.left) - size(current.right);
        	parent = current;
    		if (diff < 0 ){
    			current = current.right;
    		} else {
    			current = current.left;
    		}
    		csize = size(current);
    	}
    	if (csize == seek) {
    		//On the Left part , set the root to current and copy the rest to
    		// new node.
        	if (parent.key.compareTo(current.key) > 0) {
        		parent.left = null;
        		result = this.entrySet();
        		root = current;
        		if (isRed(root)) root.setColor(BLACK);
        	} else {
        		parent.right = this.deleteMin(current);
        		result = this.Traverse(current);
        	}
    	} else if (csize == seek - 1){
    		if (parent.key.compareTo(current.key) > 0) {
    			TreeNode tmp = parent.right;
    			parent.left = parent.right = null;
    			current = this.put(current, parent.key, parent.value);
    			this.remove(parent.key);
    			result = this.Traverse(root);
    			result.addAll(this.Traverse(tmp));
        		root = current;

    		}else{
    			parent.right = null;
    			result = this.Traverse(current);
    		}
    	}else{
    		if (parent.key.compareTo(current.key) > 0) {
    			parent.left = this.deleteMax(current); //Take one out
    			result = this.Traverse(current);
    		} else {
    			result = this.Traverse(current.left);
    			result.addAll(this.Traverse(current.right));
    			current.left = current.right = null;
    		}
    	}
    	return result;
    	}
    	return null;
    	
    }
    
    public void printTree(){
    	Set<Map.Entry<K, V>> allentries = Traverse(root);
    	for (Map.Entry<K, V> e : allentries) {
    		
    	}

    }
    
    /***************************************************************************
     *  Ordered symbol table methods.
     ***************************************************************************/

     /**
      * Returns the smallest key in the symbol table.
      * @return the smallest key in the symbol table
      * @throws NoSuchElementException if the symbol table is empty
      */
     public K min() {
         if (isEmpty()) throw new NoSuchElementException("called min() with empty symbol table");
         return min(root).key;
     } 

     // the smallest key in subtree rooted at x; null if no such key
     private TreeNode min(TreeNode x) { 
         // assert x != null;
         if (x.left == null) return x; 
         else                return min(x.left); 
     } 

     /**
      * Returns the largest key in the symbol table.
      * @return the largest key in the symbol table
      * @throws NoSuchElementException if the symbol table is empty
      */
     public K max() {
         if (isEmpty()) throw new NoSuchElementException("called max() with empty symbol table");
         return max(root).key;
     } 

     // the largest key in the subtree rooted at x; null if no such key
     private TreeNode max(TreeNode x) { 
         // assert x != null;
         if (x.right == null) return x; 
         else                 return max(x.right); 
     } 
    
    
    
    
    
    
    
	// Node Data Type
	private class TreeNode {
		private K key;
		private V value;
		private TreeNode left, right;
		private boolean color;
		private int N; // subtree size
		
		public TreeNode (K key, V val, boolean color, int subsize) {
			this.key = key;
			this.value = val;
			this.color = color;
			this.N = subsize;
		}
		
		void setLeft(TreeNode t) {
			this.left = t;
		}
		
		void setRight(TreeNode t) {
			this.right = t;
		}
		
		void setColor(boolean col) {
			this.color = col;
		}
		
		void setVal(V val) {
			this.value = val;
		}
		
		TreeEntry<K,V> getEntry() {
			return new TreeEntry<K,V> (key, value);
		}
		
	}
	
	/**
	 * Node Helper Functions
	 */
	private boolean isRed(TreeNode t) {
		if (t == null) return false;
		else return t.color == RED;
	}
	
	private boolean isLeaf(TreeNode t) {
		if (t.left == null && t.right == null) return true;
		return false;
	}
	
	private int size(TreeNode t) {
		if (t == null) return 0;
		else return t.N;
	}

	public int size() {
		return size(root);
	}
	
	public boolean isEmpty() {
		return root == null;
	}
	
	final class TreeEntry<K, V> implements Map.Entry<K, V> {
	    private K key;
	    private V value;
	    
	    public TreeEntry(K key, V value) {
	        this.key = key;
	        this.value = value;
	    }

	    @Override
	    public K getKey() {
	        return key;
	    }

	    @Override
	    public V getValue() {
	        return value;
	    }

	    @Override
	    public V setValue(V value) {
	        V old = this.value;
	        this.value = value;
	        return old;
	    }
	}
}
