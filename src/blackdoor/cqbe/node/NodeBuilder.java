package blackdoor.cqbe.node;

import blackdoor.cqbe.addressing.AddressTable;

public class NodeBuilder {

	/**
	 * Create a new NodeBuilder with no preset settings
	 */
	public NodeBuilder() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param table
	 */
	public NodeBuilder(AddressTable table) {
		
	}
	
	
	/**
	 * Prints the current list of settings that will be used when the node is built
	 * @return List of settings currently in place for when buildNode() is called
	 */
	public void viewBuildSet() {
		
	}
	
	
	/**
	 * Spawns a ConfigurationFileManager to set an AddressTable from storage
	 */
	public void setAddressTable() {
		
	}

	
	/**
	 * Gets the Address Table currently set for buildNode()
	 * @return the currently set AddressTable
	 */
	public AddressTable getAddressTable(){
		return null;
	}
	
	/**
	 * Spawns a ConfigurationFileManager to set a storage location for the new node
	 */
	public void setStorage(){
		
	}
	
	/**
	 * Sets the port of the node to be built, passed in from DART.Join
	 * @param port - The port given to be set
	 */
	public void setPort(String port){
	}
	
	/**
	 * Sets the directory where the node should be spawned and operated from
	 * Passed from DART.Join
	 * @param dir - Directory to be used by created node
	 */
	public void setDirectory(String dir){
		
	}
	
	/**
	 * Sets the location of a previous node to be run from
	 * @param dir - Location where node was run previously
	 */
	public void setRevival(String dir){
		
	}
	
	/**
	 * Sets whether the node spawned will be a Daemon or not
	 * @param status - If yes, daemon
	 */
	public void setDaemon(Boolean status){
		
	}
	
	/**
	 * Sets whether or not the node spawned will be the first in the network
	 * @param status - If yes, Adam node.
	 */
	public void setAdam(Boolean status){
		
	}
	
	/**
	 * Returns the address with which the node will be created on
	 * @return The address with which the node will be created with
	 */
	public String viewAddress(){
		return null;
	}
	
	/**
	 * Builds a node based on the current list of settings attributed to it.
	 */
	public void buildNode(){
		
	}
	
}
