package project.relations;

import project.DBManager;
import project.Pair;
import project.ExpressionParser;
import project.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
/**
 * Created by vincent on 11/22/15.
 */
public class UpdateOperator  implements AlgebraNode{
    private boolean isOpen;
    private static String operator_name = "Update";
    private AlgebraNode publisher;
    private Map<Integer,List<Pair>> SingleTBCdt;
    private List<Pair<String,ExpressionParser>> To_Update;

    public UpdateOperator(Map<Integer, Map<Integer,List<Pair>>> conditions, List<Pair<String,ExpressionParser>> updateinfo){
        publisher = null;
        SingleTBCdt = new HashMap<Integer, List<Pair>>();
            for (Map.Entry<Integer, Map<Integer, List<Pair>>> entry : conditions.entrySet()) {
                List<Pair> l = new ArrayList<Pair>();
                for (List<Pair> ll : entry.getValue().values()) {
                    l.addAll(ll);
                }
                SingleTBCdt.put(entry.getKey(), l);
            }
        To_Update = updateinfo;
    }

    public void attach(AlgebraNode node){this.publisher = node;
    }
    public void dettach(AlgebraNode node){
        this.publisher = null;
    }

    @Override
    public void open(){
        publisher.open();
        isOpen = true;
    }

    @Override
    public List<Pair<Integer,Integer>> getNext(){
        DBManager dbm = DBManager.getInstance();
        List<Pair<Integer,Integer>> receivedData = null;
        while ((receivedData = publisher.getNext()) != null){
            int tID = receivedData.get(0).getLeft();
            int rID = receivedData.get(0).getRight();
            try{
                byte[] tuple = dbm.Get(tID,rID);
                boolean isvalid = true;
                for (List<Pair> subconds : SingleTBCdt.values()){
                    if (SelectOperator.handleCondition(subconds, dbm, rID, rID)){
                        isvalid = false;
                        break;
                    }
                }
                if (isvalid) {
                    for (Pair<String, ExpressionParser> p : To_Update) {
                        String attr = p.getLeft();
                        ExpressionParser parser = p.getRight();
                        parser.parse(tuple, dbm);
                        String type = parser.getExpr().getLeft();
                        Object newval = parser.getExpr().getRight();
                        dbm.setAttribute(rID, attr, newval);
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        dbm.Commit();
        return null;
    }

    @Override
    public void close() {

    }

    public static void main(String[] args){
        DBManager dbm = DBManager.getInstance();
        Parser p = new Parser("update", "Update Country set population = population * 1.2");
        UpdateOperator up = new UpdateOperator(p.getDispatched(),p.getUpinfo());
        for (Relation r : p.getRelations()){
            up.attach(r);
        }
        up.open();
        up.getNext();

    }
}
