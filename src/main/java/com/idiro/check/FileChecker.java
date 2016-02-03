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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 * Checks if a file is as expected or not
 * 
 * Tests a file and log the results
 * 
 * @author Etienne Dumoulin
 *
 */
public class FileChecker extends Checker{

	
	/**
	 * File to check
	 */
	private File file;

	/**
	 * File Canonical name of the file
	 */
	private String fileCanonicalName;

	/**
	 * @param file file to check
	 */
	public FileChecker(File file) {
		initialized = init(file);
	}

	/**
	 * @param filename name of the file to check
	 */
	public FileChecker(String filename) {
		if(filename != null){
			initialized = init( new File(filename));
		}else{
			logger.error("Try to check a file which has no name");
			initialized = false;
		}
	}
	
	/**
	 * Initialise a FileChecker instance 
	 * @param file
	 * @return
	 */
	protected boolean init(File file){
		boolean init = true;
		this.file = file;
		try {
			fileCanonicalName = file.getCanonicalPath();
		} catch (IOException e) {
			logger.error("The canonical file name has not been set : system querry problem");
			logger.error("File concerned: "+file);
			init = false;
		}
		return init;
	}


	/**
	 * Check if the file exists or not
	 * @return true if the file exists
	 */
	public boolean exists() {
		boolean exist = false;
		try {
			exist = file.exists();
			logger.debug("check existence of "+fileCanonicalName+", result: "+exist);	
		} catch (Exception e) {
			systemErrorLog();
		}
		return exist;
	}

	/**
	 * Check if the file is a directory
	 * @return true if the file is a directory
	 */
	public boolean isDirectory(){
		boolean isDir = false;
		try {
			isDir = file.isDirectory();
			logger.debug("check if "+fileCanonicalName+" is a directory, result: "+isDir);
			if(!isDir && !file.isFile()){
				exists();
			}

		} catch (Exception e) {
			systemErrorLog();
		}
		return isDir;
	}
	
	/**
	 * Check if the file is a file (opposed to a directory)
	 * @return true if it is
	 */
	public boolean isFile(){
		boolean isF = false;
		try {
			isF = file.isFile();
			logger.debug("check if "+fileCanonicalName+" is a file, result: "+isF);
			if(!isF && !file.isDirectory()){
				exists();
			}

		} catch (Exception e) {
			systemErrorLog();
		}
		return isF;
	}
	
	/**
	 * Check if the file is readable
	 * @return true if it is readable
	 */
	public boolean canRead(){
		boolean canR = false;
		try {
			canR = file.canRead();
			logger.debug("check if "+fileCanonicalName+" is readable, result: "+canR);
			if(!canR ){
				exists();
			}

		} catch (Exception e) {
			systemErrorLog();
		}
		return canR;
	}

	/**
	 * Check if the file is writable
	 * @return true if it is writable
	 */
	public boolean canWrite(){
		boolean canW = false;
		try {
			canW = file.canWrite();
			logger.debug("check if "+fileCanonicalName+" is writable, result: "+canW);
			if(!canW ){
				canRead();
			}

		} catch (Exception e) {
			systemErrorLog();
		}
		return canW;
	}
	
	/**
	 * Check if the file is executable
	 * @return true if it is executable
	 */
	public boolean canExecute(){
		boolean canE = false;
		try {
			canE = file.canExecute();
			logger.debug("check if "+fileCanonicalName+" is writable, result: "+canE);
			if(!canE ){
				canRead();
			}

		} catch (Exception e) {
			systemErrorLog();
		}
		return canE;
	}
	
	/**
	 * Check the size of a file
	 * @return the size
	 */
	public long getSize(){
		long size = 0L;
		try {
			size = file.length();
			logger.debug("check the size of "+fileCanonicalName+", result: "+size);
			if(size == 0L ){
				canRead();
			}

		} catch (Exception e) {
			systemErrorLog();
		}
		return size;
	}
	
	/**
	 * Check the place available in the file system
	 * @return the place available
	 */
	public long getSpaceAvailable(){
		long space = 0L;
		try {
			space = file.getUsableSpace();
			logger.debug("check the usable space in the partition of "+fileCanonicalName+", result: "+space);
			if(space == 0L ){
				canRead();
			}

		} catch (Exception e) {
			systemErrorLog();
		}
		return space;
	}
	
	/**
	 * Get the number of line in the file
	 * @return the number of line
	 */
	public int getNumberOfLine(){
		try {
			LineNumberReader lnr;
			lnr = new LineNumberReader(new FileReader(file));
			lnr.skip(Long.MAX_VALUE);
			logger.debug("Count the number of line in "+fileCanonicalName+", result: "+lnr.getLineNumber());
			int ans = lnr.getLineNumber(); 
			lnr.close();
			return ans;
		} catch (FileNotFoundException e1) {
			logger.error(e1.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return 0;	
	}

	/**
	 * Send a log for a system exception
	 */
	private void systemErrorLog(){
		logger.error("File "+fileCanonicalName+": system exception, please check the right of the path and the system settings");
	}
	
	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return fileCanonicalName;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		initialized = init(file);
	}
}
