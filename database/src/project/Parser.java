package project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import project.relations.*;
import project.Pair;
import java.util.HashMap;
import java.util.Map;

public class Parser {
	Map<Integer,List<String>> attrnames;
	List<Relation> Relations; //temporary; will change to List<Relation> later
	List<Pair<Integer,String>> On_Conditions;
    //sub "or" condtions
    String[] or_conditions;
    //every branch of sub "and" conditions
    List<String[]> and_conditions=new ArrayList<>();
    Map<Integer, Map<Integer,List<Pair>>> Dispatched =  new HashMap<Integer, Map<Integer,List<Pair>>>();
    Map<Pair<Integer,Integer>, Pair<String,Pair>> CrossTable =  
    		new HashMap<Pair<Integer,Integer>, Pair<String,Pair>>();
    DBManager dbm = DBManager.getInstance();
	public Parser(String query){
		attrnames = new HashMap<Integer,List<String>>();
		Relations = new ArrayList<Relation>();
		On_Conditions = new ArrayList<Pair<Integer,String>>();
		query = query.trim().toLowerCase();
		int l1 =find(query, "\\s*select\\s+")[0];
		int l2 =find(query, "\\s+from\\s+")[0];
		int l3 =find(query, "\\s+where\\s+")[0];
		String selection = query.substring(0, l2).trim();
		String joins = query.substring(l2,l3).trim();
		String where = query.substring(l3).trim();
		Select(selection);
		Joins(joins);
		Where(where);
	}
	

	private static int[] find(String s, String pattern){
		Pattern p=Pattern.compile(pattern); 
		Matcher m = p.matcher(s);
		int[] locs = null;
		if (m.find()){
		locs = new int[2];
		locs[0] = m.start();
		locs[1] = m.end();
		}
		return locs;
	}
	
