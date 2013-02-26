package idiro.check;

import org.apache.log4j.Logger;

/**
 * Checks attributes of an object
 * Most of the checks may be overwritten, however the 
 * interest here is the logs furnished when you are running
 * these methods
 * @author etienne
 *
 */
public abstract class Checker {

	/**
	 * The logger.
	 */
	protected Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * True if the FileChecker is correctly initialised
	 */
	protected boolean initialized = false;
	
	/**
	 * @return the initialized
	 */
	public boolean isInitialized() {
		return initialized;
	}
}
