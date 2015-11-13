package project.relations;

import java.util.List;
import java.util.ArrayList;
import project.Condition;
import project.DBManager;
import project.Pair;

import java.util.Map;
import java.util.HashMap;
/**
 * Created by wangqian on 11/9/15.
 */
public class SelectOperator implements AlgebraNode {
    private static String operator_name = "Select";
    private List<AlgebraNode> publishers;
    private Map<Integer,List<String[]>> FilteredC;
    private Condition c;
    private int CNode;
    
    public SelectOperator(String query){
    	publishers = new ArrayList<AlgebraNode>();
    	FilteredC = new HashMap<Integer, List<String[]>>();
    	c = new Condition(query);
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
    		List<String[]> fc = FilteredC.get(tID);
    		if (fc == null){ // Condition for this tID has not been initialized
    			try{
    	    		ArrayList<String> addedAttrNames=new ArrayList<>();
    	    		fc = new ArrayList<String[]>();
    	    		for (String[] ss : c.throwCondition()){
    	    			if (dbm.isAttribute(tID, ss[1])){
    	    				addedAttrNames.add(ss[1]);
    	    				fc.add(ss);
    	    			}
    	    		}
    	    		FilteredC.put(tID, fc);
    	    		}catch(Exception e){
    	    			e.printStackTrace();
    	    		}
    		}
    		try{
    		if (Condition.handleCondition(fc,dbm,rID,tID)){
    			l.add(new Pair<Integer,Integer>(tID,rID));
    		}else{
    			return this.getNext();
    		}
    		}catch (Exception e){
    			e.printStackTrace();
    		}
    	}
		return l;
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
    	SelectOperator s1 = new SelectOperator("year > 1989 and country = \"USA\"");
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
