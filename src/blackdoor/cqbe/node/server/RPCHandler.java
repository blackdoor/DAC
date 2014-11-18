package blackdoor.cqbe.node.server;

import org.json.JSONObject;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 5, 2014
 */
public class RPCHandler {

	public RPCHandler() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Handles appropriate RPC call Only publicly-facing function
	 */
	public void handleRPC(JSONObject call) {

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
	private void handlePingRequest() {
	}

	/**
	 * Handles a put request
	 */
	private void handlePutRequest() {
	}

	/**
	 * Handles a get request
	 */

	private void handleGetRequest() {
	}

	/**
	 * Handles a lookup request
	 */
	private void handleLookupRequest() {
	}

}
