package com.idiro.utils.db;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Connect to a data base via jdbc.
 * 
 * All jdbc statement have to be executed from this method
 * You have firstly to link it with a url and basic statements
 * Then you can either call the basic statement or do your own
 * query.
 * 
 * @author etienne
 *
 */
public class JdbcConnection {

	private Logger logger = Logger.getLogger(getClass());
	
	private JdbcDetails connectionDetails;
	
	private Connection connection;
	
	private Statement statement;
	
	private BasicStatement bs;
	
	private int maxDefaultRowNb = 100000;
	
	public JdbcConnection(JdbcDetails connectionDetails,BasicStatement bs) throws Exception{
		this.connectionDetails = connectionDetails;
		this.bs = bs;
		try{
			connection = (DriverManager.getConnection(
					connectionDetails.getDburl(),
					connectionDetails.getUsername(),
					connectionDetails.getPassword()));
		}catch(SQLException e){
			logger.error("The database details are set, but the connection cannot be initialised");
			logger.error("Details: <"+connectionDetails.getDburl()+"> <"+connectionDetails.getUsername()+"> <*>");
			throw e;
		}
		setStatement(connection.createStatement());
		
	}
	

	public JdbcConnection(URL jarPath, String driverClassname, JdbcDetails connectionDetails,BasicStatement bs) throws Exception{
		this.connectionDetails = connectionDetails;
		this.bs = bs;
		try{
			URLClassLoader ucl = new URLClassLoader(new URL[] { jarPath });
			Driver d = (Driver)Class.forName(driverClassname, true, ucl).newInstance();
			DriverManager.registerDriver(new VirtualDriver(d));
			connection = (DriverManager.getConnection(
				connectionDetails.getDburl(),
				connectionDetails.getUsername(),
				connectionDetails.getPassword()));
		}catch(SQLException e){
			logger.error("The database details are set, but the connection cannot be initialised");
			logger.error("Details: <"+connectionDetails.getDburl()+"> <"+connectionDetails.getUsername()+"> <*>");
			throw e;
		}
		setStatement(connection.createStatement());
		
	}
	
	public void closeConnection() throws SQLException{
		statement.close();
		connection.close();
	}

	/**
	 * Returns a list of tables that matches the supplied pattern.
	 * 
	 * @param pattern
	 *            - The pattern to match (empty string or null returns all
	 *            tables)
	 * @return The list of tables
	 * @throws SQLException
	 *             If a connection error occurs
	 */
	public final List<String> listTables(final String pattern) throws SQLException {
		
		List<String> results = new ArrayList<String>();
		boolean filter = false;
		if (pattern != null && pattern.trim() != "") {
			filter = true;
		}
		ResultSet rs = executeQuery(showStmt(bs.showAllTables()));
		String name;
		while (rs.next()) {
		    name = rs.getString(1).trim();
			if (filter) {
				if (!name.matches(pattern)) {
					continue;
				}
			}
			results.add(name);
		}
		rs.close();
		
		return results;
	}
	
	/**
	 * Returns the features of a table, the table is supposed to exist
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public final List<String> listFeaturesFrom(String tableName) throws SQLException{
		List<String> features = new LinkedList<String>();
		ResultSet rs = statement
				.executeQuery(showStmt(bs.showFeaturesFrom(tableName)));
		String name;
		while (rs.next()) {
		    name = rs.getString(1).trim();
			features.add(name);
		}
		rs.close();
		
		return features;
	}

	/**
	 * @param statement the statement to set
	 */
	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	/**
	 * Execution of the statement
	 * @param tableName
	 * @param features
	 * @param options
	 * @return
	 * @throws SQLException 
	 * @see com.idiro.utils.db.BasicStatement#createExternalTable(java.lang.String, java.util.Map, java.lang.String[])
	 */
	public boolean createExternalTable(String tableName,
			Map<String, String> features, String[] options) throws SQLException {
		return statement.execute(showStmt(bs.createExternalTable(tableName, features, options)));
	}

	/**
	 * Execution of the statement
	 * @param tableName
	 * @param features
	 * @param options
	 * @return
	 * @throws SQLException 
	 * @see com.idiro.utils.db.BasicStatement#createTable(java.lang.String, java.util.Map, java.lang.String[])
	 */
	public boolean createTable(String tableName,
			Map<String, String> features, String[] options) throws SQLException {
		return statement.execute(showStmt(bs.createTable(tableName, features, options)));
	}

