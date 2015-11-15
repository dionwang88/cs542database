package project;

import java.io.Serializable;

/**
 * Pair class is to be used to save the index and the data length in the data file
 * @author wangqian
 *
 * @param <L>: Left is the index in meta data file
 * @param <R>: right is the length of the consecutive data
 */
public class Pair<L,R> implements Serializable {

	  private final L left;
	  private final R right;

	  public Pair(L left, R right) {
	    this.left = left;
	    this.right = right;
	  }

	  public L getLeft() { return left; }
	  public R getRight() { return right; }

	@Override
	public String toString() {
		return "Pair [left=" + left + ", right=" + right + "]";
	}
	
	@Override
	public boolean equals(Object obj){
	      if (!(obj instanceof Pair))
	          return false;
	        Pair<L,R> ref = (Pair<L,R>) obj;
	        return left.equals(ref.getLeft()) && 
	            right.equals(ref.getRight());
	}
	
	@Override
	public int hashCode(){
		return left.hashCode()^right.hashCode();
	}

}
