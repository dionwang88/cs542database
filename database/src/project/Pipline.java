package project;


import project.relations.Relation;
import java.util.List;
import java.util.Map;

/**
 * Created by vincent on 11/14/15.
 */
public class Pipline {
	//Single Table Selection.The Integer in the outer Map is the groupno.
	//Pair<Operator,Pair<Expr,Expr>> Operator:" >, < >=, <=, !=, ="
    private List<Relation> Relations;
    private Map<Integer, Map<Integer,List<Pair>>> Dispatched;
    private Map<Integer,List<String>> attrnames; //Projection Info. Integer for tID, List<String> for corresponding attrs
    private List<Pair<Integer,String>> On_Conditions; // Join info. Integer for tID, String for Attrname
    public Pipline(Parser p){
    	Dispatched = p.getDispatched();
    	attrnames = p.getAttrnames();
    	On_Conditions = p.getJInfo();
        Relations=p.Relations;
        construct();
    }
    private void construct(){
        for(Relation r:this.Relations){
            System.out.println(r);

        }
        /*
        for(int tid:this.Dispatched){

        }
        On_Conditions;
        for(int tid:this.attrnames){

        }
        //*/
    }
    public void exec(){

    }
    public static void main(String[] args){
        Parser par = new Parser("select Movies.x1,Movies.x3,Movies1.x4 from Movies, Movies1 on Movies.year = Movies1.year"
                + " where Movies.year > 0.8 * Movies1.year and Movies.price > 1  and Movies.Title = \"dsdsdssd\" or Movies.country=\"usa\" ");

        Pipline p= new Pipline(par);
    }
}
