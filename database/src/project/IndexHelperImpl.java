package project;

import java.nio.ByteBuffer;
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

	public IndexHelperImpl() {
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
			indexpairnumb+=index.getIndex_num();
		}
		byte[] outbyte = new byte[indexnumb*(Index.INDEX_NUMBER_SIZE+Index.KEYSIZE+1)+indexpairnumb*Integer.BYTES*2];
        logger.info("calclate the size of output byte[] is:"+outbyte.length);

		//convert indexMap to byte array
		int offset=0;
		for(int key:indexes.keySet()){
			Index index=indexes.get(key);
			outbyte[offset++]=IndexHelper.START_SIGN;
            logger.info("converted #"+key+" start_sign");

			//covert key from int to byte[]
			byte[] bytekey = inttobytes(index.getKey());
            for (byte aBytekey : bytekey) {
                outbyte[offset++] = aBytekey;
            }
            logger.info("converted #"+key+" key");

			// convert index_number to byte[]
			byte[] byteindexnumb = indexnumbtobytes(index.getIndex_num());
			for(int i=0;i<byteindexnumb.length;i++) {
				outbyte[offset++] = byteindexnumb[i];
			}
            logger.info("converted #"+key+" index_number");

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
	 * 3. Total number of the indexes
	 * 4. The index in the data array
	 * 5. The amount of bytes of this index in the data array
	 */
	@Override
	public Map<Integer, Index> bytesToIndex(byte[] metadata) {
		return null;
	}

    private static byte[] inttobytes(int intnumb){
        return ByteBuffer.allocate(Integer.BYTES).putInt(intnumb).array();
    }

    private static byte[] indexnumbtobytes(int intnumb){
        byte[] b=new byte[Index.INDEX_NUMBER_SIZE];
        //exception
        if (intnumb>>24!=0) { System.out.println("#########---ERROR---KEY SIZE > 3 bytes------#########");}
        b[0]=(byte)(intnumb>>16);
        b[1]=(byte)(intnumb>>8);
        b[2]=(byte)(intnumb);
        return b;
    }

}
