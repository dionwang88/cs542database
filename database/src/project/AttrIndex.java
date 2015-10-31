package project;
import java.util.Hashtable;
import java.util.ArrayList;

public class AttrIndex<K>{
	Hashtable<Integer,ArrayList<Integer>> table;
	private DBManager dbm = DBManager.getInstance();
	
	public AttrIndex(){
		table = new Hashtable<Integer,ArrayList<Integer>>();
	}
	public AttrIndex(ArrayList<String> Attrnames){
		this();
		String attrs = "";
		if (Attrnames.size() > 1) {
		for (String s : Attrnames){
			attrs = attrs + "|" + s;
		}
		attrs +="|";
		}else{
			attrs = Attrnames.get(0);
		}
		for (int i = 1; i <= dbm.getIndexBuffer().size(); i++){
			Object attr = dbm.getAttribute(i, attrs);
			this.hashput(attr.toString().hashCode(),i);
		}
		
	}
	
	public void put(int key, K data_value){
		this.hashput(data_value.toString().hashCode(),key);
	}

	
	
	private void hashput(int val,int rID){
		if (table.containsKey(val)) {
			table.get(val).add(rID);
		}else{
			ArrayList<Integer> l = new ArrayList<Integer>();
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
