package project.relations;

import java.util.List;

/**
 * Created by wangqian on 11/9/15.
 */
public class Relation implements AlgebraNode {
    private int relation_id;
    private String relation_name;
    private List<AlgebraNode> observers;

    public void attach(AlgebraNode node){
        this.observers.add(node);
    }
    public void dettach(AlgebraNode node){
        this.observers.remove(node);
    }

    @Override
    public void publish(){

    }

    public Relation(){}

    @Override
    public void open() {

    }

    @Override
    public void getNext() {

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
        this.relation_name = relation_name;
    }
}
