package blackdoor.cqbe.output_logic;

import blackdoor.cqbe.addressing.*;

public class Router {

	/**
	 * Create a new router, will look for a node on localhost:defaultport to use as bootstrapping node
	 * TODO decide default port, probably in settings 
	 */
	public Router() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Create a new router with an address table to use for routing.
	 * @param bootstrapTable Any address table containing valid nodes in the network.
	 */
	public Router(AddressTable bootstrapTable){
		
	}
	/**
	 * Create a new router that will contact bootstrapNode for an address table to use for routing.
	 * @param bootstrapNode Any node in the network. Should be a trusted node.
	 * @param bootstrapIP The IP address on which bootstrapNode is listening.
	 */
	public Router(String bootstrapNode, int bootstrapIP){
		
	}
	/**
	 * Resolve the network layer addresses and ports of neighbors to destination by routing through the network.
	 * @param destination an overlay address for which nearby layer 3 addresses should be resolved.
	 * @return an AddressTable filled with the nearest neighbors of destination.
	 */
	public AddressTable resolveAddress(Address destination){
		return null;
	}
	
	/**
	 * Route RPC to its destination, but call it on nodes along the way. 
	 * @param RPC 
	 * @return the consensus reply to RPC
	 */
	public void routeWithCalls(int RPC){
		
	}
	

}
