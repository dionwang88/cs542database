package project;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
public class ReadManager {
	public static Map<Integer,ArrayList<String>>  ReadFile(String Filepath) {
		BufferedReader br = null;
		String line = "";
		String sep = ","; //use comma as separator
		Map<Integer,ArrayList<String>> Records = new HashMap<Integer,ArrayList<String>>();
		ArrayList<String> attributes;
		
		try{
			br = new BufferedReader(new FileReader(Filepath));
			int i = 1;
			while ((line = br.readLine()) != null) {
				attributes = new ArrayList<String>();
				String[] record = line.split(sep);
				for (int j = 0; j < record.length; j ++){
					attributes.add(record[j]);
				}
				Records.put(i, attributes);
				i++;
			}
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	System.out.println("Done");
	return Records;
	}
	
	public static void main(String[] args) {
		Map<Integer,ArrayList<String>> m = ReadFile("/Users/Xiang/Documents/GitHub/cs542database/database/movies.txt");
		System.out.println("DDDD");
	}
	
}

