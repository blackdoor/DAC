package blackdoor.cqbe.output_logic;

import blackdoor.cqbe.addressing.*;

/**
 * Handles routing through the overlay network, resolution of overlay addresses, sending RPCs and resolving replies
 * @author nfischer3
 *
 */
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
	 * @param bootstrapPort The port on which bootstrapNode is listening.
	 */
	public Router(String bootstrapNode, int bootstrapPort){
		
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
	 * Route RPC to its destination, but also try to make call on each node along the way.
	 * @param RPC 
	 * @return the consensus reply to RPC
	 */
	public void routeWithCalls(Object RPC){
		
	}
	/**
	 * Send a RPC to destination and return the reply
	 * @param destination
	 */
	public static void call(Address destination, Object RPC){
		
	}
	
	/**
	 * Send an RPC to all nodes in an AddressTable and return the consensus response.
	 * @param destinations
	 * @return the consensus reply to the RPC
	 */
	public static void call(AddressTable destinations, Object RPC){
		
	}

}
