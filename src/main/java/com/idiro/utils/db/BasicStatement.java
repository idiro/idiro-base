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

import java.util.Map;

/**
 * Basic statement syntax interface.
 * This interfaces contains the basic statement
 * syntax for the databases you want to use
 * For example jdbc and mysql has not exactly
 * the same syntax and those even for very basic
 * queries. Some automate process are done but
 * these queries are needed
 * 
 * @author etienne
 *
 */

public interface BasicStatement {

	/**
	 * Statement to show all the tables in here
	 * @return the statement
	 */
	String showAllTables();
	
	/**
	 * Statement to delete a table
	 * 
	 * @param tableName table name to delete
	 * @return the statement
	 */
	String deleteTable(String tableName);
	
	/**
	 * Statement to create a table
	 * @param tableName name of the table
	 * @param features the feature names (key) associated to their types (value)
	 * @param options other options that may be specified
	 * @return the statement
	 */
	String createTable(String tableName, Map<String,String> features, String[] options);
	
	/**
	 * Statement to create an External table
	 * @param tableName name of the table
	 * @param features the feature names (key) associated to their types (value)
	 * @param options other options that may be specified with the path of the data for example
	 * @return the statement
	 */
	String createExternalTable(String tableName, Map<String,String> features, String[] options);
	
	/**
	 * Statement to export a table to a file
	 * 
	 * @param tableName tableName name of the table
	 * @param features features the feature names (key) associated to their types (value) to exports
	 * @param options other options that may be specified with the path to export for example
	 * @return the statement
	 */
	String exportTableToFile(String tableNameFrom, Map<String,String> features, String[] options);
	
	/**
	 * Statement to list the features of a table
	 * 
	 * @param tableName name of the table
	 * @return the statement
	 */
	String showFeaturesFrom(String tableName);
	
}
