package project;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.ArrayList;

public class AttrIndex<K>implements Serializable {
	Hashtable<Integer,ArrayList<Integer>> table;

	public AttrIndex(){table = new Hashtable<>();}
	public AttrIndex(ArrayList<String> AttrNames) {
		this();
		DBManager dbm = DBManager.getInstance();
		for (int i = 1; i <= dbm.getClusteredIndex().size(); i++) {
			int hashValue = 0;
			for (int j = 0; j < AttrNames.size(); j++) {
				Object attr = dbm.getAttribute(i, AttrNames.get(j));
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
			ArrayList<Integer> l = new ArrayList<>();
			l.add(rID);
			table.put(val, l);
		}
	}
	
	public ArrayList<Integer> get(K attrs){
		ArrayList<Integer> l = table.get(attrs.toString().hashCode());
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
