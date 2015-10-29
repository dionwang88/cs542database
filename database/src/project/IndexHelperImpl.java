package project;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.*;
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
	 * 
	 * To solve the fragment problem:
	 * 
	 * (1) public List<Integer> getSortedIndexList()
	 * Get the Index Buffer from DBManager 
	 * Loop the indexes in the IndexBuffer Map to get the index pairs list:
	 * 		Loop the index pairs list:
	 * 			Get the start-length index pair and change it to start-end index pair
	 * 			add the start-end index into a start-end list
	 * 		Sort the start-end list and return the list in order to find free space method
	 * 
	 * (2) public List<Pair<Integer,Integer>> findFreeSpaceIndex(int size)
	 * Loop the start-end list:
	 * 		Get two start-end indexes once a time
	 * 		The free space equals second index start - first index end
	 * 		Compare the size of free space with that of the saving data:
	 * 			if size of free space >= saving size of saving data:
	 * 				add the (start, saving size) pair to free space list
	 * 				return free space list
	 * 			else:
	 * 				add the free space to free space list
	 * 				saving size = saving size - this free size
	 * 				next loop
	 * 
	 * (3) public void splitDataBasedOnIndex(byte[] data_to_save, List<Pair<Integer,Integer>> indexes)
	 * Loop the free (start,end) pair in free space list:
	 * 		Get the free length = end - start
	 * 		copy the same length in saving data to the (start, end) in the database
	 * 		next loop
	 * 
	 */
	@Override
	public List<Pair<Integer,Integer>> findFreeSpaceIndex(int size) {		
		// Temp variable to record the left size of the data
		int left_size = size;
		List<Pair<Integer, Integer>> list_freeSpace = new ArrayList<>();
		List<Integer> index_list = this.getSortedIndexList();

		if(index_list.size() == 0){ //if the index has no pair index
			Pair<Integer,Integer> p = new Pair<>(0, size);
			list_freeSpace.add(p);
		}else if(index_list.size() == 2){// if the index list has only one pair index,new index start from last end + 1
			Pair<Integer,Integer> p = new Pair<>(index_list.get(1)+1, size);
			list_freeSpace.add(p);
		}else{ // if the index list has more than one pair index
			int i = 2;
			
			while( i < index_list.size()){
				int pre_end = index_list.get(i-1);
				int next_start = index_list.get(i); // the next position to the end index of first pair index 
				// the length of free space
				int length = next_start - (pre_end + 1);
				if(length != 0){// If between two pairs have free space
					if(length >= left_size){// if free space can contain the data
						// new index starts from pre_end + 1, size is left_size
						Pair<Integer,Integer> p = new Pair<>(pre_end+1, left_size);
						list_freeSpace.add(p);
						return list_freeSpace;
					}else{// if the free space cannot contain the data, then will to find next free space
						Pair<Integer,Integer> p = new Pair<>(pre_end+1, length);
						list_freeSpace.add(p);
						left_size = left_size - length;
					}
				}
				i = i + 2; // add 2 to point the next index start point
			}
			if(left_size > 0){ //There is no free space between previous pairs
				// The start of the new pair is the (end+1) of last pair
				int pre_end = index_list.get(i-1);
				if(pre_end== Storage.DATA_SIZE-1){
					pre_end=-1;
				}
				Pair<Integer,Integer> p = new Pair<>(pre_end+1, left_size);
				list_freeSpace.add(p);
			}
		}
		
		return list_freeSpace;
	}
	/**
	 * Transform the index buffer to the Map type sorted by start index
	 * @return return sorted indexList
	 */
	public List<Integer> getSortedIndexList(){
		// This list is to save all the index pairs(start, length).
		List<Integer> index_list = new ArrayList<>();
		// Get the index buffer object
		Map<Integer, Index> indexBuffer = this.dbmanager.getIndexBuffer();
		// To get all the index list
		for (Entry<Integer, Index> entry : indexBuffer.entrySet()) {
			Index index = entry.getValue();
			List<Pair<Integer, Integer>> lst_p = index.getIndexes();
			for(Pair<Integer, Integer> pair : lst_p){
				//transform the index pair to start, end
				index_list.add(pair.getLeft());
				index_list.add(pair.getLeft()+pair.getRight()-1);
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
		dbmanager.setData(db_data);
	}
	
	public int getIndexSize(List<Pair<Integer,Integer>> pairs_list) {
		return Index.getReservedSize()+1+Index.getKeySize()+pairs_list.size()*2*Integer.BYTES;
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

		//convert indexMap to byte array
		int offset=0;
		for(int key:indexes.keySet()){
			Index index=indexes.get(key);
			//make sure pairs are sorted
			index.sortpairs();
			outbyte[offset++]=INDEX_START_SIGN;

			//add reserved bytes
			for (int i = 0; i <Index.getReservedSize() ; i++) {
				outbyte[offset++]=0;
			}

			//covert key from int to byte[]
			if(index.getKey()<0) throw new Error("Key value can't be negative!");
			byte[] bytekey = intToByte(index.getKey());
            for (byte aBytekey : bytekey) {
                outbyte[offset++] = aBytekey;
            }

            //convert pairs to byte[]
            List<Pair<Integer, Integer>> l= index.getIndexes();
            for (Pair<Integer, Integer> aL : l) {
                byte[] bpl = intToByte(aL.getLeft());
                byte[] bpr = intToByte(aL.getRight());
                for (int j = 0; j < bpl.length; j++) {
                    outbyte[offset] = bpl[j];
                    outbyte[offset+Integer.BYTES] = bpr[j];
                    offset++;
                }
                offset+=Integer.BYTES;
            }
		}
		return outbyte;
	}

	@Override
	public Map<Integer, Index> bytesToIndex(byte[] metadata) {
		Map<Integer,Index> returnmap= new Hashtable<>();
		int offset=0;
		int search_span=4+Index.getKeySize();
		int pair_size=Integer.BYTES*2;
		while(offset<metadata.length){
			//search header
			if (metadata[offset]==-1){

				//get key
				int key_in_record;
				int key_start=offset+1+Index.getReservedSize();
				key_in_record= byteToInt(metadata, key_start);

				//get pairs
				offset+=search_span;// skip the head to pairs
				List<Pair<Integer, Integer>> pairlist = new ArrayList<>();
				while(metadata[offset]>=0) {
					int l,r;
					//get L,R in the current pair
					l= byteToInt(metadata, offset);
					r= byteToInt(metadata, offset + Integer.BYTES);
					Pair<Integer,Integer> pair=new Pair<>(l,r);
					//get pair to list
					pairlist.add(pair);
					//go to next pair
					offset+=pair_size;
					//index_used+=pair_size;
					//data_used += r;
					if(offset>= metadata.length) break;
				}

				//make index
				Index index=new Index();
				index.setKey(key_in_record);
				index.setIndexes(pairlist);
				//add to map
				returnmap.put(key_in_record,index);

			} else {
				offset += search_span;
			}
		}
		return returnmap;
	}

	public byte[] tabMetaToBytes(Map<Integer, List<Pair>> tabMetadata){
		int offset=0,count=0;
		for(int tid:tabMetadata.keySet())
			count+=tabMetadata.get(tid).size();
		//init the byte array
		byte[] return_byte=new byte[count*(1+3+16+4)];
		for(int tid:tabMetadata.keySet()){
			//start flag
			return_byte[offset]=TAB_START_SIGN;
			//reserved bytes
			for(int i=offset+1;i<offset+1+TAB_META_RESERVED;i++) return_byte[i]=0;
			offset+=(1+TAB_META_RESERVED);

			//pairs start
			for(int i=0;i<tabMetadata.get(tid).size();i++){
				Pair p=tabMetadata.get(tid).get(i);
				if(i==0){
					String tab_str_name=(String) p.getRight();
					tab_str_name=tab_str_name+new String(new char[14-tab_str_name.length()]).replace("\0", " ");
					byte[] tab_name=tab_str_name.getBytes();
					//copy tid
					System.arraycopy(intToByte(tid),0,return_byte,offset,4);
					offset+=4;
					//copy first part of tab name
					return_byte[offset++]=0;
					System.arraycopy(tab_name,0,return_byte,offset,7);
					offset+=7;
					//second part
					return_byte[offset++]=0;
					System.arraycopy(tab_name,7,return_byte,offset,7);
					offset+=7;
				}
				else{
					String attr_str_name=(String) p.getLeft();
					attr_str_name=attr_str_name+new String(new char[14-attr_str_name.length()]).replace("\0", " ");
					byte[] attr_name=attr_str_name.getBytes();
					int attr_type=(int) ((Pair) p.getRight()).getLeft();
					Object attr_length = ((Pair) p.getRight()).getRight();
					//copy attr name (first part)
					return_byte[offset++]=0;
					System.arraycopy(attr_name,0,return_byte,offset,7);
					offset+=7;
					//second part
					return_byte[offset++]=0;
					System.arraycopy(attr_name,7,return_byte,offset,7);
					offset+=7;
					//copy type
					System.arraycopy(intToByte(attr_type),0,return_byte,offset,4);
					offset+=4;
					//copy length
					System.arraycopy(intToByte((Integer) attr_length),0,return_byte,offset,4);
					offset+=4;
				}
			}
		}
		return return_byte;
	}

	public Map<Integer, List<Pair>> bytesToTabMeta(byte[] metadata){
		Map<Integer, List<Pair>> return_map=new Hashtable<>();
		int offset=0,search_span=8;
		while(offset<metadata.length){
			int pair_no=0;
			if(metadata[offset]==-2){
				List<Pair> pairlist=new ArrayList<>();
				offset+=4;

				//table id
				int tid= byteToInt(metadata, offset);
				offset+=Integer.BYTES;
				//table name
				byte[] tab_name=new byte[14];
				System.arraycopy(metadata,offset+1,tab_name,0,7);
				offset+=8;
				System.arraycopy(metadata,offset+1,tab_name,7,7);
				offset+=8;
				String tab_str_name= null;
				try {
					tab_str_name = new String(tab_name,"UTF-8").trim();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				//put first pair
				Pair<Integer,String> p1=new Pair<>(tid,tab_str_name);
				pairlist.add(pair_no++, p1);

				while(offset<metadata.length&&metadata[offset]>=0){
					//attr name
					byte[] attr_name=new byte[14];
					System.arraycopy(metadata,offset+1,attr_name,0,7);
					offset+=8;
					System.arraycopy(metadata,offset+1,attr_name,7,7);
					offset+=8;
					String attr_str_name= null;
					try {
						attr_str_name = new String(attr_name,"UTF-8").trim();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					//attr type
					int attr_type= byteToInt(metadata, offset);
					offset+=Integer.BYTES;
					//attr length
					int attr_len= byteToInt(metadata, offset);
					offset+=Integer.BYTES;

					//add rest pairs
					Pair<Integer,Integer> p2=new Pair<>(attr_type,attr_len);
					Pair<String,Pair> p3=new Pair<>(attr_str_name,p2);
					pairlist.add(pair_no++, p3);
				}
				//put tab into map
				return_map.put(tid,pairlist);
			}
			else offset+=search_span;
		}
		return return_map;
	}

	 static byte[] intToByte(int intnumb){
		/**
		 * convert integer into a 4 bytes byte array
		 */
        return ByteBuffer.allocate(Integer.BYTES).putInt(intnumb).array();
    }

	 static int byteToInt(byte[] b, int start_offset) {
		/**
		 * This function will, given a byte array and start,
		 * convert 4 bytes number into an integer, from start_offset to start_offset+3
		 */
		int numb = 0;
		for (int i = start_offset; i < start_offset + Index.getKeySize(); i++) {
			numb <<= Byte.SIZE;
			int tmp=b[i];
			numb+=(tmp>=0?tmp:tmp+256);
		}
		return numb;
	}

	public static void main(String[] args){
		DBManager dbm=DBManager.getInstance();
		//test tabmetadata transform
		Map<Integer,List<Pair>> tabMetadata = new Hashtable<>();
		List<Pair> listpairs = new ArrayList<>();
		//listpairs.add(new Pair<>(1,"movieID"));
		listpairs.add(new Pair<>("_movieId", new Pair<>(1,10)));
		listpairs.add(new Pair<>("movieName", new Pair<>(0, 16)));
		dbm.createTabMete("movies",listpairs);
		//tabMetadata.put(1, listpairs);

		IndexHelper t=new IndexHelperImpl();
		byte[] b=t.tabMetaToBytes(dbm.getTabMeta());
		for (byte aB : b) System.out.println(aB);
		tabMetadata=t.bytesToTabMeta(b);
		System.out.println(tabMetadata.toString());

	}
}