	/**
	 * Execution of the statement
	 * @param tableName
	 * @return
	 * @throws SQLException 
	 * @see com.idiro.utils.db.BasicStatement#deleteTable(java.lang.String)
	 */
	public boolean deleteTable(String tableName) throws SQLException {
		return statement.execute(showStmt(bs.deleteTable(tableName)));
	}

	/**
	 * Execution of the statement
	 * @param tableNameFrom
	 * @param tableNameTo
	 * @param features
	 * @param options
	 * @return
	 * @throws SQLException 
	 * @see com.idiro.utils.db.BasicStatement#exportTableToFile(java.lang.String, java.lang.String, java.util.Map, java.lang.String[])
	 */
	public boolean exportTableToFile(String tableNameFrom,
			Map<String, String> features, String[] options) throws SQLException {
		return statement.execute(showStmt(bs.exportTableToFile(tableNameFrom, features,
				options)));
	}

	/**
	 * Execution of the statement
	 * @return
	 * @throws SQLException 
	 * @see com.idiro.utils.db.BasicStatement#showAllTables()
	 */
	public ResultSet showAllTables() throws SQLException {
		return statement.executeQuery(showStmt(bs.showAllTables()));
	}

	/**
	 * Execution of the statement
	 * @return
	 * @throws SQLException 
	 * @see com.idiro.utils.db.BasicStatement#showFeaturesFrom()
	 */
	public ResultSet showFeaturesFrom(String tableName)throws SQLException {
		return executeQuery(showStmt(bs.showFeaturesFrom(tableName)),maxDefaultRowNb);
	}

	/**
	 * @param arg0
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#executeQuery(java.lang.String)
	 */
	public ResultSet executeQuery(String arg0) throws SQLException {
		return executeQuery(showStmt(arg0),maxDefaultRowNb);
	}
	
	/**
	 * Execute a query, but set the number max of row
	 * @param arg0
	 * @param maxRecord
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#executeQuery(java.lang.String)
	 */
	public ResultSet executeQuery(String arg0, int maxRecord) throws SQLException {
		statement.setMaxRows(maxRecord);
		ResultSet ans = statement.executeQuery(showStmt(arg0));
		return ans;
	}
	
	/**
	 * @param arg0
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#execute(java.lang.String)
	 */
	public boolean execute(String arg0) throws SQLException {
		return statement.execute(showStmt(arg0));
	}
	
	/**
	 * @param arg0
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#executeUpdate(java.lang.String)
	 */
	public int executeUpdate(String arg0) throws SQLException {
		return statement.executeUpdate(showStmt(arg0));
	}
	

	protected String showStmt(String stmt_str){
		//We will hide some parameters that we do not want displayed in the logs
		//To make it simple everything between ', does not modify the semantic of
		//the query and can contain personal information: password or user name
		String stmt_toshow = stmt_str;
		if(stmt_toshow.matches(".*'.*'.*")){
			String[] stmt_split = stmt_toshow.split("'");
			stmt_toshow = stmt_split[0];
			for(int i=2;i < stmt_split.length;i+=2){
				stmt_toshow += "'*'"+stmt_split[i];
			}
			if(stmt_split.length % 2 == 0){
				stmt_toshow += "'*'";
			}
		}
		if(stmt_toshow.matches(".*\".*\".*")){
			String[] stmt_split = stmt_toshow.split("\"");
			stmt_toshow = stmt_split[0];
			for(int i=2;i < stmt_split.length;i+=2){
				stmt_toshow += "\"*\""+stmt_split[i];
			}
			if(stmt_split.length % 2 == 0){
				stmt_toshow += "\"*\"";
			}
		}
		
		logger.debug("Statement to launch, in '"+connectionDetails.getDburl()+"': \n"+stmt_toshow);
		return stmt_str;
	}
	

	/**
	 * @throws SQLException
	 * @see java.sql.Connection#commit()
	 */
	public void commit() throws SQLException {
		connection.commit();
	}

	/**
	 * @return the bs
	 */
	public BasicStatement getBs() {
		return bs;
	}

	/**
	 * @return the maxDefaultRowNb
	 */
	public int getMaxDefaultRowNb() {
		return maxDefaultRowNb;
	}

	/**
	 * @param maxDefaultRowNb the maxDefaultRowNb to set
	 */
	public void setMaxDefaultRowNb(int maxDefaultRowNb) {
		this.maxDefaultRowNb = maxDefaultRowNb;
	}
	
}
