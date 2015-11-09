package project.relations;

import java.util.List;

/**
 * Created by wangqian on 11/9/15.
 */
public class Join_Operator implements AlgebraNode {

    public Join_Operator(){

    }
    private List<AlgebraNode> observers;


    public void attach(AlgebraNode node){
        this.observers.add(node);
    }

    public void dettach(AlgebraNode node){
        this.observers.remove(node);
    }

    @Override
    public void open() {

    }

    @Override
    public List getNext() {
        return null;
    }

    @Override
    public void close() {

    }
}