	private void Select(String s){
		s = s.substring(7);
		String[] attrs = s.split("\\s*,\\s*");
		for (String ss : attrs){
			String[] tmp = ss.split("\\.");
			try{
			int tID = DBTool.tabNameToID(dbm, tmp[0]);
			if (tID != -1){
			if (!attrnames.containsKey(tID)) attrnames.put(tID, new ArrayList<String>());
			attrnames.get(tID).add(tmp[1]);
			}else{
				continue;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		}
		//for (String e : attrnames){
		//	System.out.println(e);
		//}
	}
	
	private void Joins(String s){
		int on_condition,tid;
		int[] f = find(s, "\\s+on\\s+");
		if (f !=null){
			on_condition = f[0];
			String JInfo = s.substring(on_condition);
			s = s.substring(5, on_condition);
			String[] rs = s.split("\\s*,\\s*");
			for (String rname : rs){
				Relation r = new Relation(rname);
				try{
					tid = DBTool.tabNameToID(dbm,rname);
					if (tid >= 0){
					r.setRelation_id(tid);
					Relations.add(r);
					int[] infoloc = this.find(JInfo, "\\s+"+rname+".\\w+");
					String info = JInfo.substring(infoloc[0], infoloc[1]);
					String[] tmp = info.split("\\.");
					On_Conditions.add(new Pair<Integer,String>(tid, tmp[1]));
					for (Relation rr : Relations){
						if (rr.getRelation_id() == tid) rr.addSortattrs(tmp[1]);
					}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private void Where(String s){
		Pair newTermpair,newExprpair;
		s = s.substring(6);
		//Splitting the where clause into a set of sub conditions
        or_conditions=s.split("\\sor\\s(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        for(int ii=0;ii<or_conditions.length;ii++){
            and_conditions.add(or_conditions[ii].split("\\sand\\s(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
        }
        //Dispatching Attributes and Parsing sub-conditions
        try{
        List<String[]> sublists = this.throwCondition();
        for (String[] sub : sublists){
        	int groupno = Integer.parseInt(sub[3]);
    		ExpressionParser Left = AttrDispatcher(sub[1]);
    		ExpressionParser Right = AttrDispatcher(sub[2]);
    		if (Right.getID() == -1) Right.setID(Left.getID());
			newTermpair = new Pair<ExpressionParser,ExpressionParser>(Left,Right);
			newExprpair = new Pair<String, Pair>(sub[0], newTermpair);
    		if (Left.getID() == Right.getID()){
    			//Same Table; Using Dispatched
            	if (!Dispatched.containsKey(groupno)){
            		Map<Integer,List<Pair>> map = new HashMap<Integer,List<Pair>>();
            		Dispatched.put(groupno, map);
            	}
        		if (!Dispatched.get(groupno).containsKey(Left.getID())){
        			Dispatched.get(groupno).put(Left.getID(), new ArrayList<Pair>());
        		}
    			Dispatched.get(groupno).get(Left.getID()).add(newExprpair);
    		}else{
    			//Cross Table. Here We just assume there only exists one cross selection;
    			Pair newpair = new Pair<Integer,Integer>(Left.getID(),Right.getID());
    			CrossTable.put(newpair, newExprpair);
    		}
        }
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	private ExpressionParser AttrDispatcher(String expr) throws Exception{
		int tID = -1;
		Pattern p = Pattern.compile("[a-zA-Z][\\w\\d]*\\.\\w+");
		Matcher m = p.matcher(expr);
		while (m.find()){
			String s = m.group();
			String[] tmp = s.split("\\.");
			tID = DBTool.tabNameToID(dbm, tmp[0]);
			expr = expr.replaceAll(s, tmp[1]);
		}
		ExpressionParser exprp = new ExpressionParser(expr);
		exprp.setID(tID);
		return exprp;
	}
	
	
	   public List<String[]> throwCondition()throws Exception {
	        List<String[]> res=new ArrayList<>();
	        for(int ii=0;ii<and_conditions.size();ii++)
	            for(int jj=0;jj<and_conditions.get(ii).length;jj++){
	                res.add(assertCondition(and_conditions.get(ii)[jj],ii));
	            }
	        return res;}
	    //transform the subcondition into string array which will be further handled by handleCondition method
	    private static String[] assertCondition(String c,int andGroupNo) throws Exception {
	        if (c.trim().equals("")) return new String[0];
	        String h="(?<=[\\d\\w '\"])",t="(?=[\\d\\w '\"])";
	        Pattern lessThen=Pattern.compile(h+"<"+t),greaterThen=Pattern.compile(h+">"+t)
	                ,unequal=Pattern.compile(h+"\\(<>|!=\\)"+t),equal=Pattern.compile(h+"="+t)
	                ,lessEqual=Pattern.compile(h+"<="+t),greaterEqual=Pattern.compile(h+">="+t);
	        int lt=matchCounter(lessThen.matcher(c))
	                ,le=matchCounter(lessEqual.matcher(c))
	                ,gt=matchCounter(greaterThen.matcher(c))
	                ,ge=matchCounter(greaterEqual.matcher(c))
	                ,eq=matchCounter(equal.matcher(c))
	                ,ue = matchCounter(unequal.matcher(c));
	        if((lt+le+ge+gt+eq+ue)==1) {
	            String[] res= new String[4],vars=c.split(h+"=|=|!=|<=|>=|<>|>|<"+t);
	            if (lt==1){ res[0]="<";res[1]=vars[0].trim();res[2]=vars[1].trim();}
	            if (le==1){ res[0]="<=";res[1]=vars[0].trim();res[2]=vars[1].trim();}
	            if (gt==1){ res[0]=">";res[1]=vars[0].trim();res[2]=vars[1].trim();}
	            if (ge==1){ res[0]=">=";res[1]=vars[0].trim();res[2]=vars[1].trim();}
	            if (eq==1){ res[0]="=";res[1]=vars[0].trim();res[2]=vars[1].trim();}
	            if (ue==1){ res[0]="!=";res[1]=vars[0].trim();res[2]=vars[1].trim();}
	            res[3]= String.valueOf(andGroupNo);
	            return res;
	        }
	        else throw new Exception("Can't determine the condition");
	    }
	    
	    //count regexp results
	    private static int matchCounter(Matcher m){
	        int count=0;
	        while(m.find()) count++;
	        return count;
	    }
	    
	    public Map<Integer, Map<Integer,List<Pair>>> getDispatched(){
	    	return this.Dispatched;
	    }
	    
	    public Map<Pair<Integer,Integer>, Pair<String,Pair>> getCrossTable(){
	    	return this.CrossTable;
	    }
	    public List<Pair<Integer,String>> getJInfo(){
	    	return this.On_Conditions;
	    }
	    public Map<Integer,List<String>> getAttrnames(){
	    	return this.attrnames;
	    }
	    public List<Relation> getRelations(){
	    	return this.Relations;
	    }
	
    
    public static void main(String[] args){
    	Parser par = new Parser("select Movies.x1,Movies.x3,Movies1.x4 from Movies, Movies1 on Movies.year = Movies1.year"
    			+ " where Movies.year > 0.8 * Movies1.year and Movies.price > 1  and Movies.Title = \"dsdsdssd\" or Movies.country=\"usa\" ");
    }
	

}
