package project;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;

public class AttrIndex<K>implements Serializable {
	Hashtable<Integer,List<Integer>> table;

	public AttrIndex(){table = new Hashtable<>();}
	public AttrIndex(List<String> AttrNames) {
		this();
		DBManager dbm = DBManager.getInstance();
		for (int i = 1; i <= dbm.getClusteredIndex().size(); i++) {
			int hashValue = 0;
			for (String AttrName : AttrNames) {
				Object attr = dbm.getAttribute(i, AttrName);
				hashValue += attr.toString().hashCode();
			}
			this.hashPut(hashValue, i);
		}
	}
	public void put(int key, K data_value){
		this.hashPut(data_value.toString().hashCode(), key);
	}

	private void hashPut(int val, int rID){
		if (table.containsKey(val)) {
			table.get(val).add(rID);
		}else{
			List<Integer> l = new ArrayList<>();
			l.add(rID);
			table.put(val, l);
		}
	}
	
	public List<Integer> get(K attrs){
		List<Integer> l = table.get(attrs);
		if (l != null) return l;
		else return null;
	}
	
	public void remove(K attrs){
		table.remove(attrs.toString().hashCode());
	}
	
	public String toString(){
		return table.toString();
	}
	
	
}
