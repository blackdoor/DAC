package blackdoor.cqbe.node;

import blackdoor.cqbe.addressing.AddressTable;

public class Node {
	/**
	 * @author - Cyril Van Dyke
	 * 
	 */
	public Node() {
		// TODO Auto-generated constructor stub	
	}
	
	/**
	 * Closes the node and returns a list of folders containing node data
	 * @return - A list of strings containing the folder locations of the node storage, address table, and updater
	 */
	public String[] destroyNode()
	{
		return null;
	}

	/**
	 * Prints a brief status of node
	 */
	public void statusCheck(){
	}
	
	/**
	 * Lists the current status of the node's storage including space used, etc.
	 */
	public void checkStorage(){
		
	}
	
	/**
	 * Returns the address table of the node
	 * @return Address Table of node
	 */
	public AddressTable getAddressTable(){
		return null;
	}
}
