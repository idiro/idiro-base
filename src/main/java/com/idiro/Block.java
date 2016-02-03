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

package com.idiro;

import org.apache.log4j.Logger;

/**
 * The project, is build by block,
 * each execution is a series of block execution
 * Each block execution corresponding to parse arguments,
 * initialise, run, then check the results
 *  
 * @author etienne
 *
 */
public abstract class Block implements BlockInt{

	/**
	 * The execution start time
	 */
	protected long startTime;
	
	/**
	 * Logger of the task
	 */
	protected Logger logger = Logger.getLogger(this.getClass());

	/**
	 * This runs the block.
	 * 
	 * @return true if runs ok
	 */
	protected abstract boolean run();

	/**
	 * This check the output of the block
	 * 
	 * @return true if checks ok 
	 */
	protected abstract boolean finalCheck();
	
}
