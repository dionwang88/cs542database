package project;

/**
 * Pair class is to be used to save the index and the data length in the data file
 * @author wangqian
 *
 * @param <L>: Left is the index in meta data file
 * @param <R>: right is the length of the consecutive data
 */
public class Pair<L,R> {

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

}
