package project.relations;

import java.util.*;

import project.Condition;
import project.DBManager;
import project.Pair;
import project.ExpressionParser;
import project.Parser;
/**
 * Created by wangqian on 11/9/15.
 */
public class SelectOperator implements AlgebraNode {
    private static String operator_name = "Select";
    private List<AlgebraNode> publishers;
    private Map<Integer,List<Pair>> SingleTBCdt;
    private Map<Pair<Integer,Integer>, Pair<String,Pair>> CrossTbCdt;
    private int CNode;

	public String toString(){
		return operator_name+" : publisher-"+publishers.toString();
	}
    public SelectOperator(Map<Integer, Map<Integer,List<Pair>>> singTB, Map<Pair<Integer,Integer>, Pair<String,Pair>> CrossTB){
    	publishers = new ArrayList<AlgebraNode>();
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
        				if (handleCondition(Cond, dbm, rID, rID)) l.add(new Pair<Integer,Integer>(tID,rID));
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
        			if (handleCondition(listed, dbm, rID, rID2)){
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

    public static boolean handleCondition(List<Pair> conditions, 
    		DBManager dbm,int key1,int key2) throws Exception {
        //if (conditions.get(0).length<4) return true;
    	byte[] tuple1, tuple2;
        for (Pair<String,Pair> exprp : conditions){
        	String operand = (String)exprp.getLeft();
        	ExpressionParser leftval = (ExpressionParser)exprp.getRight().getLeft();
        	int ltID = leftval.getID();
        	tuple1 = dbm.Get(ltID,key1);
        	ExpressionParser rightval = (ExpressionParser)exprp.getRight().getRight();
        	int rtID = rightval.getID();
        	tuple2 = dbm.Get(rtID,key2);
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
    	DBManager dbm = DBManager.getInstance();
    	AlgebraNode r1 = new Relation("country");
    	AlgebraNode r2 = new Relation("city");
    	Parser p = new Parser("select Country.code from Country, City on Country.code = city.CountryCode"
    			+ " where 0.4 * Country.population <= city.population ");
    	JoinOperator j1 = new JoinOperator(p.getJInfo(),p.getCrossTable());
    	j1.attach(r1);
    	j1.attach(r2);
    	SelectOperator s1 = new SelectOperator(null,p.getCrossTable());
    	s1.attach(j1);
    	s1.open();
    	List<Pair<Integer,Integer>> l;
    	int counter = 1;
    	while( (l = s1.getNext()) != null){
    		byte[] t1 = dbm.Get(0,l.get(0).getRight());
    		byte[] t2 = dbm.Get(1,l.get(1).getRight());
    		int tid1 = l.get(0).getLeft();
    		int tid2 = l.get(1).getLeft();
    		System.out.println("Same Code:"+ dbm.getAttribute(tid1, t1, "code") + " " +
    				dbm.getAttribute(tid1, t1, "Name")+" "+dbm.getAttribute(tid1, t1, "Population")+ " " +" "+
    				dbm.getAttribute(tid2, t2, "Name") + " "+ dbm.getAttribute(tid2, t2, "Population"));
    		if (counter % 100 ==0) System.out.println(counter);
    		counter++;
    		
    	}
    	System.out.println("Done!");
    }
    //Useless Method here
	public boolean hasNext() {
		return CNode < publishers.size();
	}
}
