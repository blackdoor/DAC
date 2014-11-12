package blackdoor.cqbe.rpc;

import org.json.JSONObject;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 3, 2014
 */
public class RPCBuilder {

	public RPCBuilder() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Initializies a GET RPC JSON request to be sent.
	 * <p>
	 * 
	 * @parem ?
	 * @parem ??
	 * @parem ???
	 * @return JSON Object with relevant information.
	 */
	public void buildGET() {
	}

	/**
	 * Initializies a PUT RPC JSON request to be sent.
	 * <p>
	 * 
	 * @parem ?
	 * @parem ??
	 * @parem ???
	 * @return JSON Object.
	 */
	public void buildPUT() {
	}

	/**
	 * Initializies a LOOKUP RPC JSON request to be sent.
	 * <p>
	 * 
	 * @parem ?
	 * @parem ??
	 * @parem ???
	 * @return JSON Object with relevant information.
	 */
	public void buildLOOKUP() {
	}

	/**
	 * Initializies a PING RPC JSON request to be sent.
	 * <p>
	 * 
	 * @parem ?
	 * @parem ??
	 * @parem ???
	 * @return JSON Object with relevant information.
	 */
	public void buildPING() {
	}
	
	/**
	 * Initiliazes a SHUTDOWN RPC JSON request to be sent
	 *
	 * @param port - Port to be shutdown
	 */
	public JSONObject buildSHUTDOWN(int port) {
		return null;
	}
}
