package project;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;

public class AttrIndex<K>implements Serializable {
	//store the attribute index information
	Hashtable<Integer,List<Integer>> table;

	//init the attribute and establish the index of certain attributes when creating a new instance
	public AttrIndex(){table = new Hashtable<>();}
	public AttrIndex(int tid,List<String> AttrNames) {
		this();
		DBManager dbm = DBManager.getInstance();
		for (int i = 1; i <= dbm.getClusteredIndex().size(); i++) {
			int hashValue = 0;
			for (String AttrName : AttrNames) {
				byte[] tuple=dbm.Get(tid,i);
				if(tuple==null) continue;
				Object attr = dbm.getAttribute(tid,tuple,AttrName);
				hashValue += attr.toString().hashCode();
			}
			this.Put(hashValue, i);
		}
	}

	//oldPut hash value of the datavalue and rid into the table member
	public void oldPut(int key, K data_value){
		this.Put(data_value.toString().hashCode(), key);
	}

	//oldPut hash value of the datavalue and rid into the table member
	private void Put(int val, int rID){
		if (table.containsKey(val)) {
			table.get(val).add(rID);
		}else{
			List<Integer> l = new ArrayList<>();
			l.add(rID);
			table.put(val, l);
		}
	}

	//return the key list of the certain values
	public List<Integer> Get(K attrs){
		List<Integer> l = table.get(attrs);
		if (l != null) return l;
		else return null;
	}

	//Remove the index of the certain key
	public void Remove(K attrs){
		table.remove(attrs.toString().hashCode());
	}
	
	//for print and test
	public String toString(){
		return table.toString();
	}
	
	
}
