package project;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Condition {
    private String statement;
    String[] or_conditions;
    List<String[]> and_conditions=new ArrayList<>();

    public Condition(String s) {
        this.statement=removeExtraSpace(s);
        or_conditions=statement.split("\\sor\\s");
        for(int ii=0;ii<or_conditions.length;ii++){
            and_conditions.add(or_conditions[ii].split("\\sand\\s"));
        }
    }
    public Condition(){this("");}

    public List<String[]> throwCondition()throws Exception {
        List<String[]> res=new ArrayList<>();
        for(int ii=0;ii<and_conditions.size();ii++)
            for(int jj=0;jj<and_conditions.get(ii).length;jj++){
                res.add(assertCondition(and_conditions.get(ii)[jj],ii));
            }
        return res;}
    public String toString(){return statement;}

    public static boolean handleCondition(List<String[]> conditions, DBManager dbm,int key,int tid) throws Exception {
        List<Pair<String, Boolean>> results = new ArrayList<>();
        if (conditions.get(0).length<4) return true;
        for (String[] ss : conditions) {
            int type = -1;
            Object valInCondtn = ss[2], valInTab = dbm.getAttribute(key, ss[1]);
            for (int j = 1; j < dbm.getTabMeta().get(tid).size(); j++) {
                Pair p = dbm.getTabMeta().get(tid).get(j);
                if (ss[1].equals(((String) p.getLeft()).toLowerCase())) {
                    type = (int) ((Pair) p.getRight()).getLeft();
                    break;
                }
            }
            if (type == 0) {
                valInCondtn = Integer.parseInt(ss[2]);
                switch (ss[0]) {
                    case "<":
                        results.add(new Pair<>(ss[3], (int) valInTab < (int) valInCondtn));
                        break;
                    case ">":
                        results.add(new Pair<>(ss[3], (int) valInTab > (int) valInCondtn));
                        break;
                    case ">=":
                        results.add(new Pair<>(ss[3], (int) valInTab >= (int) valInCondtn));
                        break;
                    case "<=":
                        results.add(new Pair<>(ss[3], (int) valInTab <= (int) valInCondtn));
                        break;
                    case "=":
                        results.add(new Pair<>(ss[3], valInTab.equals(valInCondtn)));
                        break;
                    case "!=":
                        results.add(new Pair<>(ss[3], !valInTab.equals(valInCondtn)));
                        break;
                    default:
                        break;
                }
            } else {
                String[] tmp=((String) valInCondtn).split("\\\"|\\\'");
                if(tmp.length<2) throw new Exception("using \" \" for string value");
                valInCondtn=tmp[1];
                switch (ss[0]) {
                    case "<":
                    case ">":
                    case ">=":
                    case "<=":
                        throw new Exception("Can't compare string with < or >");
                    case "=":
                        results.add(new Pair<>(ss[3], ((String)valInTab).toLowerCase().equals(valInCondtn)));
                        break;
                    case "!=":
                        results.add(new Pair<>(ss[3], !((String)valInTab).toLowerCase().equals(valInCondtn)));
                        break;
                    default:
                        break;
                }
            }
        }
        boolean c = false;
        String last = "";
        for (Pair p : results) {
            if (last.equals(p.getLeft()))
                c = c && (boolean) p.getRight();
            else c = c || (boolean) p.getRight();
            last = (String) p.getLeft();
        }
        return c;
    }

    private static String[] assertCondition(String c,int andGroupNo) throws Exception {
        if (c.trim().equals("")) return new String[0];
        String h="(?<=[\\d\\w '\"])",t="(?=[\\d\\w '\"])";
        Pattern lessThen=Pattern.compile(h+"<"+t),greaterThen=Pattern.compile(h+">"+t)
                ,unequal=Pattern.compile(h+"\\(<>|!=\\)"+t),equal=Pattern.compile(h+"="+t)
                ,lessEqual=Pattern.compile(h+"<="+t),greaterEqual=Pattern.compile(h+">="+t);
        int lt=matchCounter(lessThen.matcher(c))
                ,le=matchCounter(lessEqual.matcher(c))
                ,gt=matchCounter(greaterThen.matcher(c))
                ,ge=matchCounter(greaterEqual.matcher(c))
                ,eq=matchCounter(equal.matcher(c))
                ,ue = matchCounter(unequal.matcher(c));
        if((lt+le+ge+gt+eq+ue)==1) {
            String[] res= new String[4],vars=c.split(h+"=|=|!=|<=|>=|<>|>|<"+t);
            if (lt==1){ res[0]="<";res[1]=vars[0].trim();res[2]=vars[1].trim();}
            if (le==1){ res[0]="<=";res[1]=vars[0].trim();res[2]=vars[1].trim();}
            if (gt==1){ res[0]=">";res[1]=vars[0].trim();res[2]=vars[1].trim();}
            if (ge==1){ res[0]=">=";res[1]=vars[0].trim();res[2]=vars[1].trim();}
            if (eq==1){ res[0]="=";res[1]=vars[0].trim();res[2]=vars[1].trim();}
            if (ue==1){ res[0]="!=";res[1]=vars[0].trim();res[2]=vars[1].trim();}
            res[3]= String.valueOf(andGroupNo);
            return res;
        }
        else throw new Exception("Can't determine the condition");
    }

    private static boolean is_W_or_D(char c){
        if(64<c&&c<91||96<c&&c<123||47<c&&c<58||c==42||c==34) return true;
        else return false;
    }
    static String removeExtraSpace(String s){
        if(s.equals("")) return s;
        char[] returnedChars=new char[s.length()],chars=s.toCharArray();
        char last=chars[0];
        int offset=0;
        returnedChars[offset++]=last;
        for(int i=1;i<chars.length;last=chars[i++]){
            if(last==' '&&chars[i]==' ') continue;
            returnedChars[offset++]=chars[i];
        }

        char[] returnedChars2= new char[returnedChars.length];
        offset=0;
        returnedChars2[offset++]=returnedChars[0];
        if(returnedChars.length==1) return new String(returnedChars2).trim().toLowerCase();
        int i;
        for(i=1;i<returnedChars.length-1;i++){
            if(returnedChars[i]!=' ')
                returnedChars2[offset++]=returnedChars[i];
            else if(is_W_or_D(returnedChars[i-1])^is_W_or_D(returnedChars[i+1]));
            else returnedChars2[offset++]=returnedChars[i];
        }
        returnedChars2[offset++]=returnedChars[i];
        return new String(returnedChars2).trim().toLowerCase();
    }
    private static int matchCounter(Matcher m){
        int count=0;
        while(m.find()) count++;
        return count;
    }

    public static void main(String[] args){
        Condition c=new Condition("year=1989 and country = \"USA\"");
        System.out.println(removeExtraSpace(c.toString()));
        try {
            System.out.println(handleCondition(c.throwCondition(), DBManager.getInstance(),1,0));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(removeExtraSpace("select * from movies where year=1989 and country=\"usa\"").split("\\\"|\\\'")[1]);
    }
}
