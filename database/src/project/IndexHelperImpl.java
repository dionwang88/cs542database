package project;



import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Collections;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;


/**
 * This class provides the methods of how to manipulate the index
 * @author wangqian
 *
 */
public class IndexHelperImpl implements IndexHelper {
	
	Logger logger = (Logger) LogManager.getLogger();
	
	private DBManager dbmanager = DBManager.getInstance();
	
	private static IndexHelperImpl indexHelper = null;
	
	protected IndexHelperImpl() {
		logger.info("Create IndexHelper Object.");
	}
	/**
	 * Singleton Object
	 * @return
	 */
	public static IndexHelperImpl getInstance(){
		if(indexHelper == null){
			indexHelper = new IndexHelperImpl();
		}
		return indexHelper;
	}
	/**
	 * find the indexes list of free spaces based on the data size get all free space based on the delete sign and 
	 * amount blocks to find some enough space to save the data.
	 * In pair object, left is start index, right is the size
	 */
	@Override
	public List<Pair<Integer,Integer>> findFreeSpaceIndex(int size) {		
		// Temp variable to record the left size of the data
		int left_size = size;
		List<Pair<Integer, Integer>> list_freeSpace = new ArrayList<Pair<Integer, Integer>>();
		List<Integer> index_list = this.getSortedIndexList();

		if(index_list.size() == 0){ //if the index has no pair index
			Pair<Integer,Integer> p = new Pair<Integer,Integer>(0, size); 
			list_freeSpace.add(p);
		}else if(index_list.size() == 2){// if the index list has only one pair index
			Pair<Integer,Integer> p = new Pair<Integer,Integer>(index_list.get(1)+1, size); 
			list_freeSpace.add(p);
		}else{ // if the index list has more than one pair index
			int i = 2;
			int second = index_list.get(1) + 1; // the next position to the end index of first pair index 
			while( i < index_list.size()){
				int third = index_list.get(i);
				// the length of free space
				int length = third - second;
				if(length != 0){// If between two pairs have free space
					if(length >= left_size){// if free space can contain the data
						Pair<Integer,Integer> p = new Pair<Integer,Integer>(second, left_size); 
						list_freeSpace.add(p);
						return list_freeSpace;
					}else{// if the free space cannot contain the data, then will to find next free space
						Pair<Integer,Integer> p = new Pair<Integer,Integer>(second, length); 
						list_freeSpace.add(p);
						left_size = left_size - length;
					}
				}
				i = i + 2; // add 2 to point the next index start point
				second = index_list.get(i - 1) + 1;
			}
		}
		
		return list_freeSpace;
	}
	/**
	 * Transform the index buffer to the Map type sorted by start index
	 * @return
	 */
	public List<Integer> getSortedIndexList(){
		// This list is to save all the index pairs(start, length).
		List<Integer> index_list = new ArrayList<Integer>();
		// Get the index buffer object
		Map<Integer, Index> indexBuffer = this.dbmanager.getIndexBuffer();
		// To get all the index list
		for (Entry<Integer, Index> entry : indexBuffer.entrySet()) {
			Index index = entry.getValue();
			List<Pair<Integer, Integer>> lst_p = index.getIndexes();
			for(Pair<Integer, Integer> pair : lst_p){
				//transform the index pair to start, end
				index_list.add(pair.getLeft());
				index_list.add(pair.getLeft()+pair.getRight());
			}
		}
		// Sort the index list
		Collections.sort(index_list);
		return index_list;
	}

	@Override
	public void splitDataBasedOnIndex(byte[] data_to_save, List<Pair<Integer,Integer>> indexes) {
		/**
		 * Split the data into several parts based on the free indexes list
		 * Then get the split data from data_to_save, and save the split data to the db_data file based on the indexes
		 */
		// load the database file from memory
		byte[] db_data = dbmanager.getData();
		int index_in_data_to_save = 0;
		// loop all the free space indexes
		for (Pair<Integer, Integer> pair : indexes){
			// get the start index on the db_data file
			int start = pair.getLeft();
			// The length of split data
			int length = pair.getRight();
			// Get the split data from data_to_save and then save the data to the db_data file based on the free space index
			for(int ind = 0; ind < length; ind++){
				db_data[start] = data_to_save[index_in_data_to_save];
				start = start + 1;
				index_in_data_to_save = index_in_data_to_save + 1;
			}
		}
	}

	
	public Index getIndex(List<Pair<Integer,Integer>> pairs_list, int key) {
		Index i = new Index();
		i.setKey(key);
		i.setIndexes(pairs_list);
		return i;
	}
	
