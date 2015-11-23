package project;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Created by vincent on 11/22/15.
 */
public class DBRecovery {
	private static String BRACKET = "<";
	private static String CHECKPOINT = "checkingpoint";
	private static String INTEGER = "Ingeter";
	private static String DOUBLE = "Double";
	private static String STRING = "String";
	
	public static void logUpdate(int tid, String attrName, int type, Object oldVal,Object newVal){
		writeIntoLog("","");
	}

	/**
	 * Read Logging file
	 * @param path: logging file path
	 * @return LogObj List
	 */
	public List<LogObj<?>> logLoad(String path){
		BufferedReader br = null;
		Stack<String> stack_line = new Stack<>();
		List<LogObj<?>> list_logObj = new ArrayList<LogObj<?>>();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				stack_line.push(line);
			}
			
			// split the line into parts
			while(!stack_line.isEmpty()){
				line = stack_line.pop().toLowerCase();
//				System.out.println(line);
				if(line.indexOf(DBRecovery.CHECKPOINT) != -1){
					return list_logObj;
				}
				if(line.indexOf(BRACKET) != -1){
					list_logObj.add(this.parseLog(line.substring(1, line.length()-1)));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Log File '" + "'" + " didn't exist.");
		} catch(Exception e){ 
			e.printStackTrace();
		} finally{
			if(br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}
	
	private LogObj<?> parseLog(String line){
		StringTokenizer st = new StringTokenizer(line, ",");
		List<String> lst = new ArrayList<>();
		String sign = DBRecovery.INTEGER;
		while(st.hasMoreElements()){
			String value = st.nextElement().toString().trim();
			
			if(value.indexOf(".") != -1) sign = DBRecovery.DOUBLE;
			else if(value.indexOf("\"") != -1 || value.indexOf("'") != -1) sign = DBRecovery.STRING;
			
			lst.add(value);
		}
		
		if(sign.equals(DBRecovery.INTEGER)){
			LogObj<Integer> lo = new LogObj<Integer>();
			lo.setTableId(Integer.parseInt(lst.get(0))); // TableID
			lo.setVarName(lst.get(1)); //Variable Name
			lo.setOldValue(Integer.parseInt(lst.get(2))); // Old Value
			lo.setNewVlaue(Integer.parseInt(lst.get(3))); // New Value
			
			return lo;
		}
		if(sign.equals(DBRecovery.DOUBLE)){
			LogObj<Double> lo = new LogObj<Double>();
			lo.setTableId(Integer.parseInt(lst.get(0))); // TableID
			lo.setVarName(lst.get(1)); //Variable Name
			lo.setOldValue(Double.parseDouble(lst.get(2))); // Old Value
			lo.setNewVlaue(Double.parseDouble(lst.get(3))); // New Value
			
			return lo;
		}
		if(sign.equals(DBRecovery.STRING)){
			LogObj<String> lo = new LogObj<String>();
			lo.setTableId(Integer.parseInt(lst.get(0))); // TableID
			lo.setVarName(lst.get(1)); //Variable Name
			lo.setOldValue(lst.get(2).substring(1, lst.get(2).length()-1)); // Old Value
			lo.setNewVlaue(lst.get(3).substring(1, lst.get(3).length()-1)); // New Value
			
			return lo;
		}

		return null;
	}

    private static void writeIntoLog(String path,String row){

    }

    public static void Recover(){

    }
    
    /**
     * @param args
     */
    public static void main(String[] args){
    		DBRecovery dbr = new DBRecovery();
    		String path = "logging.txt";
    		List<LogObj<?>> st = dbr.logLoad(path);
    		for(LogObj<?> a: st){
    			System.out.println(a);
    		}
    	
//    		String log1 = "1, name, \"ddd\", 'aaa'";
//    		String log2 = "2, value, 1, 2";
//    		String log3 = "3, value, 0.1, 0.3";
//    		
//    		
//    		LogObj<?> lo1 = dbr.parseLog(log1);
//    		System.out.println(lo1);
//    		LogObj<?> lo2 = dbr.parseLog(log2);
//    		System.out.println(lo2);
//    		LogObj<?> lo3 = dbr.parseLog(log3);
//    		System.out.println(lo3);
    		
    }
}
