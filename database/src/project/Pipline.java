package project;


import project.relations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vincent on 11/14/15.
 */
public class Pipline {
	//Single Table Selection.The Integer in the outer Map is the groupno.
	//Pair<Operator,Pair<Expr,Expr>> Operator:" >, < >=, <=, !=, ="
    private List<Relation> Relations;
    private Map<Pair<Integer,Integer>,Pair<String,Pair>> crosstab;
    private Map<Integer, Map<Integer,List<Pair>>> Dispatched;
    private Map<Integer,List<String>> attrnames; //Projection Info. Integer for tID, List<String> for corresponding attrs
    private List<Pair<Integer,String>> On_Conditions; // Join info. Integer for tID, String for Attrname
    private AlgebraNode root;
    public Pipline(Parser p) throws Exception {
    	Dispatched = p.getDispatched();
    	attrnames = p.getAttrnames();
    	On_Conditions = p.getJInfo();
        Relations=p.Relations;
        crosstab=p.getCrossTable();
        construct();
    }
    private void construct() throws Exception {
        if(On_Conditions.size()!=2){
            throw new Exception("Not two tables join!");
        }
        ProjectOperator ProjNode=new ProjectOperator(attrnames);
        JoinOperator joinNode=new JoinOperator(On_Conditions, crosstab);
        for(Relation r:this.Relations){
            SelectOperator SlctNode=new SelectOperator(Dispatched.get(r.getRelation_id()),null);
            SlctNode.attach(r);
            joinNode.attach(SlctNode);
        }
        ProjNode.attach(joinNode);
        root=ProjNode;
        System.out.println(root);
    }
    public void exec(){
        root.open();
        for(int i=0;i<5;i++)
            root.getNext();
    }
    public static void main(String[] args){
        Parser par = new Parser("select country.code from country,city on country.code=city.countrycode "
                +"where 0.4*country,population<=city.population and city.population<1000000");

        try {
            Pipline p= new Pipline(par);
            p.exec();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
