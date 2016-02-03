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

package com.idiro.check;

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
