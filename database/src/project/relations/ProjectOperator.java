package project.relations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import project.DBManager;
import project.Pair;

public class ProjectOperator implements AlgebraNode {
    private static String operator_name = "Project";
    public AlgebraNode publisher;
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
    public void open() {
        publisher.open();
        String tbname;
        boolean isFirst = true;
        for (Map.Entry<Integer, List<String>> Entry : attrNames.entrySet()){
            int ID = Entry.getKey();
            if (ID ==0) tbname = "Country";
            else tbname = "City";
            for (String s : Entry.getValue()){
                if (isFirst){
                    System.out.print(tbname +"."+s);
                    isFirst = false;
                }else{
                    System.out.print("|"+tbname +"."+s);
                }
            }
        }
        System.out.print('\n');
    }

    @Override
    public List<Pair<Integer, Integer>> getNext() {
        List<Pair<Integer,Integer>> receivedData = publisher.getNext();
        if(receivedData !=null){
            boolean isFirst = true;
            for(Pair p:receivedData){
                int tid= (int) p.getLeft();
                int rid= (int) p.getRight();
                byte[] tuple=dbm.Get(tid,rid);
                if (tuple == null) continue;
                List<String> names = attrNames.get(tid);
                if (names != null){
                    for(String attrName : names) {
                        if (isFirst) {
                            System.out.print(dbm.getAttribute(tid, tuple, attrName));
                            isFirst = false;
                        } else
                            System.out.print("|" + dbm.getAttribute(tid, tuple, attrName));
                    }
                }
            }
            System.out.print('\n');
            return new ArrayList<>();
        }
        return receivedData;
    }

    @Override
    public void close() {

    }

    public static String getOperator_name() {
        return operator_name;
    }
}
