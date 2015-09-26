package project;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import javax.lang.model.type.IntersectionType;


/**
 * This class provides the methods of how to manipulate the index
 * @author wangqian
 *
 */
public class IndexHelperImpl implements IndexHelper {
	Logger logger = (Logger) LogManager.getLogger();
	
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

	@Override
	public List<Integer> findFreeSpaceIndex(int size) {
		/**
		 * find the indexes list of free spaces based on the data size get all free space based on the delete sign and 
		 * amount blocks to find some enough space to save the data. For example, if the indexes in meta data are (0,0,1,0,1000), 
		 * (0,1,1,4001,1000), (0,1,1,8001,1000),then we can know that there has a 6000 bytes (1001-4000 and 5001-8000) free space 
		 * between those two data. If the data size is greater than the free space, then return a not enough space error message.
		 */
		return null;
	}

	@Override
	public List<Map<Integer, byte[]>> splitDataBasedOnIndex(byte[] data, List<Integer> indexes) {
		/**
		 * Split the data into several parts based on the free indexes list
		 */
		return null;
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
	private static int bytestoint(byte[] b,int start_offset){
		/**
		 * This function will, given a byte array and start,
		 * convert 4 bytes number into ag integer, from start_offset to start_offset+3
		 */
		int numb=0;
		for (int i=start_offset;i<start_offset+Index.getKeySize();i++){
			numb<<=Byte.SIZE;
			numb+=b[i];
		}
		return numb;
	}
}
