package blackdoor.cqbe.output_logic;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import blackdoor.cqbe.node.Node;
import blackdoor.cqbe.rpc.GETResponse;
import blackdoor.cqbe.rpc.GETResponse.GETResponseFactory;
import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.cqbe.rpc.ShutdownRpc;
import blackdoor.cqbe.rpc.RPCException.*;
import blackdoor.cqbe.rpc.RPCValidator;

import org.json.*;

import blackdoor.cqbe.addressing.*;
import blackdoor.net.SocketIOWrapper;
import blackdoor.util.DBP;

/**
 * Responsibility - Handles routing through the overlay network, resolution of overlay addresses, sending RPCs and resolving replies
 * @author nfischer3
 *
 */
public class Router {
	
	private AddressTable bootstrapTable;

	/**
	 * Create a new router, will look for a node on localhost:defaultport to use as bootstrapping node
	 * TODO decide default port, probably in settings 
	 * @throws IOException if the router is unable to connect to the node on localhost
	 * @throws RPCException 
	 */
	public static Router fromDefaultLocalNode() throws IOException, RPCException {
		return Router.fromBootstrapNode(new L3Address(InetAddress.getLoopbackAddress(), 1234));
	}
	
	/**
	 * Create a new router with an address table to use for routing.
	 * @param bootstrapTable Any address table containing valid nodes in the network.
	 */
	public Router(AddressTable bootstrapTable){
		this.bootstrapTable = bootstrapTable;
	}

	/**
	 * Create a new router that will contact bootstrapNode for an address table to use for routing.
	 * @param bootstrapNode
	 * @throws IOException if router could not connect to bootstrapNode
	 * @throws RPCException 
	 */
	public static Router fromBootstrapNode(L3Address bootstrapNode) throws IOException, RPCException{
		//RPCBuilder lookupRequestBuilder = new RPCBuilder();
		AddressTable table;
		L3Address me;
		//JSONObject lookupRequest;
		//JSONObject lookupResponse;
		//SocketIOWrapper io;
		
		//determine our address
		try{
			me = Node.getAddress();
		}catch(ExceptionInInitializerError e){
			me = new L3Address(InetAddress.getLoopbackAddress(), -1);
		}
		
		/*
		//create lookup request
		lookupRequestBuilder.setDestinationO(bootstrapNode);
		lookupRequestBuilder.setSourceIP(me.getLayer3Address());
		lookupRequestBuilder.setSourcePort(me.getPort());
		try {
			lookupRequest = lookupRequestBuilder.buildLOOKUP();
		} catch (RPCException e) {
			DBP.printException(e);
			DBP.printerrorln("Couldn't build lookup request, this is bad, code is broken somewhere, investigate, returning");
			return null;
		}
		
		//send lookup request to bootstrapNode
		io = new SocketIOWrapper(new Socket(bootstrapNode.getLayer3Address(),bootstrapNode.getPort()));
		io.write(lookupRequest.toString());
		
		//check lookup response
		lookupResponse = new JSONObject(io.read()); //TODO error handle this. Make sure syntax is valid, make sure JSON follows JSON-RPC, make sure id's match, make sure RPC was successful.
		JSONArray addresses = lookupResponse.getJSONArray("result");
		
		//set router address table
		table = new AddressTable(me);
		JSONObject entry;
		for(int i = 0; i < addresses.length(); i++){
			entry = addresses.getJSONObject(i);
			if (entry.getInt("IP") > 0){
				L3Address a = new L3Address(InetAddress.getByName(entry.getString("IP")), entry.getInt("port"));
				if(!a.equals(me)){
					table.put(a);
				}else{
					DBP.printdevln(a + " is the current node("+me+"), not adding to table");
				}
			}
		}
		*/
		table = primitiveLookup(bootstrapNode, me);
		return new Router(table);
	}
	
	/**
	 * 
	 * @param remoteNode
	 * @param destination
	 * @return true if pong
	 * @throws RPCException 
	 * @throws IOException 
	 */
	public static boolean ping(L3Address remoteNode) throws RPCException{
		RPCBuilder requestBuilder = new RPCBuilder();
		L3Address source = getSource();
		JSONObject request;
		try {
		requestBuilder.setDestinationO(remoteNode);
		requestBuilder.setSourceIP(source.getLayer3Address());
		requestBuilder.setSourcePort(source.getPort());
		request = requestBuilder.buildPING();
		return RPCValidator.isValidoopResponse(call(remoteNode, request));
		} catch(IOException e) {
			return false;
		}
	}
	
	private static L3Address getSource(){
		InetAddress sourceIP;
		int sourcePort = 0;
		try{
			L3Address source = Node.getAddress();
			sourceIP = source.getLayer3Address();
			sourcePort = source.getPort();
		}catch (ExceptionInInitializerError e){
			DBP.printwarningln("Could not get an instance of node for primitive lookup, using localhost:0 as source for RPC");
			sourceIP = InetAddress.getLoopbackAddress();
		}
		return new L3Address(sourceIP, sourcePort);
	}
	
