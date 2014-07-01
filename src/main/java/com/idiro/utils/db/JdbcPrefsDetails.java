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
