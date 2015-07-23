package com.idiro.utils.db.oracle;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.idiro.check.FileChecker;
import com.idiro.utils.db.BasicStatement;

/**
 * Oracle implementation of the BasicStatement
 * @author etienne
 *
 */
public class OracleBasicStatement implements BasicStatement {

	private Logger logger = Logger.getLogger(getClass());

	@Override
	public String createExternalTable(String tableName,
			Map<String, String> features, String[] options) {
		//TODO
		String stmt_str = createTable(tableName,features,options);
		return stmt_str;
	}

	/**
	 * Create table takes one option, the primary key
	 */
	@Override
	public String createTable(String tableName,
			Map<String, String> features, String[] options) {

		String primary_key = new String();
		if(options == null){
			options = new String[0];

		}
		if(options.length > 1){
			logger.warn("The mysql createtable method does not take more than 1 option");
		}else if(options.length == 1){
			boolean found = false;
			Iterator<String> it = features.keySet().iterator();
			while(it.hasNext() && !found){
				found = it.next().equals(options[0]);
			}
			if(found){
				primary_key = "PRIMARY KEY (`"+options[0]+"`)";
			}else{
				logger.error("The primary key '"+options[0]+"'has not been found in the features");
				return "";
			}
		}

		String stmt_str = "CREATE TABLE "+tableName+"(\n\t";

		Iterator<String> it = features.keySet().iterator();
		if(!it.hasNext()){
			logger.warn("Table does not contain any features");
		}else{
			String feature = it.next();
			stmt_str += feature+" "+features.get(feature);
			while(it.hasNext()){
				feature = it.next();
				stmt_str += ",\n\t"+feature+" "+features.get(feature);
			}
		}
		if(!primary_key.isEmpty()){
			stmt_str +=",\n\t" + primary_key;
		}

		stmt_str +="\n)";

		return stmt_str;
	}

	@Override
	public String deleteTable(String tableName) {
		return "DROP TABLE "+tableName;
	}


	@Override
	public String showAllTables() {
		//return "SELECT TABLE_NAME FROM USER_TABLES";
		//Get all the tables selectable by this user
		return "select owner||'.'||table_name AS TABLE_NAME from user_tab_privs where privilege='SELECT' "
		+" union " 
		+" select rtp.owner||'.'||rtp.table_name  AS TABLE_NAME from user_role_privs urp, role_tab_privs rtp "
		+" where urp.granted_role = rtp.role and rtp.privilege='SELECT' "
		+" union "
		+" select table_name from user_tables "
		+" ORDER BY TABLE_NAME";
		
	}

	@Override
	public String showFeaturesFrom(String tableName) {
		String query = "SELECT COLUMN_NAME, DATA_TYPE FROM USER_TAB_COLUMNS WHERE TABLE_NAME = '"+tableName+"' ORDER BY COLUMN_ID";
		if(tableName.contains(".")){
			String[] tableNameSplit = tableName.split("\\.");
			query = "SELECT COLUMN_NAME, DATA_TYPE FROM ALL_TAB_COLUMNS WHERE TABLE_NAME = '"+tableNameSplit[1]+"' AND OWNER = '"+tableNameSplit[0]+"' ORDER BY COLUMN_ID";
		}
		return query; 
	}


	@Override
	public String exportTableToFile(String tableNameFrom,
			Map<String, String> features, String[] options) {

		String path = "";

		if(options.length != 1){
			logger.warn("Mysql Export takes one and only one parameter: the path where to export");
		}
		path = options[0].trim();
		if( !path.endsWith("/")){
			path +="/";
		}
		FileChecker fCh = new FileChecker(path);
		if(!fCh.isInitialized()){
			logger.error(path+" does not represent a path");
			return "";
		}else{
			fCh.getFile().getParentFile().mkdirs();
			if(!fCh.getFile().getParentFile().isDirectory() ||
					fCh.isDirectory()	){
				logger.error(path+" not valid");
				return "";
			}else{
				if(!fCh.getFile().getParentFile().canWrite()){
					logger.error("Does not have the right to write into "+path);
					return "";
				}
			}
		}


		String stmt_str = "SELECT ";

		Iterator<String> it = features.keySet().iterator();
		if(!it.hasNext()){
			logger.warn("Export 0 features");
		}else{
			stmt_str += it.next();
			while(it.hasNext()){
				stmt_str += ",\n\t"+it.next();
			}
		}
		stmt_str +="\nFROM "+tableNameFrom;
		stmt_str +="\nINTO OUTFILE "+path;

		return stmt_str;
	}
}
