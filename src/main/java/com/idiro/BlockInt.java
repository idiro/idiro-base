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

/**
 * Interface of a block, a block can be 
 * initialised, or executed.
 * Originaly execute means:
 * initialise, run and checked
 * 
 * @author etienne
 *
 */
public interface BlockInt {
	
	/**
	 * Execute a block.
	 * 
	 * Parse arguments, init, run, and check the results.
	 * @param arg The arguments
	 * @return true if everything goes well
	 */
	public boolean execute(String[] args);
	
	/**
	 * Initialises the block.
	 * 
	 * @return true if the initialisation is ok
	 */
	public boolean init();
	
	/**
	 * Gives a description of the class
	 * 
	 * @return a description
	 */
	public String getDescription();
}
