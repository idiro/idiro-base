package com.idiro.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Create a new file from a template.
 * 
 * The class works like a "sed -e", you 
 * give the variables associated to their
 * value and it will change all the occurence
 * of the variable. The class implement a main
 * method.
 *  
 * @author etienne
 *
 */
public class CreateFileFromTemplate {

	/**
	 * Create a file from a template and from variables to change
	 * @param templateFile the template file
	 * @param outputFile the output file
	 * @param words map in which the key are the variables and their value are the variables value
	 * @return true if it processed ok
	 */
	public boolean create(String templateFile, String outputFile,Map<String,String> words){
		boolean ok = true;
		try{
			BufferedReader br = new BufferedReader(new FileReader(templateFile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null){
				Iterator<String> it = words.keySet().iterator();
				while(it.hasNext()){
					String key = it.next();
					strLine = strLine.replace(key, words.get(key));
				}
				bw.write(strLine+"\n");
			}
			br.close();
			bw.close();
		}catch(Exception e){
			ok = false;
		}
		return ok;
	}
	
	/**
	 * Call the create function.
	 * @param args 0: the input file, 1: the output file, 2 to n: | delimited arg
	 * such as "variable"|"value"
	 * 
	 * @throws Exception if anything goes wrong
	 * @see CreateFileFromTemplate#create(String, String, Map)
	 */
	public static void main(String[] args) throws Exception{
		if(args.length < 2){
			throw new Exception("Needs at least 2 args: the template and the output file");
		}
		
		File in = new File(args[0]);
		File out = new File(args[1]);
		if(!in.exists()){
			throw new Exception(args[0]+" does not exists");
		}
		if(out.exists()){
			out.delete();
		}
		
		if(args.length == 2){
			LocalFileSystem.copyfile(args[0], args[1]);
			return;
		}
		
		Map<String,String> words = new LinkedHashMap<String,String>();
		for(int i = 2; i < args.length;++i){
			String[] split = args[i].split("\\|");
			if(split.length != 2){
				throw new Exception("Dictionary not conformed, "+split.length+" number of field instead of 2");
			}
			words.put(split[0],split[1]);
		}
		
		if(! new CreateFileFromTemplate().create(args[0],args[1],words)){
			throw new Exception("Did not succeed to create file from template");
		}
		
	}
}
