package project.relations;

import java.util.List;
import java.util.ArrayList;
import project.Index;
import project.DBManager;
import project.DBTool;
import project.Pair;
import java.util.Map;

/**
 * Created by wangqian on 11/9/15.
 */
public class Relation implements AlgebraNode{
    private int relation_id;
    private String relation_name;
    List<Integer> rIDs;
    int current;


    public Relation(String rname){
    	DBManager dbm = DBManager.getInstance();
        this.relation_name = rname;
        try{
        	int tID = DBTool.tabNameToID(dbm, rname);
        	if (tID == -1) throw new Exception("No such table!");
        this.relation_id = DBTool.tabNameToID(dbm, rname);
        }catch(Exception e){
        	e.printStackTrace();
        }
        this.rIDs = new ArrayList<Integer>();
    }

    @Override
    public void open() {
    	//table-scan;pre-fetch everything
    	DBManager dbm = DBManager.getInstance();
    	Map<Integer, Index> cIndex = dbm.getClusteredIndex();
    	for (int i =1; i < cIndex.size(); i ++){
    		int tID = cIndex.get(i).getTID();
    		if (relation_id == tID) rIDs.add(i);
    	}
    	current = 1;
    }

    @Override
    public List<Pair<Integer,Integer>> getNext() {
    	//May need to change later. Depends on how we send the data;
    	if (hasNext()) {
    		int rID = rIDs.get(current - 1);
    		current ++;
    		List<Pair<Integer,Integer>> l = new ArrayList<Pair<Integer,Integer>>();
    		l.add(new Pair(relation_id,rID));
    		return l;
    	}
    	else return null; // null represents notFound
    }

    @Override
    public void close() {

    }

    public int getRelation_id() {
        return relation_id;
    }

    public void setRelation_id(int relation_id) {
        this.relation_id = relation_id;
    }

    public String getRelation_name() {
        return relation_name;
    }

    public void setRelation_name(String relation_name) {

    }
    
    public static void main(String[] args) {
    	Relation r1 = new Relation("Country");
    	r1.open();
    	List<Pair<Integer,Integer>> l;
    	while((l = r1.getNext()) != null){
    		System.out.println(l.get(0).getRight());
    	}
    }

	private boolean hasNext() {
		return current <= rIDs.size();
	}

}
