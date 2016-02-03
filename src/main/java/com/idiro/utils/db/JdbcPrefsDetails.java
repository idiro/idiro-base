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

package com.idiro.utils.db;

/**
 * Use a Preference like interface to store jdbc details.
 * Stores key to retrieve password and username. These two
 * keys are build from the database url.
 * 
 * @author etienne
 *
 */
public abstract class JdbcPrefsDetails implements JdbcDetails{

	protected String dburl;
	
	//the two keys
	protected String passwdKey;
	protected String userKey;
	
	protected JdbcPrefsDetails(String dburl){
		this.dburl = dburl;
		resetKeys();
	}
	
	protected void resetKeys(){
		userKey = dburl + "_username";
		passwdKey = dburl+"_password";
	}
	
	public void reset(String dburl, String username, String password){
		setDburl(dburl);
		setUsername(username);
		setPassword(password);
	}
}
