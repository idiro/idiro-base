package idiro.check;

import idiro.utils.db.JdbcConnection;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Do basic check on a database through jdbc and JdbcConnection
 * @author etienne
 *
 */
public class DbChecker extends Checker{

	private JdbcConnection conn;
	
	public DbChecker(JdbcConnection conn){
		this.conn = conn;
		initialized = true;
	}
	
	private void logAccessDb(Exception e){
		logger.error("Cannot access to the database");
		logger.error(e.getMessage());
	}
	
	/**
	 * Check if all the tables in the list exist in the database
	 * return false if not
	 * @param tablenames tables to check
	 * @return true if all the tables exist
	 */
	public boolean areTablesExist(Collection<String> tablenames){
		boolean ok = true;

		Iterator<String> itTable = tablenames.iterator();
		Collection<String> nameTablesExist = null;
		try {
			nameTablesExist = conn.listTables("");
		} catch (SQLException e) {
			logAccessDb(e);
			ok = false;
		}
		while(itTable.hasNext() && ok){
			ok = isTableExist(itTable.next(),nameTablesExist);
		}
		return ok;
	}
	
	/**
	 * Check if the table exist
	 * @param tablename
	 * @return
	 */
	public boolean isTableExist(String tablename){
		try {
			return isTableExist(tablename,conn.listTables(""));
		} catch (SQLException e) {
			logAccessDb(e);
			return false;
		}
	}
	
	/**
	 * Check if tablename is contained in tablesInDb
	 * @param tablename
	 * @param tablesInDb
	 * @return true if it is contained
	 */
	private boolean isTableExist(String tablename, Collection<String> tablesInDb){
		boolean ok = tablesInDb.contains(tablename.toLowerCase());
		String ans = "";
		if(!ok){
			ans = " not";
		}
		logger.debug("Table "+tablename+" is"+ans+" contained in the database, list of tables: "+tablesInDb);
		return ok;
	}
	
	
	public boolean areFeaturesContained(String tableName,Collection<String> features){
		boolean ok = false;
		String ans = "";
		
		try {
			Collection<String> existingFeature = conn.listFeaturesFrom(tableName);
			ok = existingFeature.containsAll(features);
			if(!ok){
				ans = " not";
			}
			logger.debug("The features listed ("+features.toString()+") are"+ans+" included in "+
			tableName+" ("+existingFeature.toString()+")");
		} catch (SQLException e) {
			logAccessDb(e);
		}
		
		return ok;
	}
	
	public boolean areFeaturesTheSame(String tableName,Collection<String> features){
		boolean ok = false;
		String ans = "";
		try {
			Collection<String> existingFeature = conn.listFeaturesFrom(tableName);
			ok = existingFeature.containsAll(features) && features.containsAll(existingFeature);
			if(!ok){
				ans = " not";
			}
			logger.debug("The features listed ("+features.toString()+") are"+ans+" the same that in "+
			tableName+" ("+existingFeature.toString()+")");
		} catch (SQLException e) {
			logAccessDb(e);
		}
		
		return ok;
	}

	public JdbcConnection getConn() {
		return conn;
	}

	public void setConn(JdbcConnection conn) {
		this.conn = conn;
	}
	
}