	/**
	 * Sends a get request to remoteNode either for the value of destination or the keys stored by remoteNode in their index bucket.
	 * @param remoteNode
	 * @param destination
	 * @param index
	 * @return
	 * @throws RPCException
	 * @throws IOException
	 */
	public static GETResponse primitiveGet(L3Address remoteNode, Address destination, int index) throws RPCException, IOException{
		JSONObject requestObject = null;
		JSONObject responseObject = null;
		L3Address source = getSource();
		RPCBuilder requestBuilder = new RPCBuilder();
		try {
			requestBuilder.setDestinationO(destination);
			requestBuilder.setSourceIP(source.getLayer3Address());
			requestBuilder.setSourcePort(source.getPort());
			requestBuilder.setIndex(index);
			requestObject = requestBuilder.buildGET();
		} catch (RPCException e) {
			DBP.printException(e);
		}
		responseObject = call(remoteNode, requestObject);
		GETResponse response = GETResponseFactory.multipleReturnTypeDerp(responseObject);
		return response;
	}
	
	/**
	 * 
	 * @param remoteNode
	 * @param destination
	 * @param index
	 * @return a list of values stored by remoteNode in the index bucket
	 * @throws RPCException
	 * @throws IOException
	 */
	public static List<Address> getIndex(L3Address remoteNode, int index) throws RPCException, IOException{
		GETResponse response = null;
		response = primitiveGet(remoteNode, Address.getNullAddress(), index);
		if(response instanceof GETResponse.GETIndexResponse){
			return ((GETResponse.GETIndexResponse) response).getResult();
		}else throw new RPCException(JSONRPCError.INVALID_RESULT);
	}
	
	/**
	 * 
	 * @param remoteNode
	 * @param destination
	 * @return the value for destination, or null if remoteNode does not have a value for destination
	 * @throws RPCException
	 * @throws IOException
	 */
	public static byte[] getValue(L3Address remoteNode, Address destination) throws RPCException, IOException{
		GETResponse response = primitiveGet(remoteNode, destination, 0);
		if(response instanceof GETResponse.GETValueResponse){
			return ((GETResponse.GETValueResponse) response).getResult();
		}else return null;
	}
	
	/**
	 * Retrieves the value for destination (which is a key).
	 * This method sends get requests to multiple nodes and returns the most popular value.
	 * @param destination
	 * @return
	 * @throws RPCException
	 * @throws IOException
	 */
	public byte[] get(Address destination) throws RPCException, IOException{
		AddressTable neighbors = iterativeLookup(destination);
		HashMap<byte[], Integer> counts = new HashMap<byte[], Integer>();
		for(L3Address neighbor : neighbors.values()){
			byte[] value = getValue(neighbor, destination);
			if(counts.containsKey(value)){
				counts.put(value, counts.get(value) + 1);
			}else{
				counts.put(value, 1);
			}
		}
		byte[] max = new byte[0];
		counts.put(max, -1);
		for(byte[] value : counts.keySet()){
			if(counts.get(value) > counts.get(max))
				max = value;
		}
		return max;
	}
	
	//TODO use some OOD to associate destination and value. destination might have value in it like an L3Address, or destination can be a Class object that is a subtype of Address, and use that class to build an Oaddr from value.
	public static boolean primitivePut(L3Address remoteNode, Address destination, byte[] value) throws IOException, RPCException{
		JSONObject requestObject = null;
		JSONObject responseObject = null;
		L3Address source = getSource();
		RPCBuilder requestBuilder = new RPCBuilder();
		try {
			requestBuilder.setDestinationO(destination);
			requestBuilder.setSourceIP(source.getLayer3Address());
			requestBuilder.setSourcePort(source.getPort());
			requestBuilder.setValue(value);
			requestObject = requestBuilder.buildPUT();
		} catch (RPCException e) {
			DBP.printException(e);
		}
		responseObject = call(remoteNode, requestObject);
		try{
			return responseObject.getBoolean("result");
		}catch(JSONException e){
			throw new RPCException(JSONRPCError.INVALID_RESULT);
		}
	}
	
	private static JSONObject getPut(Address destination, byte[] value){
		L3Address source = getSource();
		RPCBuilder requestBuilder = new RPCBuilder();
		try {
			requestBuilder.setDestinationO(destination);
			requestBuilder.setSourceIP(source.getLayer3Address());
			requestBuilder.setSourcePort(source.getPort());
			requestBuilder.setValue(value);
			return requestBuilder.buildPUT();
		} catch (RPCException e) {
			DBP.printException(e);
		}
		return null;
	}
	
	/**
	 * call put on all nodes near destination. returns the number of nodes that acknowledged storage of value.
	 * @param destination
	 * @param value
	 * @return the number of nodes that acknowledged storage of value.
	 * @throws RPCException
	 * @throws IOException
	 */
	public int put(Address destination, byte[] value) throws RPCException, IOException{
		int ret = 0;
		JSONObject request = getPut(destination, value);
		JSONObject response;
		AddressTable neighbors = iterativeLookup(destination);
		for(L3Address address : neighbors.values()){
			response = call(address, request);
			ret += response.getBoolean("result") ? 1 : 0;
		}
		return ret;
	}
	
