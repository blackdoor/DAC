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
	 * Accepts the response from the RPC handler, calls parser and sends request
	 */
	public void handleResponse(){
		
	}
	
	/**
	 * Parses the response and calls the proper functions
	 */
	public void parseResponse(){
	}
	
	/**
	 * Sends the response to the RPC builder 
	 */
	public void sendResponse(){
		
	}
}
