package blackdoor.cqbe.node.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.cqbe.rpc.RPCException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.node.Node;
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
	private OutputStream outy;
	private String errorData = null;

	public RPCHandler(OutputStream outy, JSONObject rpc) {
		this.rpc = rpc;
		this.outy = outy;
	}

	/**
	 * Handles appropriate RPC call
	 * @throws IOException 
	 */
	public void handle() throws IOException {
		JSONObject responseObject;
		try{
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
					outy.close();
					throw new RuntimeException("WTF IS THISSSS??? I'm looking at a method type that I don't recognize! WHERE is the validator? Is it on vacation? Cause it's not validating!");
			}
		}catch(JSONException j){
			DBP.printException(j);
			DBP.printerrorln("Apparently the RPC validator is broken");
			DBP.printerrorln("A JSON-RPC response is not being sent, better fix the validator");
			return;
		}catch(AddressException a){
			DBP.printException(a);
			responseObject = RPCBuilder.RPCResponseFactory(rpc.getInt("id"),false,null, RPCBuilder.JSONRPCError.INVALID_ADDRESS_FORMAT, a.getMessage());
		}catch(RPCException e){
			if(errorData != null)
				responseObject = RPCBuilder.RPCResponseFactory(rpc.getInt("id"),false,null,e.getRPCError(), errorData);
			else
				responseObject = RPCBuilder.RPCResponseFactory(rpc.getInt("id"),false,null,e.getRPCError());
		}
		try(PrintWriter output = new PrintWriter(outy)){
			DBP.printdevln("in handle");
			DBP.printdevln("about to write response " + responseObject);
			output.write(responseObject.toString());
		}
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
	 */
	private JSONObject handlePutRequest() {
		return null;
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
			Address dest = Address.parse(params.getString("destinationO"));//TODO change all "destinationO" to "destO"
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
			throw new RPCException(RPCBuilder.JSONRPCError.INVALID_PARAMS);
		}
		return responseObject;
	}
	
	private JSONObject handleShutdown(){
		return null;
	}

}
