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
