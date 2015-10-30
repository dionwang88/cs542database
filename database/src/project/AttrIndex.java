package project;
import java.util.Hashtable;
import java.util.LinkedList;

public class AttrIndex<K> {
	Hashtable<String,LinkedList<Integer>> table;
	private DBManager dbm = DBManager.getInstance();
	
	public AttrIndex(){
		table = new Hashtable<String,LinkedList<Integer>>();
	}
	public AttrIndex(String attrname){
		this();
		for (int i = 1; i <= dbm.getIndexBuffer().size(); i++){
			Object attr = dbm.getAttribute(i, attrname);
			this.Stringput(attr.toString(),i);
		}
		
	}
	
	public void put(int key, K data_value){
		this.Stringput(data_value.toString(),key);
	}
	
	
	private void Stringput(String val,int rID){
		if (table.containsKey(val)) {
			table.get(val).add(rID);
		}else{
			LinkedList<Integer> l = new LinkedList<Integer>();
			l.add(rID);
			table.put(val, l);
		}
	}

	
	public LinkedList<Integer> get(K attrs){
		LinkedList<Integer> l = table.get(attrs);
		if (l != null) return l;
		else return null;
	}
	
	public void remove(K attrs){
		table.remove(attrs);
	}
	
	public String toString(){
		return table.toString();
	}
	
	
}
