package project;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by vincent on 9/30/15.
 */
public class DBTool {
    //private static DBTool dBTool = null;
/**
    public static void main(String args[]){
        //test show within the class
        //ArrayList l=new ArrayList();
        //l.add(new Pair<>(0, 16));
        /l.add(new Pair<>(16, 16));
        //l.add(new Pair<>(32, 16));
        //l.add(new Pair<>(64, 16));
        //l.add(new Pair<>(96, 16));
        //l.add(new Pair<>(128, 16));
        //show(l);
        DBManager dbmanager=DBManager.getInstance();
        int freesize=Storage.DATA_SIZE-dbmanager.get_DATA_USED();
        List<Pair<Integer, Integer>> al=freelist(dbmanager.getIndexBuffer());
        show(minusList(new Pair<>(0,Storage.DATA_SIZE),al),freesize);
    }
*/
    private DBTool(){}

    public static void show(List<Pair<Integer, Integer>> freelist, int free){
        if (free==0){System.out.println("Database is full.");}
        else if(freelist==null) {System.out.println("metadata disorder");}
        else {
            int total = Storage.DATA_SIZE;
            int[] lset = new int[freelist.size()];
            int[] rset = new int[freelist.size()];
            for (int i = 0; i < freelist.size(); i++) {
                lset[i] = freelist.get(i).getLeft();
                rset[i] = freelist.get(i).getRight() - 1;
            }
            System.out.println("Total space is " + total + "byte(s).\n Used space is " + (total - free) + "byte(s).\n Unused is " + free + "byte(s).");
            System.out.println("Free space location:");
            for (int i = 0; i < freelist.size(); i++) {
                System.out.println("[ " + lset[i] + ", " + (lset[i] + rset[i]) + "]");
            }
        }
    }

    public static List<Pair<Integer,Integer>> freelist(Map<Integer,Index> tab){
        List<Pair<Integer,Integer>> looplist=new ArrayList<>();
        for (int key:tab.keySet()) {
            Index tmplist=tab.get(key);
            looplist.addAll(tmplist.getIndexes().stream().collect(Collectors.toList()));
        }
        Index sortIndex=new Index();
        sortIndex.setIndexes(looplist);
        sortIndex.sortpairs();
        return sortIndex.getIndexes();
    }

    public static List<Pair<Integer,Integer>> minusList(Pair<Integer,Integer> F,List<Pair<Integer,Integer>> L){
        List<Pair<Integer,Integer>> rlist=new ArrayList<>();
        if((L.get(0).getLeft()<F.getLeft())
                ||(L.get(L.size()-1).getLeft()+L.get(L.size()-1).getRight()-1>F.getRight())){
            return null;
        }

        int ii=F.getLeft(),ll=0;
        for(Pair<Integer,Integer> p:L){
            ll=p.getLeft();
            if(ll==ii){
                ii=ll+p.getRight();
            } else if(ii<ll){
                rlist.add(new Pair<>(ii,ll-ii));
                ii=ll+p.getRight();
            }
            else{
                System.out.println("Bad pairs!!!");
                return null;
            }
        }
        if (ii<F.getRight()){
            rlist.add(new Pair<>(ii,F.getRight()-ii));
        }

        return rlist;
    }
    //public static DBTool getInstance(){
    //    if (dBTool == null){
    //        dBTool = new DBTool();
    //    }
     //   return dBTool;
    //}
}
