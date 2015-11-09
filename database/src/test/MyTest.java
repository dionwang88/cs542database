package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.TreeMap;

import project.Pair;

import java.util.Iterator;
import java.util.List;

class MyTest {
	public List<Pair<Integer,Integer>> findFreeSpaceIndex(int size,List<Integer> index_list) {	
		// Temp variable to record the left size of the data
		int left_size = size;
		List<Pair<Integer, Integer>> list_freeSpace = new ArrayList<Pair<Integer, Integer>>();

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
					if(length >= left_size){
						Pair<Integer,Integer> p = new Pair<Integer,Integer>(second, left_size); 
						list_freeSpace.add(p);
						return list_freeSpace;
					}else{
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
	
	public void splitDataByIndex(byte[] db_data, byte[] data_to_save, List<Pair<Integer,Integer>> indexes){
		int index_in_data_to_save = 0;
		
		for (Pair<Integer, Integer> pair : indexes){
			int start = pair.getLeft();
			int length = pair.getRight();
			for(int ind = 0; ind < length; ind++){
				db_data[start] = data_to_save[index_in_data_to_save];
				start = start + 1;
				index_in_data_to_save = index_in_data_to_save + 1;
			}
		}
	}
public static void main(String[] args) {
//   HashMap<Integer,String> hm = new HashMap<Integer,String>();
//   hm.put(14,"three");
//   hm.put(13,"three");
//   hm.put(19,"three");
//   hm.put(23,"three");
//   hm.put(3,"three");
//   hm.put(4,"four");
//   hm.put(2,"two");
//   hm.put(1,"one");
//   printMap(hm);
//   Map<Integer, String> treeMap = new TreeMap<Integer, String>(hm);
//   printMap(treeMap);
	MyTest my = new MyTest();
	
	int[] l = new int[]{1,4,5,10,20,28,47,55,70,80,100,110};
	List<Integer> list = new ArrayList<Integer>();
	for(int i:l){
		list.add(i);
	}
	byte[] data_to_save = {18,17,16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1};
	
	List<Pair<Integer, Integer>> mylist = my.findFreeSpaceIndex(data_to_save.length, list);
	for(Pair<Integer, Integer> p : mylist){
		System.out.println(p);
	}
	
	byte[] db_data = new byte[256];
	for(int i=0;i<db_data.length;i++){
		db_data[i] = (byte)i;
	}
	for(byte i : db_data){
		System.out.print(i + " ");
	}
	
	my.splitDataByIndex(db_data, data_to_save, mylist);
	
	System.out.println();
	for(byte i : db_data){
		System.out.print(i + " ");
	}
	
}//main

//public static void printMap(Map<Integer,String> map) {
//    Set s = map.entrySet();
//    Iterator it = s.iterator();
//    while ( it.hasNext() ) {
//       Map.Entry entry = (Map.Entry) it.next();
//       Integer key = (Integer) entry.getKey();
//       String value = (String) entry.getValue();
//       System.out.println(key + " => " + value);
//    }//while
//    System.out.println("========================");
//}//printMap

}//class
