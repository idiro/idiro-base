package com.idiro.utils.db;

import java.util.prefs.Preferences;

/**
 * Implements jdbc details for a local use.
 * 
 * Stores username/password details into 
 * the user preference tree.
 * 
 * @author etienne
 *
 */
public class JdbcLocalPrefsDetails extends JdbcPrefsDetails{

	/**
	 * pref pointing on the node where username, password and location are stored
	 */
	protected Preferences prefs =Preferences.userNodeForPackage(getClass());
	
	public JdbcLocalPrefsDetails(String dburl) throws Exception{
		super(dburl);
		if(prefs.get(userKey, "").isEmpty())
			throw new Exception("The database url is not link to any database details");
	}
	
	public JdbcLocalPrefsDetails(String dburl,String username,String password){
		super(dburl);
		setUsername(username);
		setPassword(password);
	}
	
	
	public void remove(){
		prefs.remove(passwdKey);
		prefs.remove(userKey);
	}
	
	/**
	 * @return the dburl
	 */
	public String getDburl() {
		return dburl;
	}

	/**
	 * @param dburl the dburl to set
	 */
	public void setDburl(String dburl) {
		String password = getPassword();
		String username = getUsername();
		prefs.remove(passwdKey);
		prefs.remove(userKey);
		this.dburl = dburl;
		resetKeys();
		prefs.put(passwdKey, password);
		prefs.put(userKey, username);
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return prefs.get(userKey, "");
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		prefs.put(userKey, username);
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return prefs.get(passwdKey, "");
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		prefs.put(passwdKey, password);
	}

	
}
