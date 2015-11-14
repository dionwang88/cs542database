package project;


import java.util.List;
import java.util.Map;

/**
 * Created by vincent on 11/14/15.
 */
public class Pipline {
	//Single Table Selection.The Integer in the outer Map is the groupno.
	//Pair<Operator,Pair<Expr,Expr>> Operator:" >, < >=, <=, !=, ="
    private Map<Integer, Map<Integer,List<Pair>>> Dispatched;
    private Map<Pair<Integer,Integer>, Pair<String,Pair>> CrossTable;
    private Map<Integer,List<String>> attrnames; //Projection Info. Integer for tID, List<String> for corresponding attrs
    private List<Pair<Integer,String>> On_Conditions; // Join info. Integer for tID, String for Attrname
    public Pipline(Parser p){
    	Dispatched = p.getDispatched();
    	CrossTable = p.getCrossTable();

    }
}
