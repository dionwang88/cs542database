package project;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;

import project.relations.AlgebraNode;
import project.relations.Relation;

public class AttrIndex<K>implements Serializable {
	//store the attribute index infomation
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
			this.hashPut(hashValue, i);
		}
	}

	//put hash value of the datavalue and rid into the table member
	public void put(int key, K data_value){
		this.hashPut(data_value.toString().hashCode(), key);
	}

	//put hash value of the datavalue and rid into the table member
	private void hashPut(int val, int rID){
		if (table.containsKey(val)) {
			table.get(val).add(rID);
		}else{
			List<Integer> l = new ArrayList<>();
			l.add(rID);
			table.put(val, l);
		}
	}

	//return the key list of the certain values
	public List<Integer> get(K attrs){
		List<Integer> l = table.get(attrs);
		if (l != null) return l;
		else return null;
	}

	//remove the index of the certain key
	public void remove(K attrs){
		table.remove(attrs.toString().hashCode());
	}
	
	//for print and test
	public String toString(){
		return table.toString();
	}
	
	
}
