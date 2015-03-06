package com.idiro;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
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
			try{
				Logger.getRootLogger().setLevel(Level.INFO);
				Logger.getRootLogger().addAppender(
						new FileAppender(new PatternLayout(),
								System.getProperty("user.home")+
								"/tmp/"+ProjectID.get()+".log")
						);
			}catch(Exception e){
				logger.error("Fail to write log in temporary folder");
			}
			logger = Logger.getLogger(Log.class);
			logger.warn("Please initialise the log4j preference with a correct file path");
		}
		return ok;
	}

	/**
	 * Initialise log4j not with a property, but with a URL
	 * 
	 * @return true if a log4j is found and loaded correctly
	 */
	public static void init(URL log4j_prop){
		if(logger != null){
			return;
		}
		// Set up a simple configuration that logs on the console.
		PropertyConfigurator.configure(log4j_prop);
		logger = Logger.getLogger(Log.class);
		logger.debug("path to the log4j file: "+log4j_prop);
	}



	public static void flushAllLogs()
	{
		try
		{
			Set<FileAppender> flushedFileAppenders = new HashSet<FileAppender>();
			Enumeration currentLoggers = LogManager.getLoggerRepository().getCurrentLoggers();
			while(currentLoggers.hasMoreElements())
			{
				Object nextLogger = currentLoggers.nextElement();
				if(nextLogger instanceof Logger)
				{
					Logger currentLogger = (Logger) nextLogger;
					Enumeration allAppenders = currentLogger.getAllAppenders();
					while(allAppenders.hasMoreElements())
					{
						Object nextElement = allAppenders.nextElement();
						if(nextElement instanceof FileAppender)
						{
							FileAppender fileAppender = (FileAppender) nextElement;
							if(!flushedFileAppenders.contains(fileAppender) && !fileAppender.getImmediateFlush())
							{
								flushedFileAppenders.add(fileAppender);
								//log.info("Appender "+fileAppender.getName()+" is not doing immediateFlush ");
								fileAppender.setImmediateFlush(true);
								currentLogger.info("FLUSH");
								fileAppender.setImmediateFlush(false);
							}
							else
							{
								//log.info("fileAppender"+fileAppender.getName()+" is doing immediateFlush");
							}
						}
					}
				}
			}
		}
		catch(RuntimeException e)
		{
			logger.error("Failed flushing logs",e);
		}
	}


	public static boolean isConfigured() {
		Enumeration appenders = Logger.getRoot().getAllAppenders();
		if (appenders.hasMoreElements()) {
			return true;
		}
		else {
			Enumeration loggers = LogManager.getCurrentLoggers() ;
			while (loggers.hasMoreElements()) {
				Logger c = (Logger) loggers.nextElement();
				if (c.getAllAppenders().hasMoreElements())
					return true;
			}
		}
		return false;
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
