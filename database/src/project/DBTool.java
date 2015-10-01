package project;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincent on 9/30/15.
 */
public class DBTool {
    private static DBTool dBTool = null;

    public static void main(String args[]){
        //test show within the class
        ArrayList l=new ArrayList();
        l.add(new Pair<Integer,Integer>(0,16));
        l.add(new Pair<Integer,Integer>(16,16));
        l.add(new Pair<Integer,Integer>(32,16));
        l.add(new Pair<Integer,Integer>(64,16));
        l.add(new Pair<Integer,Integer>(96,16));
        l.add(new Pair<Integer,Integer>(128,16));
        show(l);
    }

    public static void show(List<Pair<Integer,Integer>> freelist){
        int used=0;
        int total=Storage.DATA_SIZE;
        int[] lset= new int[freelist.size()];
        int[] rset= new int[freelist.size()];
        for(int i=0;i<freelist.size();i++){
            lset[i]=freelist.get(i).getLeft();
            rset[i]=freelist.get(i).getRight()-1;
            used+=rset[i];
        }
        System.out.println("Total space is "+total+"byte(s).\n Used space is "+used+"byte(s).\n Unused is "+(total-used)+"byte(s).");
        System.out.println("Free space location:");
        for(int i=0;i<freelist.size();i++){
            System.out.println("[ " + lset[i] + ", " + (lset[i] + rset[i]) + "]");
        }
    }

    public static DBTool getInstance(){
        if (dBTool == null){
            dBTool = new DBTool();
        }
        return dBTool;
    }
}
