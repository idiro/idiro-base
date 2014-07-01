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
