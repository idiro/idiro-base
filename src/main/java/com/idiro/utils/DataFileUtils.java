/** 
 *  Copyright © 2016 Red Sqirl, Ltd. All rights reserved.
 *  Red Sqirl, Clarendon House, 34 Clarendon St., Dublin 2. Ireland
 *
 *  This file is part of Utility Library developed by Idiro
 *
 *  User agrees that use of this software is governed by: 
 *  (1) the applicable user limitations and specified terms and conditions of 
 *      the license agreement which has been entered into with Red Sqirl; and 
 *  (2) the proprietary and restricted rights notices included in this software.
 *  
 *  WARNING: THE PROPRIETARY INFORMATION OF Utility Library developed by Idiro IS PROTECTED BY IRISH AND 
 *  INTERNATIONAL LAW.  UNAUTHORISED REPRODUCTION, DISTRIBUTION OR ANY PORTION
 *  OF IT, MAY RESULT IN CIVIL AND/OR CRIMINAL PENALTIES.
 *  
 *  If you have received this software in error please contact Red Sqirl at 
 *  support@redsqirl.com
 */

package com.idiro.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Helps to read/analyse a line of a data file.
 * 
 * @author etienne
 *
 */
public class DataFileUtils {

	private static Logger logger = Logger.getLogger(DataFileUtils.class);
	/**
	 * The quotes list accepted
	 */
	private static final char[] quotesChar = {'"','\''};
	/**
	 * The regex that a string has to validate 
	 * (at the moment of writing we avoid the '|')
	 */
	private static final String fieldStringReg = "^[^|]*$";  

	
	static public boolean validateHeader(String line, int fieldNumber,char delimiter){
		boolean ok = true;
		List<String> fields = DataFileUtils.getFields(line,delimiter);
		if(fieldNumber != fields.size()){
			ok = false;
			logger.error("Number of fields declare ("+fieldNumber+
					") does not match number of fields in the data ("+fields.size()+")");
		}else{
			Set<String> fieldsSet = new HashSet<String>(fields);
			if(fieldsSet.size() < fields.size()){
				logger.error("Number of field name different than number of field");
				logger.error(fields);
				logger.error(fieldsSet);
				ok = false;
			}
		}
		return ok;
	}
	
	static public boolean validateLine(Collection<String> type,String line, char delimiter){
		boolean ok = true;
		List<String> fields = getFields(line,delimiter);
		if(type.size() != fields.size()){
			ok = false;
			logger.error("Number of fields declare does not match number of fields in the data");
		}else{
			Iterator<String> itType = type.iterator();
			Iterator<String> itFields = fields.iterator();
			while(itType.hasNext() && ok){
				String typeCur = itType.next();
				String fieldCur = itFields.next();
				try{
					if(typeCur.equalsIgnoreCase("BOOLEAN")){
						ok = fieldCur.equalsIgnoreCase("true") || 
						fieldCur.equalsIgnoreCase("false") ||
						fieldCur.equals("0") ||
						fieldCur.equals("1");
						if(!ok){
							logger.error("A boolean should have a value as " +
								"'true','false','0' or '1', value found: '"+fieldCur+"'");
						}
					}else if(typeCur.equalsIgnoreCase("INT")){
						@SuppressWarnings("unused")
						int tmp = Integer.parseInt(fieldCur);
					}else if(typeCur.equalsIgnoreCase("DOUBLE")){
						@SuppressWarnings("unused")
						double tmp = Double.parseDouble(fieldCur);
					}else if(typeCur.equalsIgnoreCase("STRING")){
						if(!fieldCur.matches(DataFileUtils.getFieldStringReg())){
							logger.error("The '|' is kept by the software as default delimiter");
							ok = false;
						}
					}
				}catch(Exception e){
					logger.error("Conversion exception...");
					logger.error(e.getMessage());
					ok = false;
				}
			}
		}
		return ok;
	}
	
	
	/**
	 * Get the fields from a line
	 * @param line line of a data file
	 * @param delimiter delimiter used in the data file
	 * @return
	 */
	static public List<String> getFields(String line,char delimiter){
		String curLine = line;
		List<String> fields = new LinkedList<String>();
		if(line == null || line.isEmpty()){
			return fields;
		}
		int index = 0;
		do{
			//logger.debug("line :"+curLine);
			if(isQuote(curLine.charAt(0))){
				index = 0;
				do{
					index = curLine.indexOf(curLine.charAt(0),index+1);
				}while( index+1 < curLine.length() && curLine.charAt(index+1) != delimiter && index != -1);
				if(index == -1){
					logger.error("Error reading the file, line: "+line);
				}else if(index == curLine.length()-1){
					fields.add((curLine.substring(1,curLine.length()-1)));
				}else{
					fields.add(curLine.substring(1,index));
					++index;
				}
			}else{
				index = curLine.indexOf(delimiter);
				if(index == -1){
					fields.add(curLine.substring(0));
				}else{
					fields.add(curLine.substring(0,index));
				}
			}
			curLine = curLine.substring(index+1);
		}while(index != -1);
		return fields;
	}

	/**
	 * True if the character is within the quotesChar list
	 * @param c
	 * @return
	 */
	static boolean isQuote(char c){
		boolean found = false;
		int i = -1;
		while(!found && ++i < quotesChar.length){
			found = quotesChar[i] == c;
		}
		return found;
	}

	/**
	 * Get the line without quote
	 * @param line
	 * @param delimiter
	 * @return
	 */
	static public String getCleanLine(String line,char delimiterIn,char delimiterOut){
		Iterator<String> itFields = getFields(line,delimiterIn).iterator();
		String cleanLine = null;
		if(itFields.hasNext()){
			cleanLine = itFields.next();
		}
		while(itFields.hasNext()){
			cleanLine += delimiterOut + itFields.next();
		}
		return cleanLine;
	}

	/**
	 * Add quotes to a line without it.
	 * 
	 * @param line
	 * @param quotes the list of quotes to add in the right order. 
	 * If there is no quote, just have an empty String. null value will generate an error
	 * @param delimiter
	 * @return
	 */
	static public String addQuotesToLine(String line,Collection<String>quotes,char delimiter){
		List<String> fields = getFields(line,delimiter);
		String newLine = null,quote;
		if(fields.size() != quotes.size()){
			logger.error("Not the right number of quotes compare to the number of field");
			return null;
		}
		Iterator<String> itFields = fields.iterator();
		Iterator<String> itQuotes = quotes.iterator();
		if(itFields.hasNext()){
			quote = itQuotes.next();
			newLine = quote+itFields.next()+quote;
		}
		while(itFields.hasNext()){
			quote = itQuotes.next();
			newLine += delimiter + quote+ itFields.next()+quote;
		}
		return newLine;
	}

	/**
	 * @return the quotesChar
	 */
	public static char[] getQuotesChar() {
		return quotesChar;
	}

	/**
	 * @return the fieldstringreg
	 */
	public static String getFieldStringReg() {
		return fieldStringReg;
	}
}
