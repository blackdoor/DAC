package blackdoor.cqbe.rpc;

import java.util.Random;

import org.json.JSONObject;

import blackdoor.cqbe.rpc.RPCException.RequieredParametersNotSet;




/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 3, 2014
 */
public class RPCBuilder {
	
	private String sourceO;
	private String sourceIP;
	private String sourcePort;
	private String destinationO;
	private String value;
	
	private int index;
	private int id;

	public RPCBuilder() {
		setID();
		sourceO = null;
		sourceIP = null;
		sourcePort = null;
		destinationO = null;
		value = null;
		index = -1;
	}

	/**
	 * Initializies a GET RPC JSON request to be sent.
	 * <p>
	 * @return JSON Object with relevant information.
	 */
	public JSONObject buildGET() throws RequieredParametersNotSet {
		if (sourceO == null || sourceIP == null || sourcePort == null || destinationO == null || index == -1) {
			throw new RequieredParametersNotSet();
		}
		else
			JSONObject rpc = new JSONObject();
			rpc.put("jsonrpc", "2.0");
			rpc.put("method", "get");
			JSONObject params = new JSONObject();
			params.put("sourceO", sourceO);
			params.put("sourceIP", sourceIP);
			params.put("sourcePort", sourcePort);
			params.put("destinationO", destinationO);
			params.put("index", index);
			JSONObject extensions = new JSONObject();
			params.put("extensions", extensions);
			rpc.put("params", params);
			rpc.put("id", id);
			return rpc;
	}

	/**
	 * Initializies a PUT RPC JSON request to be sent.
	 * <p>
	 * @return JSON Object.
	 */
	public JSONObject buildPUT() throws RequieredParametersNotSet {
		if (sourceO == null || sourceIP == null || sourcePort == null || destinationO == null || value == null) {
			throw new RequieredParametersNotSet();
		}
		else
			JSONObject rpc = new JSONObject();
			rpc.put("jsonrpc", "2.0");
			rpc.put("method", "put");
			JSONObject params = new JSONObject();
			params.put("sourceO", sourceO);
			params.put("sourceIP", sourceIP);
			params.put("sourcePort", sourcePort);
			params.put("destinationO", destinationO);
			params.put("value", value);
			JSONObject extensions = new JSONObject();
			params.put("extensions", extensions);
			rpc.put("params", params);
			rpc.put("id", id);
			return rpc;
	}
	
	/**
	 * Initializies a LOOKUP RPC JSON request to be sent.
	 * <p>
	 * @return JSON Object with relevant information.
	 */
	public JSONObject buildLOOKUP() throws RequieredParametersNotSet {
		if (sourceO == null || sourceIP == null || sourcePort == null || destinationO == null) {
			throw new RequieredParametersNotSet();
		}
		else
			JSONObject rpc = new JSONObject();
			rpc.put("jsonrpc", "2.0");
			rpc.put("method", "lookup");
			JSONObject params = new JSONObject();
			params.put("sourceO", sourceO);
			params.put("sourceIP", sourceIP);
			params.put("sourcePort", sourcePort);
			params.put("destinationO", destinationO);
			JSONObject extensions = new JSONObject();
			params.put("extensions", extensions);
			rpc.put("params", params);
			rpc.put("id", id);
			return rpc;
	}

	/**
	 * Initializies a PING RPC JSON request to be sent.
	 * <p>
	 * @return JSON Object with relevant information.
	 */
	public JSONObject buildPING() throws RequieredParametersNotSet {
		if (sourceO == null || sourceIP == null || sourcePort == null || destinationO == null) {
			throw new RequieredParametersNotSet();
		}
		else
			JSONObject rpc = new JSONObject();
			rpc.put("jsonrpc", "2.0");
			rpc.put("method", "ping");
			JSONObject params = new JSONObject();
			params.put("sourceO", sourceO);
			params.put("sourceIP", sourceIP);
			params.put("sourcePort", sourcePort);
			params.put("destinationO", destinationO);
			JSONObject extensions = new JSONObject();
			params.put("extensions", extensions);
			rpc.put("params", params);
			rpc.put("id", id);
			return rpc;
	}
	
	/**
	 * Initiliazes a SHUTDOWN RPC JSON request to be sent
	 *
	 * @param port - Port to be shutdown
	 */
	public JSONObject buildSHUTDOWN() throws RequieredParametersNotSet {
		if (sourceO == null || sourceIP == null || sourcePort == null || destinationO == null) {
			throw new RequieredParametersNotSet();
		}
		else
			JSONObject rpc = new JSONObject();
			rpc.put("jsonrpc", "2.0");
			rpc.put("method", "shutdown");
			JSONObject params = new JSONObject();
			params.put("sourceO", sourceO);
			params.put("sourceIP", sourceIP);
			params.put("sourcePort", sourcePort);
			params.put("destinationO", destinationO);
			JSONObject extensions = new JSONObject();
			params.put("extensions", extensions);
			rpc.put("params", params);
			rpc.put("id", id);
			return rpc;
	}

	private void setID() {
	  Random rand = new Random();
	  id = rand.nextInt();
	}

	public String getSourceO() {
		return sourceO;
	}

	public void setSourceO(String sourceO) {
		this.sourceO = sourceO;
	}

	public String getSourceIP() {
		return sourceIP;
	}

	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}

	public String getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}

	public String getDestinationO() {
		return destinationO;
	}

	public void setDestinationO(String destinationO) {
		this.destinationO = destinationO;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getId() {
		return id;
	}

}