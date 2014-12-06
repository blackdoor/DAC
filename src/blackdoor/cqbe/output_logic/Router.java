package blackdoor.cqbe.output_logic;

import blackdoor.cqbe.rpc.RPCException;
import org.json.*;
import blackdoor.cqbe.addressing.*;

/**
 * Responsibility - Handles routing through the overlay network, resolution of overlay addresses, sending RPCs and resolving replies
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
	public Router(Address bootstrapNode){
		
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
	 * Route RPC to its destination, but also try to make call on each node
	 * along the way.
	 * 
	 * @param RPC
	 * @return the consensus reply to RPC. Note: this is not the RPC response,
	 *         but rather the JSON object in the "method" field of the response.
	 * @throws RPCException
	 */
	public JSONObject routeWithCalls(Object RPC) throws RPCException {
		return null;
	}

	/**
	 * Send a RPC to destination and return the reply
	 * 
	 * @param destination
	 * @return the reply from destination. Note: this is not the RPC response,
	 *         but rather the JSON object in the "method" field of the response.
	 * @throws RPCException
	 */
	public static JSONObject call(Address destination, Object RPC)
			throws RPCException {
		return null;
	}

	/**
	 * Send an RPC to all nodes in an AddressTable and return the consensus
	 * response.
	 * 
	 * @param destinations
	 * @return the consensus reply to the RPC. Note: this is not the RPC
	 *         response, but rather the JSON object in the "method" field of the
	 *         response.
	 * @throws RPCException
	 */
	public static JSONObject call(AddressTable destinations, Object RPC)
			throws RPCException {
		return null;
	}
}