	/**
	 * Performs a primitive lookup RPC for destination on remoteNode
	 * @param remoteNode the node to which the RPC will be sent
	 * @param destination the address for which remote the remote node will return nearby neighbors
	 * @return an address table with the nodes nearest to destination that the remote node is aware of
	 * @throws RPCException 
	 */
	public static AddressTable primitiveLookup(L3Address remoteNode, Address destination) throws IOException, RPCException{
		AddressTable ret = null;
		JSONObject requestObject;
		JSONObject responseObject;
		SocketIOWrapper io;
		L3Address source = getSource();
		RPCBuilder requestBuilder = new RPCBuilder();
		requestBuilder.setDestinationO(destination);
		requestBuilder.setSourceIP(source.getLayer3Address());
		requestBuilder.setSourcePort(source.getPort());
		requestObject = requestBuilder.buildLOOKUP();
		
		io = new SocketIOWrapper(new Socket(remoteNode.getLayer3Address(), remoteNode.getPort()));
		io.write(requestObject);
		responseObject = new JSONObject(io.read());
		//handle if response object is invalid JSON RPC response
		//TODO check response ID
		if(!RPCValidator.isValidoopResponse(responseObject)){
			throw new RPCException(JSONRPCError.NODE_SHAT);
		}
		
		// handle if response is an error.
		if(responseObject.has("error")){
			JSONObject error = responseObject.getJSONObject("error");
			if(error.has("data"))
				DBP.printerrorln("lookup request responded with error data: " + error.get("data"));
			throw new RPCException(JSONRPCError.fromJSON(error));
		}
		// get AddressTable from response
		try{
			ret = AddressTable.fromJSONArray(responseObject.getJSONArray("result"));
		}catch (JSONException e){
			throw new RPCException(JSONRPCError.PARSE_ERROR);
		}
		io.close();
		ret.add(remoteNode);
		return ret;
	}
	
	/**
	 * Resolve the network layer addresses and ports of neighbors to destination by routing through the network.
	 * @param destination an overlay address for which nearby layer 3 addresses should be resolved.
	 * @return an AddressTable filled with the nearest neighbors of destination.
	 */
	public AddressTable iterativeLookup(Address destination){//TODO rename this
		AddressTable rrt = bootstrapTable.clone();
		AddressTable nrt = new AddressTable(destination);
		HashSet<L3Address> visited= new HashSet<L3Address>();
		boolean changed = true;
		while(changed){
			for(L3Address a : rrt.values()){
				if(visited.contains(a))
					continue;
				AddressTable candidates = null; // candidates for the NRT returned by a
				//visit a, get addresses
				try{
					candidates = primitiveLookup(a, destination);
				}catch(IOException ioE){
					DBP.printerrorln(ioE);
				}catch (RPCException e) {
					// catch exceptions where a responds over socket, but not with valid lookup response
					DBP.printerrorln(e);
				}
				//remove from rrt if a does not respond
				if(candidates == null){
					rrt.remove(a);
					continue;
				}
				//add addresses to NRT
				for(L3Address c : candidates.values()){
					if(visited.contains(c))
						continue;
					nrt.add(c);
				}
				visited.add(a);
			}
			changed = rrt.addAll(nrt.values());
			nrt.clear();
		}
		return rrt;
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
	 *         but rather the JSON object in the result or error field of the response.
	 *         TODO make make the return match the docs, or make the docs match the actual return
	 * @throws RPCException
	 * @throws IOException 
	 */
	public static JSONObject call(L3Address destination, JSONObject RPC) throws RPCException, IOException {
		JSONObject response;
		SocketIOWrapper io = new SocketIOWrapper(new Socket(destination.getLayer3Address(), destination.getPort()));
		io.write(RPC);
		response = new JSONObject(io.read());
		io.close();
		if(RPCValidator.isValidoopResponse(response)){
			return response;
		}else{
			throw new RPCException(JSONRPCError.NODE_SHAT);
		}
	}

	/**
	 * Send an RPC to all nodes in an AddressTable and return the consensus
	 * response.
	 * 
	 * @param destinations
	 * @return the consensus reply to the RPC. Note: this is not the RPC
	 *         response, but rather the JSON object in the result or error field of the
	 *         response.
	 * @throws RPCException
	 */
	public static JSONObject call(AddressTable destinations, Object RPC)
			throws RPCException {
		return null;
	}

	public static void shutDown(int port) throws IOException {
		SocketIOWrapper io = new SocketIOWrapper(new Socket(InetAddress.getLoopbackAddress(), port));
		io.write(ShutdownRpc.getShutdownRPC().toJSONString());
		//should I read here?
		io.write(ShutdownRpc.HANDSHAKE);
	}
}
