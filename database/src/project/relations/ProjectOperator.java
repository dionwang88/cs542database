package project.relations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangqian on 11/9/15.
 */
public class ProjectOperator implements AlgebraNode {
    private static String operator_name = "Project";
    private List<AlgebraNode> observers;
    private List receivedData;


    public void attach(AlgebraNode node){
        this.observers.add(node);
    }
    public void dettach(AlgebraNode node){
        this.observers.remove(node);
    }

    public List getReceivedData(){
        return this.receivedData;
    }
    
	//do projection
	public List<String> tabProject(Relation r, String attrNames){
		List<String> res = new ArrayList<>();
		if(attrNames.trim().equals("*")){
			for(int i =1;i<tabMetadata.get(tid).size();i++)
				res.add((String) tabMetadata.get(tid).get(i).getLeft());
		}
		else {
			String[] strings = attrNames.toLowerCase().split(",");
			for (String s : strings)
				res.add(s.toLowerCase().trim());
		}
		return res;
	}
    
    @Override
    public void open() {

    }

    @Override
    public void getNext() {

    }

    @Override
    public void close() {

    }

    @Override
    public void publish() {

    }

    public static String getOperator_name() {
        return operator_name;
    }
}
