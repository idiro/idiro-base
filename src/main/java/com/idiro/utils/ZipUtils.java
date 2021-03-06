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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

public class ZipUtils {

	private Logger logger = Logger.getLogger(getClass());

	/**
	 * Unzip it
	 * @param zipFile input zip file
	 * @param output zip file output folder
	 */
	public void unZipIt(File zipFile, File outputFolder){

		byte[] buffer = new byte[1024];

		try{

			if(!outputFolder.exists()){
				outputFolder.mkdirs();
			}

			//get the zip file content
			ZipInputStream zis = 
					new ZipInputStream(new FileInputStream(zipFile));
			//get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while(ze!=null){

				String fileName = ze.getName();
				File newFile = new File(outputFolder, fileName);

				logger.debug("file unzip : "+ newFile.getAbsoluteFile());

				if(ze.isDirectory()){
					newFile.mkdirs();
				}else{
					//create all non exists folders
					//else you will hit FileNotFoundException for compressed folder
					newFile.getParentFile().mkdirs();
					FileOutputStream fos = new FileOutputStream(newFile);             

					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}

					fos.close();   
				}
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

		}catch(IOException ex){
			ex.printStackTrace(); 
		}
	} 
	
	/**
	 * Zip it
	 * @param zipFile output ZIP file location
	 */
	public void zipIt(File inputFolder, File zipFile){

		byte[] buffer = new byte[1024];

		try{
			String parentInputFolder = inputFolder.getParent();
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			List<String> fileList = generateFileList(parentInputFolder, inputFolder);
			for(String file : fileList){
				ZipEntry ze= new ZipEntry(file);
				zos.putNextEntry(ze);

				FileInputStream in = 
						new FileInputStream(parentInputFolder + File.separator + file);

				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
			}

			zos.closeEntry();
			zos.close();
		}catch(IOException ex){
			ex.printStackTrace();   
		}
	}
	
	
	/**
	 * Traverse a directory and get all files,
	 * and add the file into fileList  
	 * @param node file or directory
	 */
	private List<String> generateFileList(String root, File node){
		List<String> ans = new LinkedList<String>();
		//add file only
		if(node.isFile()){
			ans.add(generateZipEntry(root, node.getAbsoluteFile().toString()));
		}

		if(node.isDirectory()){
			String[] subNote = node.list();
			for(String filename : subNote){
				ans.addAll(generateFileList(root, new File(node, filename)));
			}
		}
		return ans;
	}
	
	/**
     * Format the file path for zip
     * @param file file path
     * @return Formatted file path
     */
    private String generateZipEntry(String root, String file){
    	return file.substring(root.length()+1, file.length());
    }
}
