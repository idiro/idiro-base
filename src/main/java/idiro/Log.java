package idiro;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Log4j managements
 * 
 * Stores and use the log4j preference
 * @author etienne
 *
 */
public class Log {

	/**
	 * The logger.
	 */
	private static Logger logger = null;

	/**
	 * Preferences node
	 */
	private Preferences prefNode= Preferences.userNodeForPackage(Log.class);

	private String key;

	public Log(){
		key = "log4j_file-" + ProjectID.get();
	}

	/**
	 * Initialise log4j
	 * 
	 * @return true if a log4j is found and loaded correctly
	 */
	public static boolean init(){
		if(!ProjectID.isInit()){
			return false;
		}else if(logger != null){
			return true;
		}
		Log log = new Log();
		String path = log.get();
		File file = new File(path);

		boolean ok = file.canRead() && file.isFile();

		if(ok){
			// Set up a simple configuration that logs on the console.
			PropertyConfigurator.configure(path);
			logger = Logger.getLogger(Log.class);
			logger.info("path to the log4j file: "+path);
		}else{
			BasicConfigurator.configure();
			logger = Logger.getLogger(Log.class);
			logger.warn("Please initialise the log4j preference with a correct file path");
		}
		return ok;
	}


	/**
	 * Get the log4j properties path from the preferences tree
	 * @return
	 */
	public String get(){
		if(logger != null){
			logger.debug("get value for "+prefNode.absolutePath()+" : "+key+": "+prefNode.get(key,"null"));
		}
		return prefNode.get(key, "null");
	}

	/**
	 * Put a new log4j properties path in the tree
	 * @param value
	 */
	public void put(String value){
		if(logger != null)
			logger.debug("put value for "+prefNode.absolutePath()+" : "+key+": "+value);
		
		prefNode.put(key, value);
		try {
			prefNode.flush();
		} catch (BackingStoreException e) {
			System.out.println("Exception cannot write in the store: "+e.getMessage());
		}
		System.out.println("Put value: "+value);
	}

	/**
	 * Reset the value of the log4j properties path in the tree
	 */
	public void reset(){
		put("null");
	}

}
