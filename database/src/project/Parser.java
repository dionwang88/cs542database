package project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import project.relations.*;

public class Parser {
	List<String> attrnames;
	List<Relation> Relations; //temporary; will change to List<Relation> later
	public Parser(String query){
		attrnames = new ArrayList<String>();
		Relations = new ArrayList<Relation>();
		query = query.trim().toLowerCase();
		int l1 =find(query, "\\s*select\\s+");
		int l2 =find(query, "\\s+from\\s+");
		int l3 =find(query, "\\s+where\\s+");
		System.out.println(l1 +" " + l2 + " " + l3);
		String selection = query.substring(0, l2).trim();
		String joins = query.substring(l2,l3).trim();
		String where = query.substring(l3).trim();
		System.out.println(selection);
		System.out.println(joins);
		System.out.println(where);
		Select(selection);
		Joins(joins);
		Where(where);
	}
	
	private static int find(String s, String pattern){
		Pattern p=Pattern.compile(pattern); 
		Matcher m = p.matcher(s);
		m.find();
		return m.start();
	}
	
	private void Select(String s){
		s = s.substring(7);
		String[] attrs = s.split("\\s*,\\s*");
		attrnames = Arrays.asList(attrs);
		for (String e : attrnames){
			System.out.println(e);
		}
	}
	
	private void Joins(String s){
		DBManager dbm = DBManager.getInstance();
		s = s.substring(5);
		String[] rs = s.split("\\s*,\\s*");
		for (String rname : rs){
			Relation r = new Relation();
			r.setRelation_name(rname);
			try{
				int tid = tabNameToID(dbm,rname);
				if (tid >= 0){
				r.setRelation_id(tid);
				Relations.add(r);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void Where(String s){
		s = s.substring(6);
		Condition c = new Condition(s);
	}
	
	public void Pipeline(DBManager dbm){
		AlgebraNode joinNode = new JoinOperator();
		if (Relations.size() > 1){
			for (Relation r : Relations){
				r.attach(joinNode);
				r.publish();
			}
		}

		
	}
	
    public static int tabNameToID(DBManager dbm, String tables) throws Exception {
        int tid=-1;
        for (int id : dbm.getTabMeta().keySet()) {
            if (dbm.getTabMeta().get(id).get(0).getRight().equals(tables.toLowerCase())) {
                tid = id;
                break;
            }
        }
        if(tid==-1){throw new Exception("No such table");}
        return tid;
    }
    
    public static void main(String[] args){
    	Parser par = new Parser("select x1,x3,x4 from Movies where year = 1989 and bay > 5  or country=\"usa\" ");
    }
	

}
