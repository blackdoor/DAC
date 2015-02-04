package blackdoor.cqbe.node.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import blackdoor.cqbe.rpc.AckResponse;
import blackdoor.cqbe.rpc.PutRpc;
import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.cqbe.rpc.RPCException;

import blackdoor.cqbe.rpc.RPCException.JSONRPCError;
import blackdoor.cqbe.rpc.Rpc;
import blackdoor.cqbe.storage.StorageController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.CASFileAddress;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.node.Node;
import blackdoor.net.SocketIOWrapper;
import blackdoor.util.DBP;

/**
 * 
 * @author nfischer3
 *
 * Custom RPC response codes:
 * -32001 - Malformed addresses
 */
public class RPCHandler {
	
	private JSONObject rpc;
	private String errorData = null;

	public RPCHandler(JSONObject rpc) {
		this.rpc = rpc;
	}

	/**
	 * Handles appropriate RPC call
	 * @return 
	 * @throws IOException 
	 */
	public JSONObject handle() throws IOException {
		
		JSONObject responseObject;
		try{
			addRequestSenderToAT();
			
			switch(rpc.getString("method")){
				case "get":
					responseObject = handleGetRequest();
					break;
				case "put":
					responseObject = handlePutRequest();
					break;
				case "lookup":
					responseObject = handleLookupRequest();
					break;
				case "ping":
					responseObject = handlePingRequest();
					break;
				case "shutdown":
					responseObject = handleShutdown();
					throw new ShutdownInterrupt();
				default:
					throw new RuntimeException("WTF IS THISSSS??? I'm looking at a method type that I don't recognize! WHERE is the validator? Is it on vacation? Cause it's not validating!");
			}
		}catch(JSONException j){
			DBP.printException(j);
			DBP.printerrorln("Apparently the RPC validator is broken");
			DBP.printerrorln("A JSON-RPC response is not being sent, better fix the validator");
			return null;
		}catch(AddressException a){
			DBP.printException(a);
			responseObject = RPCBuilder.RPCResponseFactory(rpc.getInt("id"),false,null, RPCException.JSONRPCError.INVALID_ADDRESS_FORMAT, a.getMessage());
		}catch(RPCException e){
			if(errorData != null)
				responseObject = RPCBuilder.RPCResponseFactory(rpc.getInt("id"),false,null,e.getRPCError(), errorData);
			else
				responseObject = RPCBuilder.RPCResponseFactory(rpc.getInt("id"),false,null,e.getRPCError());
		}catch(UnknownHostException e){
			responseObject = RPCBuilder.RPCResponseFactory(rpc.getInt("id"), false, null, RPCException.JSONRPCError.INVALID_ADDRESS_FORMAT);
			DBP.printException(e);
		}
		return responseObject;
	}
	
	/**
	 * adds sender ip and port from rpc to this nodes's address table if applicable. 
	 * DOES NOT CHECK validity
	 * @throws JSONException 
	 * @throws UnknownHostException 
	 */
	private void addRequestSenderToAT() throws UnknownHostException, JSONException{
		JSONObject params = rpc.getJSONObject("params"); 
		L3Address sender = new L3Address(InetAddress.getByName(params.getString("sourceIP")), params.getInt("sourcePort"));
		Node.getAddressTable().add(sender);
	}

	/**
	 * Sends response to server, to be sent to the RPC builder
	 */
	private void sendRPC() {

	}

	/**
	 * Accepts an RPC request and calls further functions to handle it
	 */
	private void acceptRPC() {

	}

	/**
	 * Checks the validity of an RPC statement using the RPC Validator
	 * 
	 * @return True if valid RPC statement, False otherwise
	 */
	private Boolean checkValidity() {
		return null;
	}

	/**
	 * Returns an error-message to the server
	 */
	private void sendError() {
	}

	/**
	 * Determines the nature of the request after validity
	 */
	private void parseRequest() {
	}

	/**
	 * Handles a ping request
	 */
	private JSONObject handlePingRequest() {
		return RPCBuilder.RPCResponseFactory(rpc.getInt("id"), true, "pong", null);
	}

	/**
	 * Handles a put request
	 * @throws RPCException 
	 */
	private JSONObject handlePutRequest() throws RPCException {
		StorageController storageController = Node.getStorageController();
		PutRpc rpc = (PutRpc) Rpc.fromJson(this.rpc);
		//TODO look at settings and find out how large of a value we are willing to store
		//throw exception if value is oversized
		try {
			CASFileAddress value = new CASFileAddress(storageController.getDomain(), rpc.getValue());
			storageController.put(value);
		} catch (IOException e) {
			throw new RPCException(JSONRPCError.NODE_SHAT);
		}
		AckResponse response = new AckResponse(rpc.getId());
		return response.toJSON();
	}

	/**
	 * Handles a get request
	 */

	private JSONObject handleGetRequest() {
		return null;
	}

	/**
	 *
	 * @return A JSON response object to be sent back over the socket/stream
	 * @throws AddressException
	 * @throws UnknownHostException
	 * @throws RPCException
	 */
	private JSONObject handleLookupRequest() throws AddressException, UnknownHostException, RPCException {
		JSONObject responseObject;
		try {
			JSONObject params = rpc.getJSONObject("params");
			Address dest = new Address(params.getString("destinationO"));//TODO change all "destinationO" to "destO"
			Address src = new L3Address(InetAddress.getByName(params.getString("sourceIP")), params.getInt("sourcePort"));  //TODO change all "source" to "src"
			AddressTable nodeTable = Node.getAddressTable();
			AddressTable nearest = nodeTable.getNearestAddresses(Node.getN() + 1, dest);
			nearest.remove(src.getOverlayAddress());
			JSONArray result = new JSONArray();
			JSONObject entry;
			for (L3Address a : nearest.values()) {
				entry = new JSONObject();
				entry.put("overlay", a.overlayAddressToString());
				entry.put("IP", a.getLayer3Address().getHostAddress());
				entry.put("port", a.getPort());
				result.put(entry);
			}
			responseObject = RPCBuilder.RPCResponseFactory(rpc.getInt("id"), true, result, null);
		}catch(JSONException e){
			errorData = e.getMessage();
			throw new RPCException(RPCException.JSONRPCError.INVALID_PARAMS);
		}
		return responseObject;
	}
	
	private JSONObject handleShutdown(){
		return null;
	}

}
