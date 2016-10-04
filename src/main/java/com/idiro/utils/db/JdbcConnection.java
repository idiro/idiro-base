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

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
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

	protected class StatementObj{
		protected Statement stat;
		protected ResultSet rs;
		protected long timestamp;
		
		protected StatementObj(Statement stat, ResultSet rs){
			this.stat = stat;
			this.rs = rs;
			timestamp = System.currentTimeMillis();
		}
	}
	private int maxTimeInMinuteBeforeCleaningStatement = 60;
	private int maxNumberOfStatementOpen = 5;
	private int numberOfQueryRunningInParallel = 0;
	private int maxNumberOfQueryRunningInParallel = 3;
	
	private static Logger logger = Logger.getLogger(JdbcConnection.class);
	
	protected JdbcDetails connectionDetails;
	
	protected Connection connection;
	
	protected BasicStatement bs;
	
	private int maxDefaultRowNb = 100000;
	private List<StatementObj> statementCach = new LinkedList<StatementObj>();
	
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
	}
	
	public JdbcConnection(String driverClassname, JdbcDetails connectionDetails, BasicStatement bs) throws Exception {
		this.connectionDetails = connectionDetails;
		this.bs = bs;
		try{
			Class.forName(driverClassname);
			connection = (DriverManager.getConnection(
				connectionDetails.getDburl(),
				connectionDetails.getUsername(),
				connectionDetails.getPassword()));
		}catch(SQLException e){
			logger.error("The database details are set, but the connection cannot be initialised");
			logger.error("Details: <"+connectionDetails.getDburl()+"> <"+connectionDetails.getUsername()+"> <*>");
			throw e;
		}
	}


	
	private final void waitForQuery(){
		while(numberOfQueryRunningInParallel >= maxNumberOfQueryRunningInParallel){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		}
	}
	
	private final void requestTicketForQuery(){
		waitForQuery();
		++numberOfQueryRunningInParallel;
	}
	
	private final void releaseTicket(){
		--numberOfQueryRunningInParallel;
	}
	
	public void closeConnection() throws SQLException{
		removeAllStatement();
		numberOfQueryRunningInParallel = 0;
		connection.close();
	}
	
	public Statement getNewStatement() throws SQLException{
		return connection.createStatement();
	}

	public void cleanOldStatement(){
		Iterator<StatementObj> itStat = statementCach.iterator();
		while(itStat.hasNext()){
			StatementObj cur = itStat.next();
			Statement curSt = cur.stat;
			ResultSet rs = cur.rs;
			boolean toClose = false;
			try{
				toClose = rs == null || rs.isClosed();
			}catch(Exception e){
				//Remove the statement after 30 minutes
				if(System.currentTimeMillis() - cur.timestamp > 1000*60*maxTimeInMinuteBeforeCleaningStatement){
					toClose = true;
				}
			}
			if(toClose){
				closeStatement(curSt,rs);
				itStat.remove();
			}
		}
	}
	
	protected void cleanOldStatement(ResultSet resultSet){
		if(resultSet != null){
			Iterator<StatementObj> itStat = statementCach.iterator();
			while(itStat.hasNext()){
				StatementObj cur = itStat.next();
				Statement curSt = cur.stat;
				ResultSet rs = cur.rs;
				if(resultSet.equals(rs)){
					closeStatement(curSt,rs);
					itStat.remove();
					break;
				}
			}
		}
	}
	
	private void closeStatement(Statement st, ResultSet rs){
		try{
			rs.close();
		}catch(Exception e){}
		try{
			st.close();
		}catch(Exception e){}
		
	}
	
	public void addNewStatement(Statement stat, ResultSet rs) throws SQLException{
		cleanOldStatement();
		statementCach.add(new StatementObj(stat, rs));
		while(maxNumberOfStatementOpen < statementCach.size()){
			closeStatement(statementCach.get(0).stat,statementCach.get(0).rs);
			statementCach.remove(0);
		}
	}
	
	public void removeAllStatement(){
		Iterator<StatementObj> itStat = statementCach.iterator();
		while(itStat.hasNext()){
			StatementObj cur = itStat.next();
			Statement curSt = cur.stat;
			ResultSet rs = cur.rs;
			try{
				rs.close();
			}catch(Exception e){}
			try{
				curSt.close();
			}catch(Exception e){}
			itStat.remove();
		}
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
		ResultSet rs = executeQuery(bs.showFeaturesFrom(tableName));
		String name;
		while (rs.next()) {
			name = rs.getString(1).trim();
			features.add(name);
		}
		rs.close();
		return features;
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
		return execute(bs.createExternalTable(tableName, features, options));
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
		return execute(bs.createTable(tableName, features, options));
	}

	/**
	 * Execution of the statement
	 * @param tableName
	 * @return
	 * @throws SQLException 
	 * @see com.idiro.utils.db.BasicStatement#deleteTable(java.lang.String)
	 */
	public boolean deleteTable(String tableName) throws SQLException {
		return execute(bs.deleteTable(tableName));
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
		return execute(bs.exportTableToFile(tableNameFrom, features,
				options));
	}

	/**
	 * Execution of the statement
	 * @return
	 * @throws SQLException 
	 * @see com.idiro.utils.db.BasicStatement#showAllTables()
	 */
	public ResultSet showAllTables() throws SQLException {
		return executeQuery(bs.showAllTables());
	}

	/**
	 * Execution of the statement
	 * @return
	 * @throws SQLException 
	 * @see com.idiro.utils.db.BasicStatement#showFeaturesFrom()
	 */
	public ResultSet showFeaturesFrom(String tableName)throws SQLException {
		return executeQuery(bs.showFeaturesFrom(tableName),maxDefaultRowNb);
	}

	/**
	 * @param arg0
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#executeQuery(java.lang.String)
	 */
	public ResultSet executeQuery(String arg0) throws SQLException {
		return executeQuery(arg0,maxDefaultRowNb);
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
		requestTicketForQuery();
		ResultSet ans = null;
		try{
			Statement statement = getNewStatement();
			statement.setMaxRows(maxRecord);
			ans = statement.executeQuery(showStmt(arg0));
			addNewStatement(statement, ans);
		}catch(SQLException e){
			releaseTicket();
			throw e;
		}
		releaseTicket();
		return ans;
	}
	
	/**
	 * @param arg0
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#execute(java.lang.String)
	 */
	public boolean execute(String arg0) throws SQLException {
		boolean ans;
		requestTicketForQuery();
		try{
			Statement statement = getNewStatement();
			ans = statement.execute(showStmt(arg0));
			statement.close();
		}catch(SQLException e){
			releaseTicket();
			throw e;
		}
		releaseTicket();
		return ans;
	}
	
	/**
	 * @param arg0
	 * @return
	 * @throws SQLException
	 * @see java.sql.Statement#executeUpdate(java.lang.String)
	 */
	public int executeUpdate(String arg0) throws SQLException {
		int ans = 0;
		requestTicketForQuery();
		try{
			Statement statement = getNewStatement();
			ans = statement.executeUpdate(showStmt(arg0));
			statement.close();
		}catch(SQLException e){
			releaseTicket();
			throw e;
		}
		return ans;
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

	public Connection getConnection() {
		return connection;
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


	public int getMaxTimeInMinuteBeforeCleaningStatement() {
		return maxTimeInMinuteBeforeCleaningStatement;
	}


	public void setMaxTimeInMinuteBeforeCleaningStatement(int maxTimeInMinuteBeforeCleaningStatement) {
		this.maxTimeInMinuteBeforeCleaningStatement = maxTimeInMinuteBeforeCleaningStatement;
	}


	/**
	 * @return the maxNumberOfStatementOpen
	 */
	public int getMaxNumberOfStatementOpen() {
		return maxNumberOfStatementOpen;
	}


	/**
	 * @param maxNumberOfStatementOpen the maxNumberOfStatementOpen to set
	 */
	public void setMaxNumberOfStatementOpen(int maxNumberOfStatementOpen) {
		this.maxNumberOfStatementOpen = maxNumberOfStatementOpen;
	}


	/**
	 * @return the maxNumberOfQueryRunningInParallel
	 */
	public int getMaxNumberOfQueryRunningInParallel() {
		return maxNumberOfQueryRunningInParallel;
	}


	/**
	 * @param maxNumberOfQueryRunningInParallel the maxNumberOfQueryRunningInParallel to set
	 */
	public void setMaxNumberOfQueryRunningInParallel(int maxNumberOfQueryRunningInParallel) {
		this.maxNumberOfQueryRunningInParallel = maxNumberOfQueryRunningInParallel;
	}
	
}
