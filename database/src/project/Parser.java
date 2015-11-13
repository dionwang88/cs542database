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
	List<Pair<Integer,String>> On_Conditions;
	public Parser(String query){
		attrnames = new ArrayList<String>();
		Relations = new ArrayList<Relation>();
		On_Conditions = new ArrayList<Pair<Integer,String>>();
		query = query.trim().toLowerCase();
		int l1 =find(query, "\\s*select\\s+")[0];
		int l2 =find(query, "\\s+from\\s+")[0];
		int l3 =find(query, "\\s+where\\s+")[0];
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
	
	private static int[] find(String s, String pattern){
		Pattern p=Pattern.compile(pattern); 
		Matcher m = p.matcher(s);
		m.find();
		int[] locs = new int[2];
		locs[0] = m.start();
		locs[1] = m.end();
		return locs;
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
		int on_condition =find(s, "\\s+on\\s+")[0];
		int tid;
		String JInfo = s.substring(on_condition);
		s = s.substring(5, on_condition);
		String[] rs = s.split("\\s*,\\s*");
		for (String rname : rs){
			Relation r = new Relation();
			r.setRelation_name(rname);
			try{
				tid = tabNameToID(dbm,rname);
				if (tid >= 0){
				r.setRelation_id(tid);
				Relations.add(r);
				int[] infoloc = this.find(JInfo, "\\s+"+rname+".\\w+");
				String info = JInfo.substring(infoloc[0], infoloc[1]);
				String[] tmp = info.split("\\.");
				On_Conditions.add(new Pair<Integer,String>(tid, tmp[1]));
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
    	Parser par = new Parser("select x1,x3,x4 from Movies, Movies1 on Movies.year = Movies1.year"
    			+ " where year = 1989 and bay > 5  or country=\"usa\" ");
    }
	

}
