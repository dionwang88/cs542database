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
	 * The size of a block
	 */
	public static final int BLOCK_SIZE = 128;
	/**
	 * The key of the block
	 */
	private int Key;
	
	/**
	 * The data length of this block
	 */
	private int data_length;

	public Block(){}

	public int getKey() {
		return Key;
	}

	public void setKey(int key) {
		Key = key;
	}

	public int getData_length() {
		return data_length;
	}

	public void setData_length(int data_length) {
		this.data_length = data_length;
	}
	
	
}
