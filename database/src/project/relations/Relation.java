package project.relations;

import java.util.List;

/**
 * Created by wangqian on 11/9/15.
 */
public class Relation implements AlgebraNode {
    private List<AlgebraNode> observers;
    private int relation_id;
    private String relation_name;

    public Relation(){

    }

    public void attach(AlgebraNode node){
        this.observers.add(node);
    }

    public void dettach(AlgebraNode node){
        this.observers.remove(node);
    }

    public List<AlgebraNode> getObservers(){
        return this.observers;
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

    @Override
    public String toString() {
        return "Relation{" +
                "relation_id=" + relation_id +
                ", relation_name='" + relation_name + '\'' +
                '}';
    }
}
