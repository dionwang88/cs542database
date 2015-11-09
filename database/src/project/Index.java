package project;

import java.util.List;

public class Index {
	/**
	 * The addr start sign in the meta data file
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
	 * tid: the table id of the data
	 */
	private int tid;
	/**
	 * the pairs of (offset, length) of the data blocks in the data file, ordered by offset
	 */
	private List<Pair<Integer, Integer>> physAddrList;

	//-------------methods------------
	public Index(){}
	public static byte getSign() {return sign;}
	public static int getReservedSize(){return RESERVED;}
	public static int getKeySize(){return KEYSIZE;}
	public int getKey() {return this.key;}
	public void setKey(int key) {this.key = key;}
	public int getTID() {return this.tid;}
	public void setTID(int tid) {this.tid=tid;}
	public List<Pair<Integer, Integer>> getPhysAddrList() {return physAddrList;}
	public void setPhysAddrList(List<Pair<Integer, Integer>> l) {this.physAddrList = l;}

	@Override
	public String toString() {
		return "Index [key=" + key +", Address=" + physAddrList + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((physAddrList == null) ? 0 : physAddrList.hashCode());
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
		if (physAddrList == null) {
			if (other.physAddrList != null)
				return false;
		} else if (!physAddrList.equals(other.physAddrList))
			return false;
		return key == other.key;
	}

	public void sortPairs(){
		Pair<Integer,Integer> tmp;
		for (int i = physAddrList.size(); i>0; i--) {
			for (int j = 0; j < i-1 ; j++) {
				if(physAddrList.get(j).getLeft()> physAddrList.get(j+1).getLeft()){
					tmp= physAddrList.get(j+1);
					physAddrList.set(j+1, physAddrList.get(j));
					physAddrList.set(j,tmp);
				}
			}

		}
	}
}
