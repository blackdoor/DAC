package blackdoor.cqbe.node.server;

public class Server {

	public Server() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Listens for an incoming RPC request
	 */
	public void listen(){
	}
	
	/**
	 * Pushes an RPC request to the RPC-Handler
	 */
	public void pushRequest(){
	}
	
	/**
	 * Accepts the response from the RPC-Handler, sends to RPC-Builder
	 */
	public void sendToBuilder(){
	}
	
	/**
	 * Listens for a returned RPC from builder.
	 * Calls sendResponse() after.
	 */
	public void receiveBuiltRPC(){
	}
	
	/**
	 * Sends the built RPC given by RPC Builder
	 */
	public void sendResponse(){
		
	}
}
