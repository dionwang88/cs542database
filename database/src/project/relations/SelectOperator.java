package project.relations;

import java.util.List;
import java.util.ArrayList;
import project.Condition;
import project.DBManager;
import project.Pair;
import project.ExpressionParser;
import java.util.Map;
import java.util.HashMap;
import project.Parser;
/**
 * Created by wangqian on 11/9/15.
 */
public class SelectOperator implements AlgebraNode {
    private static String operator_name = "Select";
    private List<AlgebraNode> publishers;
    private Map<Integer,List<String[]>> FilteredC;
    private Map<Integer,List<Pair>> SingleTBCdt;
    private Map<Pair<Integer,Integer>, Pair<String,Pair>> CrossTbCdt;
    private Condition c;
    private int CNode;
    
    public SelectOperator(Map<Integer, Map<Integer,List<Pair>>> singTB, Map<Pair<Integer,Integer>, Pair<String,Pair>> CrossTB){
    	publishers = new ArrayList<AlgebraNode>();
    	FilteredC = new HashMap<Integer, List<String[]>>();
    	CrossTbCdt = CrossTB;
    	//Pre-processing single TB info;
    	if (singTB != null){
        	SingleTBCdt = new HashMap<Integer,List<Pair>>();
    		for (Map.Entry<Integer, Map<Integer,List<Pair>>> Entry : singTB.entrySet()){
    			int groupno = Entry.getKey();
    			if (!SingleTBCdt.containsKey(groupno)) SingleTBCdt.put(groupno, new ArrayList<Pair>());
    			for (List<Pair> lp : Entry.getValue().values()){
    				SingleTBCdt.get(groupno).addAll(lp);
    			}
    		}
    	}
    }



    public void attach(AlgebraNode node){
        this.publishers.add(node);
    }
    public void dettach(AlgebraNode node){
        this.publishers.remove(node);
    }

    @Override
    public void open(){
    	DBManager dbm = DBManager.getInstance();
    	if (publishers.size() > 0){
    		CNode = 0;
        	publishers.get(CNode).open();
    	}
    }

    @Override
    public List<Pair<Integer,Integer>> getNext() {
    	List<Pair<Integer,Integer>> l = null;
    	//if (!hasNext()){
    	//	publishers.Get(CNode).close();
    	//	if (CNode < publishers.size()){
    	//	CNode ++;
    	//	}
    	//	return null;
    	//}
    	DBManager dbm = DBManager.getInstance();
    	List<Pair<Integer,Integer>> receivedData = publishers.get(CNode).getNext();
    	if (receivedData != null){
        	l = new ArrayList<Pair<Integer,Integer>>();
    		int tID = receivedData.get(0).getLeft();
    		int rID = receivedData.get(0).getRight();
    		if (SingleTBCdt != null){
    			//Single Table Selection
    			for (List<Pair> Cond : SingleTBCdt.values()){
    				try{
        				if (handleCondition(Cond, dbm, rID, tID, tID)) l.add(new Pair<Integer,Integer>(tID,rID));
    				}catch (Exception e){
    					e.printStackTrace();
    				}
    			}
    			if (l.size() == 0) return this.getNext();
    		}else{
    			//Cross Table Selection
        		int tID2 = receivedData.get(1).getLeft();
        		int rID2 = receivedData.get(1).getRight();
        		List<Pair> listed = new ArrayList<Pair>();
        		listed.add(CrossTbCdt.get(new Pair(tID,tID2)));
    			try{
        			if (handleCondition(listed, dbm, rID, tID,tID)){
        				l.add(new Pair<Integer,Integer>(tID,rID));
        				l.add(new Pair<Integer,Integer>(tID2,rID2));	
        				}
    			}catch (Exception e){
    				e.printStackTrace();
    			}
    			}
    		if (l.size() == 0) return this.getNext();
    		}
		return l;
    }
    
    private static boolean isValid(String operand, Pair<String,Object> left, Pair<String,Object> right) throws Exception{
		boolean result = false;
    	String type = left.getLeft();
    	if (type.equals(right.getLeft())){
    		if (type == "Value"){
    			double val1 = Double.parseDouble(left.getRight().toString());
    			double val2 = Double.parseDouble(right.getRight().toString());
                switch (operand) {
                case "<":
                    result = val1 < val2;
                    break;
                case ">":
                    result = val1 > val2;
                    break;
                case ">=":
                	result = val1 >= val2;
                    break;
                case "<=":
                	result = val1 <= val2;
                    break;
                case "=":
                	result = val1 == val2;
                    break;
                case "!=":
                	result = val1 != val2;
                    break;
                default:
                    break;
            }
    		}else{
                String s1 = left.getRight().toString().toLowerCase();
                String s2 = right.getRight().toString().toLowerCase();
                switch (operand){
                case "<":
                case ">":
                case ">=":
                case "<=":
                    throw new Exception("Can't compare string with < or >");
                case "=":
                    result = s1.equals(s2);
                    break;
                case "!=":
                    result = !s1.equals(s2);
                    break;
                default:
                    break;
            }
    		}
    		
    	}
    	return result;
    }

    public static boolean handleCondition(List<Pair> conditions, DBManager dbm,int key,int tid1, int tid2) throws Exception {
        //if (conditions.get(0).length<4) return true;
    	byte[] tuple1, tuple2;
    	if (tid1 == tid2){
    		tuple1 = tuple2 = dbm.Get(tid1,key);
    	}else{
    		tuple1 = dbm.Get(tid1,key);
    		tuple2 = dbm.Get(tid2,key);
    	}
        for (Pair<String,Pair> exprp : conditions){
        	String operand = (String)exprp.getLeft();
        	ExpressionParser leftval = (ExpressionParser)exprp.getRight().getLeft();
        	ExpressionParser rightval = (ExpressionParser)exprp.getRight().getRight();
        	leftval.parse(tuple1, dbm);
        	rightval.parse(tuple2, dbm);
        	if (!isValid(operand,leftval.getExpr(),rightval.getExpr())) return false;
        }
        return true;
    }
    
    

    @Override
    public void close() {

    }
    
    

    public static String getOperator_name() {
        return operator_name;
    }

	
    public static void main(String[] args) {
    	AlgebraNode r1 = new Relation();
    	((Relation)r1).setRelation_name("movies");
    	AlgebraNode r2 = new Relation();
    	((Relation)r2).setRelation_name("movies1");
    	Parser p = new Parser("select Movies.x1,Movies.x3,Movies1.x4 from Movies, Movies1 on Movies.year = Movies1.year"
    			+ " where Movies.year > 1960 and Movies.title = \"the abyss\"");
    	SelectOperator s1 = new SelectOperator(p.getDispatched(),null);
    	s1.attach(r1);
    	s1.open();
    	List<Pair<Integer,Integer>> l;
    	while( (l = s1.getNext()) != null){
    		System.out.println(l.get(0).getRight());
    	}
    }
    //Useless Method here
	public boolean hasNext() {
		return CNode < publishers.size();
	}
}
