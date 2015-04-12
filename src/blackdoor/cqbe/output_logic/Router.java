package blackdoor.cqbe.output_logic;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.ws.Response;

import org.json.JSONException;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import blackdoor.cqbe.node.Node;
import blackdoor.cqbe.node.server.RPCHandler;
import blackdoor.cqbe.rpc.AckResponse;
import blackdoor.cqbe.rpc.ErrorRpcResponse;
import blackdoor.cqbe.rpc.GETResponse;
import blackdoor.cqbe.rpc.GETResponse.GETLookupResponse;
import blackdoor.cqbe.rpc.GETResponse.GETResponseFactory;
import blackdoor.cqbe.rpc.GetRpc;
import blackdoor.cqbe.rpc.IndexResult;
import blackdoor.cqbe.rpc.JSONRPCResult;
import blackdoor.cqbe.rpc.JSONRPCResult.ResultType;
import blackdoor.cqbe.rpc.PingRpc;
import blackdoor.cqbe.rpc.PongResult;
import blackdoor.cqbe.rpc.PutRpc;
import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.cqbe.rpc.ResultRpcResponse;
import blackdoor.cqbe.rpc.Rpc;
import blackdoor.cqbe.rpc.RpcResponse;
import blackdoor.cqbe.rpc.ShutdownRpc;
import blackdoor.cqbe.rpc.TableResult;
import blackdoor.cqbe.rpc.ValueResult;
import blackdoor.cqbe.rpc.RPCException.*;
import blackdoor.cqbe.rpc.RPCValidator;
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
		//determine our address
		try{
			me = Node.getAddress();
		}catch(ExceptionInInitializerError e){
			me = new L3Address(InetAddress.getLoopbackAddress(), -1);
		}
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
	public static boolean ping(L3Address remoteNode) throws RPCException {
		RPCBuilder requestBuilder = new RPCBuilder();
		L3Address source = getSource();
		try {
		requestBuilder.setDestinationO(remoteNode);
		requestBuilder.setSourceIP(source.getLayer3Address());
		requestBuilder.setSourcePort(source.getPort());
		requestBuilder.setSource(new L3Address (source.getLayer3Address(), 123));//
		PingRpc ping = requestBuilder.buildPingObject();;
		ResultRpcResponse response = (ResultRpcResponse) call(remoteNode, ping);//check if call returns a pong
		return(response.getResult().getValue().toString() == "pong");
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
	public static JSONRPCResult primitiveGet(L3Address remoteNode, Address destination, int index) throws RPCException, IOException{
		Rpc requestObject;
		L3Address source = getSource();
		RPCBuilder requestBuilder = new RPCBuilder();
		try{
			requestBuilder.setDestinationO(destination);
			requestBuilder.setSourceIP(source.getLayer3Address());
			requestBuilder.setSourcePort(source.getPort());
			requestBuilder.setIndex(index);
			GetRpc request = requestBuilder.buildGetObject();
			ResultRpcResponse response = call(remoteNode, request);
			return response.getResult();
			}catch(RPCException e){
				DBP.printerrorln(e);
			}
			return null;
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
	public static Set<Address> getIndex(L3Address remoteNode, int index) throws RPCException, IOException{
		JSONRPCResult response = null;
		response = primitiveGet(remoteNode, Address.getNullAddress(), index);
		if(response instanceof IndexResult){
			return ((IndexResult)response).getValue();
			//((GETResponse.GETIndexResponse) response).getResult();
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
		JSONRPCResult response = primitiveGet(remoteNode, destination, 0);
		if(response instanceof ValueResult){
			return ((ValueResult) response).getValue();
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
		Rpc requestObject = null;
		RpcResponse responseObject = null;
		L3Address source = getSource();
		RPCBuilder requestBuilder = new RPCBuilder();
		requestBuilder.setDestinationO(destination);
		requestBuilder.setSourceIP(source.getLayer3Address());
		requestBuilder.setSourcePort(source.getPort());
		requestBuilder.setValue(value);
		Rpc put  = requestBuilder.buildPutObject();
		try{
			ResultRpcResponse response = call(remoteNode, put);
			return(response.getResult().getValue().equals(true));
		}catch(RPCException e){
			DBP.printerrorln(e);
			return false;
		}
	}
	
	
	private static Rpc getPut(Address destination, byte[] value) throws RPCException{
		L3Address source = getSource();
		RPCBuilder requestBuilder = new RPCBuilder();
			requestBuilder.setDestinationO(destination);
			requestBuilder.setSourceIP(source.getLayer3Address());
			requestBuilder.setSourcePort(source.getPort());
			requestBuilder.setValue(value);
			return requestBuilder.buildPutObject();
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
		RpcResponse response;
		Rpc request = getPut(destination, value);
		AddressTable neighbors = iterativeLookup(destination);
		for(L3Address address : neighbors.values()){
			response = call(address, request);
			ret += ((ResultRpcResponse) response).getResult() instanceof AckResponse ? 1 : 0;
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
		Rpc requestObject;
		RpcResponse responseObject = null;
		JSONRPCResult result = null;
		SocketIOWrapper io;
		L3Address source = getSource();
		RPCBuilder requestBuilder = new RPCBuilder();
		requestBuilder.setDestinationO(destination);
		requestBuilder.setSourceIP(source.getLayer3Address());
		requestBuilder.setSourcePort(source.getPort());
		requestObject = requestBuilder.buildLookupObject();
		io = new SocketIOWrapper(new Socket(remoteNode.getLayer3Address(), remoteNode.getPort()));
		io.write(requestObject.toString());
		responseObject = ResultRpcResponse.fromJson(io.read());
		// handle if response is an error.
		if(responseObject instanceof ErrorRpcResponse){
			throw new RPCException(((ErrorRpcResponse) responseObject).getError());
		}
		ResultRpcResponse r =  (ResultRpcResponse)responseObject;
		ret = (AddressTable) r.getResult().getValue();
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
	 * @return the consensus reply to RPC. 
	 * @throws RPCException
	 */
	public RpcResponse routeWithCalls(Object RPC) throws RPCException {
		throw new UnsupportedOperationException("method not yet implemented.");
	}

	/**
	 * Send a RPC to destination and return the reply
	 * 
	 * @param destination
	 * @return the reply from destination. 
	 *         TODO make make the return match the docs, or make the docs match the actual return
	 * @throws RPCException
	 * @throws IOException 
	 */
	public static ResultRpcResponse call(L3Address destination, Rpc RPC) throws RPCException, IOException {
		RpcResponse result;
		SocketIOWrapper io = new SocketIOWrapper(new Socket(destination.getLayer3Address(), destination.getPort()));
		io.write(RPC);
		result = RpcResponse.fromJson(io.read());
		if(result instanceof ErrorRpcResponse){
			throw new RPCException(((ErrorRpcResponse) result).getError());
		}
		io.close();
		return (ResultRpcResponse)result;
	}

	/**
	 * Send an RPC to all nodes in an AddressTable and return the consensus
	 * response.
	 * 
	 * @param destinations
	 * @return the consensus reply to the RPC. 
	 * @throws RPCException
	 */
	public static RpcResponse call(AddressTable destinations, Object RPC)
			throws RPCException {
		return null;
	}

	public static void shutDown(int port) throws IOException {
		SocketIOWrapper io = new SocketIOWrapper(new Socket(InetAddress.getLoopbackAddress(), port));
		io.write(ShutdownRpc.getShutdownRPC().toJSONString());
		io.write(ShutdownRpc.HANDSHAKE);
	}
}





