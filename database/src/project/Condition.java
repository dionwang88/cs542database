package project;


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
        or_conditions=statement.split(" or ");
        for(int ii=0;ii<or_conditions.length;ii++){
            and_conditions.add(or_conditions[ii].split(" and "));
        }
    }

    public List<String[]> throwCondition()throws Exception {
        List<String[]> res=new ArrayList<>();
        for(int ii=0;ii<and_conditions.size();ii++)
            for(int jj=0;jj<and_conditions.get(ii).length;jj++){
                res.add(assertCondition(and_conditions.get(ii)[jj],ii));
            }
        return res;}
    public String toString(){return statement;}

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

    private static String removeExtraSpace(String s){
        if(s.equals("")) return s;
        char[] returnedChars=new char[s.length()],chars=s.toCharArray();
        char last=chars[0];
        int offset=0;
        returnedChars[offset++]=last;
        for(int i=1;i<chars.length;last=chars[i++]){
            if(last==' '&&chars[i]==' ') continue;
            returnedChars[offset++]=chars[i];
        }
        return new String(returnedChars).trim();
    }
    private static int matchCounter(Matcher m){
        int count=0;
        while(m.find()) count++;
        return count;
    }

    public static void main(String[] args){
        try {
            Condition c=new Condition(" asasdfddf  =  asdf and asdf=asdf or asdofji<asdf ");
            for(int i=0;i<3;i++)
                for(int j=0;j<4;j++)
            System.out.println(c.throwCondition().get(i)[j]);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
