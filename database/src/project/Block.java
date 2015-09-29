package project;

/**
 * The block is the smallest unit in the data file
 * The first byte is the key of this block
 * The second byte is the data length in this block
 * @author wangqian
 *
 */
public class Block {
	/**
	 * The key of the block
	 */
	private int key;
	/**
	 * The start index of this block
	 */
	private int index;
	/**
	 * The data length of this block
	 */
	private int data_length;

	public Block(){}

	public int getKey() {
		return this.key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int getData_length() {
		return this.data_length;
	}

	public void setData_length(int data_length) {
		this.data_length = data_length;
	}
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
}
