package project.relations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import project.DBManager;
import project.Pair;

/**
 * Created by wangqian on 11/9/15.
 */
public class ProjectOperator implements AlgebraNode {
    private static String operator_name = "Project";
    private AlgebraNode publisher;
    private DBManager dbm=DBManager.getInstance();
    private Map<Integer, List<String>>attrNames;

    public ProjectOperator(Map<Integer, List<String>> attrNames) {
        this.attrNames=attrNames;
    }


    public String toString(){
        return operator_name+" : publisher-{ "+ publisher.toString()+" }";
    }
    public void attach(AlgebraNode node){
        this.publisher=node;
    }
    public void dettach(){
        this.publisher=null;
    }
    
    @Override
    public void open() {publisher.open();}

    @Override
    public List<Pair<Integer, Integer>> getNext() {
        List<Pair<Integer,Integer>> l;
        if((l=publisher.getNext())!=null){
            for(Pair p:l){
                int tid= (int) p.getLeft();
                int rid= (int) p.getRight();
                byte[] tuple=dbm.Get(tid,rid);
                if (tuple == null) continue;
                boolean isFirst = true;
                System.out.print(rid + ": ");
                for(String attrName:attrNames.get(tid)) {
                    if (isFirst) {
                        System.out.print(dbm.getAttribute(rid, tuple, attrName));
                        isFirst = false;
                    } else
                        System.out.print("|" + dbm.getAttribute(rid, tuple, attrName));
                }
                System.out.print('\n');
            }
            return new ArrayList<>();
        }
        else return null;
    }

    @Override
    public void close() {

    }

    public static String getOperator_name() {
        return operator_name;
    }
}