	@Override
	public byte[] indexToBytes(Map<Integer, Index> indexes) {


		// calculate byte array size firstly.
		int indexpairnumb=0;
		int indexnumb=indexes.keySet().size();
		for(int key:indexes.keySet()){
			Index index=indexes.get(key);
			indexpairnumb+=index.getIndexes().size();
		}
		byte[] outbyte = new byte[indexnumb*(Index.getReservedSize()+Index.getKeySize()+1)+indexpairnumb*Integer.BYTES*2];
        logger.info("calculate the size of output byte[] is:"+outbyte.length);

		//convert indexMap to byte array
		int offset=0;
		for(int key:indexes.keySet()){
			Index index=indexes.get(key);
			//make sure pairs are sorted
			index.sortpairs();
			outbyte[offset++]=IndexHelper.START_SIGN;
            logger.info("converted #"+key+" start_sign");

			//add reserved bytes
			for (int i = 0; i <Index.getReservedSize() ; i++) {
				outbyte[offset++]=0;
			}
			logger.info("added reserved bytes");

			//covert key from int to byte[]
			byte[] bytekey = inttobytes(index.getKey());
            for (byte aBytekey : bytekey) {
                outbyte[offset++] = aBytekey;
            }
            logger.info("converted #"+key+" key");

            //convert pairs to byte[]
            List<Pair<Integer, Integer>> l= index.getIndexes();
            for (Pair<Integer, Integer> aL : l) {
                byte[] bpl = inttobytes(aL.getLeft());
                byte[] bpr = inttobytes(aL.getRight());
                for (int j = 0; j < bpl.length; j++) {
                    outbyte[offset] = bpl[j];
                    outbyte[offset+Integer.BYTES] = bpr[j];
                    offset++;
                }
                offset+=Integer.BYTES;
                logger.info("converted #"+key+"'s #"+(offset-8)/8+" pair");
            }

		}
		return outbyte;

	}
	
	/**
	 * 1. An index start sign
	 * 2. Key
	 * 3. The index in the data array
	 * 4. The amount of bytes of this index in the data array
	 */
	@Override
	public Map<Integer, Index> bytesToIndex(byte[] metadata) {

		Map<Integer,Index> returnmap= new Hashtable<>();
		int offset=0;
		int search_span=Index.getReservedSize()+1+Index.getKeySize();
		int pair_size=Integer.BYTES*2;
		while(offset<metadata.length){
			//search header
			if (metadata[offset]==-1){
				logger.info("found a start sign at " + offset);

				//get key
				int key_in_record=0;
				int key_start=offset+1+Index.getReservedSize();
				key_in_record=bytestoint(metadata,key_start);
				logger.info("key # is: " + key_in_record);

				//get pairs
				offset+=search_span;// skip the head to pairs
				List<Pair<Integer, Integer>> pairlist = new ArrayList<>();
				while(metadata[offset]>=0) {
					int l,r;
					//get L,R in the current pair
					l=bytestoint(metadata,offset);
					r=bytestoint(metadata,offset+Integer.BYTES);
					Pair<Integer,Integer> pair=new Pair<>(l,r);
					//get pair to list
					pairlist.add(pair);
					//go to next pair
					offset+=pair_size;
					if(offset>= metadata.length) break;
				}
				logger.info("got pairs " + pairlist.toString());

				//make index
				Index index=new Index();
				index.setKey(key_in_record);
				index.setIndexes(pairlist);

				//add to map
				returnmap.put(key_in_record,index);
			} else {
				logger.info("test this line may not appear, so this else could be redundant");
				offset += search_span;
			}
		}
		return returnmap;
	}

    private static byte[] inttobytes(int intnumb){
		/**
		 * convert integer into a 4 bytes byte array
		 */
        return ByteBuffer.allocate(Integer.BYTES).putInt(intnumb).array();
    }
	private static int bytestoint(byte[] b,int start_offset) {
		/**
		 * This function will, given a byte array and start,
		 * convert 4 bytes number into ag integer, from start_offset to start_offset+3
		 */
		int numb = 0;
		for (int i = start_offset; i < start_offset + Index.getKeySize(); i++) {
			numb <<= Byte.SIZE;
			numb += b[i];
		}
		return numb;
	}

	@Override
	public void addIndex(Index index) {

	}

	@Override

	public void removeIndex(Integer Key) {

	}


	public void updateIndex(Index index) {

	}
	@Override
	public Map<Integer, Index> getIndexesBuffer() {
		return null;
	}
}
