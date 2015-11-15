package project;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
public class ExpressionParser {
	private String exprString,type;
	private Stack<Double> vals;
	private Stack<String> Operands;
	//private List<String> output;
	private Object finalval;
	private OperatorPriority opsvr;
	private int tID;
	private class OperatorPriority{
		HashMap<String,Integer> priorityMap = new HashMap<String,Integer>();
		
		OperatorPriority(){
			priorityMap.put("+", 1);
			priorityMap.put("-", 1);
			priorityMap.put("*", 2);
			priorityMap.put("/", 2);
			priorityMap.put("%", 2);
		}
		
		int compareto(String op1, String op2) throws Exception{
			if (!priorityMap.containsKey(op1) || !priorityMap.containsKey(op2)){
				throw new RuntimeException("not an accept op :" +op1+" "+ op2);
			}
			return priorityMap.get(op1).compareTo(priorityMap.get(op2));
			
		}
		
		boolean acceptOperator(String s){
			return priorityMap.containsKey(s);
		}
	}
	
	
	public ExpressionParser(String exprString) {
		  this.exprString = exprString ;
		  opsvr = new OperatorPriority();
		  vals = new Stack<Double>();
		  Operands = new Stack<String>();
		  tID = -1;
		  finalval = null;
		  //output = new ArrayList<String>();
		 }
		 public String getExprString(){
		  return exprString ;
		 }
		 
	private boolean isOperand(String s){
	    Pattern operators = Pattern.compile("[\\+\\-\\*\\/\\(\\)]");
	    Matcher m = operators.matcher(s);
	    return m.find();
	}
	public void parse(byte[] tuple, DBManager dbm) throws Exception{
		Double val = 0.0;
		String op = null ;
    	String[] terms = exprStr2terms();
	    if (terms.length == 1) {
	    	finalval = getVal(terms[0],tuple, dbm);
	    	return;
	    }
		for(String term : terms){
			term= term.trim();
			if (isOperand(term)){
				op = term;
			}else{
				val = Double.parseDouble(getVal(term,tuple, dbm).toString()); op = null;
			}
		   if(op == null){ // value
		    vals.push(val);
		   }else{ // operator
		    String top = null ;
		    if("(".equals(op) || ")".equals(op)){
		    	if("(".equals(op)){
		    	      Operands.push(op);
		    	     }else if(")".equals(op)){
		    	      while( (top = Operands.pop()) != null && 
		    	        (!top.equals("("))){
		    	    	  doOperator(top);
		    	   }
		    }
		    }else if(opsvr.acceptOperator(op)){
		    	if (Operands.size() > 0 && (top = Operands.peek()) != null &&
		    		(!top.equals("(")) && (opsvr.compareto(top, op)>=0)){
		    		top = Operands.pop();
		    		doOperator(top);
		    	}
		    	Operands.push(op);
		    }else{
		     System.err.println("Unsupported operator : " + op);
		   }
		   }
		 }
		  while(Operands.size() != 0){
			  doOperator(Operands.pop());
			  }
		  finalval = vals.pop();
		  
		  //if (output.isEmpty()){
			//  output.add(vals.pop());
		  //}
	}
	
	//private void write(String op){
	//String t1 = vals.pop();
	//if (output.isEmpty()){
	//	String t2 = vals.pop();
  	//  output.add(t2);
  	//output.add(op);
  	//output.add(t1);
	//}else{
	//	output.add(op);
	//	output.add(t1);
	//}
	//}
	
	private Object getVal(String attr,byte[] tuple, DBManager dbm) throws Exception{
		Object result;
		int type = 1;
		Pattern p = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
		Matcher m = p.matcher(attr);
		if (m.find()){
			this.type = "Value";
			return Double.parseDouble(attr);
		}
		result = dbm.getAttribute(tID, tuple, attr);
		if (result == null){
			this.type = "String";
			result = attr;
		}
        for (int j = 1; j < dbm.getTabMeta().get(tID).size(); j++) {
            Pair pp = dbm.getTabMeta().get(tID).get(j);
            if (attr.equals(((String) pp.getLeft()).toLowerCase())) {
                type = (int) ((Pair) pp.getRight()).getLeft();
                break;
            }
        }
        //Based on type , we know what to parse
        if (type == 0 || type == 2){
        	this.type = "Value";
        	return Double.parseDouble(result.toString());
        }
        else{
        	this.type = "String";
        	return result;
        }
	}
	
	 private void doOperator(String op) {
		  char opCh = op.toCharArray()[0];
		  double num2 = 0, num1 = 0;
		  switch(opCh){
		  case '+':
		   num2 = vals.pop();
		   num1 = vals.pop();
		   vals.push(num1+num2);
		   break;
		  case '-': 
		   num2 = vals.pop();
		   num1 = vals.pop();
		   vals.push(num1-num2);
		   break;
		  case '*': 
		   num2 = vals.pop();
		   num1 = vals.pop();
		   vals.push(num1*num2);
		   break;
		  case '/': 
		   num2 = vals.pop();
		   num1 = vals.pop();
		   vals.push(num1/num2);
		   break;
		  case '%': 
		   num2 = vals.pop();
		   num1 = vals.pop();
		   vals.push(num1%num2);
		   break;
		  }
		 }
	 
	 public void setID(int ID){
		 this.tID = ID;
	 }
	 
	 public int getID(){
		 return tID;
	 }
	
	public Pair<String,Object> getExpr(){
		return new Pair<String,Object>(type,finalval);
	}
		 private String[] exprStr2terms() {
		if (exprString.charAt(0) != '"') return exprString.split("\\s");
		else{
			String[] s = new String[1];
			s[0] = exprString.split("\"")[1];
			return s;
		}
		 }
		 
	public static void main(String[] args) {
		DBManager dbm = DBManager.getInstance();
		ExpressionParser ep = new ExpressionParser("0.1 * population");
		ep.setID(0);
		try{
		ep.parse(dbm.Get(0,9),dbm);
		}catch (Exception e){
			e.printStackTrace();
		}
		System.out.println(ep.getExpr().getLeft()+" "+ ep.getExpr().getRight());
	}

}
