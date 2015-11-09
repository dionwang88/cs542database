package project.relations;

import java.util.List;

/**
 * Created by wangqian on 11/9/15.
 */
public class SelectOperator implements AlgebraNode {
    private static String operator_name = "Select";
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
