package project.relations;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import project.DBManager;
import project.Pair;
/**
 * Created by wangqian on 11/9/15.
 */
public class JoinOperator implements AlgebraNode {
    private static String operator_name = "Join";
    private List<AlgebraNode> publishers; // Here this list's size is said to be max 2.
    private Map<Integer,List<Pair<Integer,Integer>>> TuplesofLeft; 
	int current;
	private List<String> common; // Common attributes to join on.


    public JoinOperator(){
    	publishers = new ArrayList<AlgebraNode>();
    	TuplesofLeft = new HashMap<Integer,List<Pair<Integer,Integer>>>();
    }

    public void attach(AlgebraNode node){
        this.publishers.add(node);
    }
    public void dettach(AlgebraNode node){
        this.publishers.remove(node);
    }


    @Override
    public void open() {
    	DBManager dbm = DBManager.getInstance();
    	List<Pair<Integer,Integer>> l;
    	AlgebraNode left = publishers.get(0);
    	//Get everything from the left algebraNode
    	int no = 1;
    	while ((l=left.getNext())!=null){
    		TuplesofLeft.put(no, l);
    		no++;
    	}
    	AlgebraNode right = publishers.get(1);	
    	// Finding common attributes that should be joined on
    	List<String> common = new ArrayList<String>();
    	List<Pair<Integer,Integer>> leftr = TuplesofLeft.get(1);
    	for (Pair<Integer,Integer> p : leftr){
    		int tID = p.getLeft();
    		common = union(common,dbm.getTabMeta().get(tID));
    	}
    	for ()
    }
    
    private List<String> union(List<String> l1, List<Pair> l2){
    	for (Pair<String,Pair> p : l2){
    		String tmp = p.getLeft();
    		if (!l1.contains(tmp)){
    			l1.add(tmp);
    		}
    	}
    	return l1;
    }
    

    @Override
    public List<Pair<Integer,Integer>> getNext() {
    	Pair R1pair = receivedData.get(0);
    	Pair R2pair = receivedData.get(1);
    	for (int i = 0; i <receivedData.size(); i++){
    		int tid = receivedData.get(i).getLeft();
    		List<byte[]> tuple = receivedData.get(i).getRight();
    		//loop over all values. There are only two relations to join
    		for (String Attr : common){
    			
    		}
    		for (byte[] d : tuple){
        		String s = (String) dbm.getAttribute(tid, d, common.get(0));
    		}

    	}

    }

    @Override
    public void close() {

    }
    
    public void setCondition(String condition){
    	
    }


    public static String getOperator_name() {
        return operator_name;
    }

}
