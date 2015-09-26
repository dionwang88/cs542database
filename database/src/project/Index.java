package project;

import java.util.List;

/**
 * Index Class
 * @author wangqian
 * @param sign: the separator of record's indexes
 * @param key: the key of the data
 * @param index_num: how many blocks to save the data in the data file
 * @param indexes: the pairs of (index, length) of the data blocks in the data file, ordered by index
 */

public class Index {
	/**
	 * The index start sign in the meta data file
	 * could be redundant
	 */
	private static final byte sign = -1;
	/**
	 * byte size of key
	 */
	private static final byte KEYSIZE = Integer.BYTES;
	/**
	 * keep three reserved bytes, so total minimum number of byte,
	 * for a record, is (1+3+key size+pair size).
	 * Now it is 16 bytes
	 */
	private static final byte RESERVED=3;
	/**
	 * key: the key of the data
	 */
	private int key;
	/**
	 * the pairs of (index, length) of the data blocks in the data file, ordered by index
	 */
	private List<Pair<Integer, Integer>> indexes;

	private int index_num;

	public Index(){}
	
	public static int getReservedSize(){
		return RESERVED;
	}
	public static int getKeySize(){
		return KEYSIZE;
	}
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public List<Pair<Integer, Integer>> getIndexes() {
		return indexes;
	}
	public void setIndexes(List<Pair<Integer, Integer>> l) {
		this.indexes = l;
	}
	public static byte getSign() {
		return sign;
	}
	
	@Override
	public String toString() {
		return "Index [key=" + key +", indexes=" + indexes + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index_num;
		result = prime * result + ((indexes == null) ? 0 : indexes.hashCode());
		result = prime * result + key;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Index other = (Index) obj;
		if (index_num != other.index_num)
			return false;
		if (indexes == null) {
			if (other.indexes != null)
				return false;
		} else if (!indexes.equals(other.indexes))
			return false;
		if (key != other.key)
			return false;
		return true;
	}
}
