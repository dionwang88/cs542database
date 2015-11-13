package project.relations;

import java.util.ArrayList;
import java.util.List;

import project.Pair;

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
    
    @Override
    public void open() {

    }

    @Override
    public List<Pair<Integer,Integer>> getNext() {
    	return null;

    }

    @Override
    public void close() {

    }


    public static String getOperator_name() {
        return operator_name;
    }
}
