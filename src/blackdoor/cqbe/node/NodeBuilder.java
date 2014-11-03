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
