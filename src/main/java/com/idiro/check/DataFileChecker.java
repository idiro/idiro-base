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

package com.idiro.check;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.idiro.utils.DataFileUtils;

/**
 * Checks/validates a given data file.
 * 
 * Check if a file is in an appropriate format.
 * A data file is a column delimited file ('|',','...).
 * Each column is associated with a quote ('','\'','"'...)
 * and a type.
 * If there is a header, the column titles have to be 
 * different from each other.
 * 
 * The class check if the first lines and the last lines
 * are respecting the format.
 * 
 * @author etienne
 *
 */
public class DataFileChecker extends FileChecker{

	private char delimiter;
	private int nbLineToCheck = 100;
	

	public DataFileChecker(File file, char delimiter) {
		super(file);
		this.delimiter = delimiter;
	}

	public DataFileChecker(String fileStr,char delimiter) {
		super(fileStr);
		this.delimiter = delimiter;
	}

	public boolean validate(Map<String,String> features,boolean header){
		List<String> types = new LinkedList<String>();
		Iterator<String> it = features.keySet().iterator();
		while(it.hasNext()){
			types.add(features.get(it.next()));
		}

		return validate(types,header);
	}

	public boolean validate(Collection<String> types, boolean header){
		boolean ok = true;

		if(!canRead()){
			ok = false;
		}else{

			//Check head
			try {
				BufferedReader r = new BufferedReader(new FileReader(getFile()));
				String line = null;
				int nbLineRead = 1;
				if(header){
					line = r.readLine();
					ok = DataFileUtils.validateHeader(line,types.size(),delimiter);
				}
				while( (line = r.readLine()) != null && ++nbLineRead < nbLineToCheck  && ok){
					if(!(ok = DataFileUtils.validateLine(types,line,delimiter))){
						logger.error("Line "+ nbLineRead);
					}
				}
				r.close();
			} catch (IOException e) {
				logger.error("Fail to read the head of "+getFilename());
				logger.error(e.getMessage());
				ok = false;
			}


			//Check tail
			if(ok){
				//We want to avoid the header... we go one line too far and come back
				String tail = tail(nbLineToCheck+1);
				if(tail != null){
					BufferedReader r = new BufferedReader(new StringReader(tail));
					String line = null;
					int nbLineRead = -nbLineToCheck;
					try {
						r.readLine();
						while( (line = r.readLine()) != null && ok){
							if(!(ok = DataFileUtils.validateLine(types,line,delimiter))){
								logger.error("Line "+ nbLineRead);
							}
							++nbLineRead;
						}
					} catch (IOException e) {
						logger.error("Fail to read the tail of "+getFilename());
						logger.error(e.getMessage());
						ok = false;
					}
				}else{
					logger.error("Fail to returns the tail of the file");
					ok = false;
				}
			}
		}

		return ok;
	}


	protected String tail(int lines) {
		File file = getFile();
		try {
			java.io.RandomAccessFile fileHandler = new java.io.RandomAccessFile( file, "r" );
			long fileLength = file.length() - 1;
			StringBuilder sb = new StringBuilder();
			int line = 0;

			for( long filePointer = fileLength; filePointer != -1; filePointer-- ) {
				fileHandler.seek( filePointer );
				int readByte = fileHandler.readByte();

				if( readByte == 0xA ) {
					line = line + 1;
					if (line == lines) {
						if (filePointer == fileLength) {
							continue;
						} else {
							break;
						}
					}
				} else if( readByte == 0xD ) {
					
					if (line == lines) {
						if (filePointer == fileLength - 1) {
							continue;
						} else {
							break;
						}
					}
				}
				sb.append( ( char ) readByte );
			}

			sb.deleteCharAt(sb.length()-1);
			String lastLine = sb.reverse().toString();
			fileHandler.close();
			return lastLine;
		} catch( java.io.FileNotFoundException e ) {
			e.printStackTrace();
			return null;
		} catch( java.io.IOException e ) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the delimiter
	 */
	public char getDelimiter() {
		return delimiter;
	}

	/**
	 * @param delimiter the delimiter to set
	 */
	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return the nbLineToCheck
	 */
	public int getNbLineToCheck() {
		return nbLineToCheck;
	}

	/**
	 * @param nbLineToCheck the nbLineToCheck to set
	 */
	public void setNbLineToCheck(int nbLineToCheck) {
		this.nbLineToCheck = nbLineToCheck;
	}

}
