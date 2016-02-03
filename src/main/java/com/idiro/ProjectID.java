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

package com.idiro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Keep a track of the version of the software.
 * The name and the version has to be parsed 
 * from the main java method, it does not read into
 * a file.
 * @author etienne
 *
 */
public class ProjectID {

	/**
	 * Name of the program
	 */
	private String name = null;

	/**
	 * Version of the program
	 */
	private String version = null;

	private static ProjectID instance = new ProjectID();

	private ProjectID(){
		
		try {
			InputStream is = getClass().getResourceAsStream( "/META-INF/application.properties" );
			Properties prop = new Properties();
			prop.load(is);
			name = prop.getProperty("artifactId");
			version = prop.getProperty("version");
		} catch (IOException e) {
			System.out.println("IOException cannot load name and version: "+e.getMessage());
		}catch (Exception e) {
			System.out.println("No application.properties file found");
		}
	}


	public static String get(){
		return instance.name+"-"+instance.version;
	}

	public static boolean isInit(){
		return !instance.name.isEmpty() && !instance.version.isEmpty();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Now the project name have to be set from an application.properties
	 * file, in the META-INF directory of the jar
	 * @param name the name to set
	 */
	@Deprecated
	public void setName(String name) {
		if(instance.name == null)
			this.name = name;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Now the project version have to be set from an application.properties
	 * file, in the META-INF directory of the jar
	 * @param version the version to set
	 */
	@Deprecated
	public void setVersion(String version) {
		if(instance.version == null)
			this.version = version;
	}

	/**
	 * @return the instance
	 */
	public static ProjectID getInstance() {
		return instance;
	}

}
