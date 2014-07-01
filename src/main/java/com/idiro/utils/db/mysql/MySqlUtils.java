package com.idiro.utils.db.mysql;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.idiro.check.DbChecker;
import com.idiro.check.FileChecker;
import com.idiro.utils.DataFileUtils;
import com.idiro.utils.db.JdbcConnection;
import com.idiro.utils.db.JdbcDetails;

public class MySqlUtils {

	
	private static Logger logger = Logger.getLogger(MySqlUtils.class);
	
	
	public static boolean changeFormatAfterExport(File in, File out, char delimiter,Collection<String> header,
			Collection<String> quotes) {
		//We expect that in is a csv file and out a file
		boolean ok = true;
		FileChecker fChIn = new FileChecker(in),
				fChOut = new FileChecker(out);

		if(!fChIn.isFile()){
			logger.error(fChIn.getFilename()+" is not a directory or does not exist");
			return false;
		}

		if(fChOut.exists()){
			if(fChOut.isDirectory()){
				logger.error(fChOut.getFilename()+" is a directory");
				return false;
			}
			logger.warn(fChOut.getFilename()+" already exists, it will be removed");
			String out_str = out.getAbsolutePath();
			out.delete();
			out = new File(out_str);
		}

		BufferedWriter bw = null;
		BufferedReader br = null;

		try {
			bw = new BufferedWriter(new FileWriter(out));

			logger.debug("read the file"+in.getAbsolutePath());
			br = new BufferedReader(new FileReader(in));
			String strLine;
			if(header != null && !header.isEmpty()){
				Iterator<String> it = header.iterator();
				String headerLine = it.next();
				while(it.hasNext()){
					headerLine += delimiter + it.next();
				}
				bw.write(headerLine+"\n");
			}
			
			//Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				bw.write(DataFileUtils.addQuotesToLine(
						DataFileUtils.getCleanLine(strLine.replace(',', delimiter),delimiter,delimiter), 
						quotes, 
						delimiter)+
						"\n"
					);
			}
			br.close();

			bw.close();
		} catch (FileNotFoundException e1) {
			logger.error(e1.getCause()+" "+e1.getMessage());
			logger.error("Fail to read "+in.getAbsolutePath());
			ok = false;
		} catch (IOException e1) {
			logger.error("Error writting, reading on the filesystem from the directory"+
					fChIn.getFilename()+" to the file "+fChOut.getFilename());
			ok = false;
		}
		if(ok){
			in.delete();
		}
		return ok;
	}
	
	
	public boolean exportTable(JdbcConnection conn, String tableName, File out,
			Map<String, String> features, char delimiter,boolean header,Collection<String>quotes) {



		String[] options = new String[1];
		try {
			options[0]= File.createTempFile("idiro", tableName).getAbsolutePath();
		} catch (IOException e1){
			logger.error("Fail to create temporary local file");
			logger.error(e1.getMessage());
			return false;
		}
		
		try {
			conn.exportTableToFile(tableName, features, options);
		} catch (SQLException e) {
			logger.debug(e.getMessage());
			return false;
		}
		
		if(out.exists()){
			out.delete();
		}
		return changeFormatAfterExport(new File(options[0]),out,delimiter,features.keySet(),quotes);
		
	}

	public static boolean importTable(JdbcConnection conn, String tableName,
			Map<String, String> features, File fileIn, File tablePath, char delimiter,boolean header) {
		boolean ok = true;
		try {
			DbChecker dbCh = new DbChecker(conn);
			if(! dbCh.isTableExist(tableName)){
				logger.debug("The table which has to be imported has not been created");
				logger.debug("Creation of the table");
				Integer ASCIIVal = (int) delimiter;
				String[] options = { ASCIIVal.toString() ,
						tablePath.getCanonicalPath()};
				conn.executeQuery(new MySqlBasicStatement().createExternalTable(tableName, features, options));
			}else{
				//Check if it is the same table 
				if( ! dbCh.areFeaturesTheSame(tableName, features.keySet())){
					logger.warn("Mismatch between the table to import and the table in the database");
					return false;
				}

				logger.warn("Have to check if the table is external or not, I do not know how to do that");

			}
		} catch (SQLException e) {
			logger.debug("Fail to watch the datastore");
			logger.debug(e.getMessage());
			return false;
		} catch (IOException e) {
			logger.warn("Fail to get the output path from a File object");
			logger.warn(e.getMessage());
			return false;
		}
		
		//Check if the input file has the right number of field
		FileChecker fChIn = new FileChecker(fileIn);
		FileChecker fChOut = new FileChecker(tablePath);
		String strLine = "";
		try{

			if(fChIn.isDirectory() || !fChIn.canRead()){
				logger.warn("The file "+fChIn.getFilename()+"is a directory or can not be read");
				return false;
			}
			BufferedReader br = new BufferedReader(new FileReader(fileIn));
			//Read first line
			strLine = br.readLine();
			br.close();
		}catch (IOException e1) {
			logger.debug("Fail to open the file"+fChIn.getFilename());
			return false;
		}

		if( StringUtils.countMatches(strLine,String.valueOf(delimiter)) != features.size() - 1){
			logger.warn("File given does not match with the delimiter '"+
					delimiter+"' given and the number of fields '"+features.size()+"'");
			return false;
		}

		BufferedWriter bw = null;
		BufferedReader br = null;

		try {
			bw = new BufferedWriter(new FileWriter(tablePath));
			logger.debug("read the file"+fileIn.getAbsolutePath());
			br = new BufferedReader(new FileReader(fileIn));
			String delimiterStr = ""+delimiter;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				bw.write("\""+strLine.replace(delimiterStr, "\",\"")+"\"\n");
			}
			br.close();

			bw.close();
		} catch (FileNotFoundException e1) {
			logger.error(e1.getCause()+" "+e1.getMessage());
			logger.error("Fail to read "+fileIn.getAbsolutePath());
			ok = false;
		} catch (IOException e1) {
			logger.error("Error writting, reading on the filesystem from the directory"+
					fChIn.getFilename()+" to the file "+fChOut.getFilename());
			ok = false;
		}

		return ok;
	}


	public static boolean createDatabase(JdbcDetails metastoreUrl, String database, 
			String user, String user_password) {

		boolean ok = true;

		try {
			JdbcConnection conn = new JdbcConnection(metastoreUrl,new MySqlBasicStatement());
			conn.executeUpdate("CREATE DATABASE "+database);
			
			conn.executeUpdate("GRANT USAGE ON *.* TO '"+user+"'");
			conn.executeUpdate("SET PASSWORD FOR '"+user +"' = PASSWORD('"+user_password+"')");
			conn.executeUpdate("GRANT ALL PRIVILEGES ON "+database+".* TO '"+user+"'");
			
			conn.executeUpdate("FLUSH PRIVILEGES");
		}catch (SQLException e){
			logger.error("Error in the execution of an administration query");
			logger.error(e.getMessage());
			ok = false;
		} catch (Exception e) {
			logger.error("Cannot connect to the metastore "+metastoreUrl.getDburl());
			logger.error(e.getMessage());
			ok = false;
		}

		return ok;
	}
	
}
