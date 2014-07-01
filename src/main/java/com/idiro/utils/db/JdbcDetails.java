package com.idiro.utils.db;

import com.idiro.utils.Password;


/**
 * Save jdbc details and retrieve them.
 * This class is used to save login/password,
 * and retrieve them when it is necessary from the url.
 * 
 * This method suppose that one url correspond to one 
 * user with a password. You cannot connect to the same
 * database with two different database user accounts 
 * from the same unix account.
 * 
 * 
 * @author etienne
 *
 */
public interface JdbcDetails extends Password{

	/**
	 * Remove the old details and save the new details given
	 * @param dburl
	 * @param username
	 * @param password
	 */
	public void reset(String dburl, String username, String password);
		
	/**
	 * Remove the details from memory
	 */
	public void remove();
	
	/**
	 * @return the dburl
	 */
	public String getDburl();

	/**
	 * @param dburl the dburl to set
	 */
	public void setDburl(String dburl);

	/**
	 * @return the username
	 */
	public String getUsername();

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username);

	/**
	 * @return the password
	 */
	public String getPassword();

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password);
	

}
