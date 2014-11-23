package blackdoor.cqbe.rpc;

import java.net.InetAddress;
import java.util.Random;

import org.json.JSONObject;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.rpc.RPCException.RequiredParametersNotSetException;




/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 3, 2014
 */
public class RPCBuilder {

	private InetAddress sourceIP;
	private int sourcePort;
	private Address destinationO;
	private String value;
	
	private int index;
	private int id;

	public RPCBuilder() {
		setID();
		sourcePort = -1;
		index = -1;
	}
	
	private JSONObject getDefaultParams() throws RequiredParametersNotSetException{
		if (sourceIP == null || sourcePort == -1 || destinationO == null) {
			throw new RequiredParametersNotSetException();
		}
		JSONObject params = new JSONObject();
		params.put("sourceO", new L3Address(sourceIP, sourcePort).overlayAddressToString());
		params.put("sourceIP", sourceIP.getHostAddress());
		params.put("sourcePort", sourcePort);
		params.put("destinationO", destinationO.overlayAddressToString());
		return params;
	}

	/**
	 * Initializies a GET RPC JSON request to be sent.
	 * <p>
	 * @return JSON Object with relevant information.
	 */
	public JSONObject buildGET() throws RequiredParametersNotSetException {
		if (sourceIP == null || sourcePort == -1 || destinationO == null || index == -1) {
			throw new RequiredParametersNotSetException();
		}
		else{
			JSONObject rpc = new JSONObject();
			rpc.put("jsonrpc", "2.0");
			rpc.put("method", "get");
			JSONObject params = getDefaultParams();
			params.put("index", index);
			JSONObject extensions = new JSONObject();
			params.put("extensions", extensions);
			rpc.put("params", params);
			rpc.put("id", id);
			return rpc;
		}
	}

	/**
	 * Initializies a PUT RPC JSON request to be sent.
	 * <p>
	 * @return JSON Object.
	 */
	public JSONObject buildPUT() throws RequiredParametersNotSetException {
		if (sourceIP == null || sourcePort == -1 || destinationO == null || value == null) {
			throw new RequiredParametersNotSetException();
		}
		else{
			JSONObject rpc = new JSONObject();
			rpc.put("jsonrpc", "2.0");
			rpc.put("method", "put");
			JSONObject params = getDefaultParams();
			params.put("value", value);
			JSONObject extensions = new JSONObject();
			params.put("extensions", extensions);
			rpc.put("params", params);
			rpc.put("id", id);
			return rpc;
		}
	}
	
	/**
	 * Initializies a LOOKUP RPC JSON request to be sent.
	 * <p>
	 * @return JSON Object with relevant information.
	 */
	public JSONObject buildLOOKUP() throws RequiredParametersNotSetException {
		if (sourceIP == null || sourcePort == -1 || destinationO == null) {
			throw new RequiredParametersNotSetException();
		}
		else
		{
			JSONObject rpc = new JSONObject();
			rpc.put("jsonrpc", "2.0");
			rpc.put("method", "lookup");
			JSONObject params = getDefaultParams();
			JSONObject extensions = new JSONObject();
			params.put("extensions", extensions);
			rpc.put("params", params);
			rpc.put("id", id);
			return rpc;
		}
	}

	/**
	 * Initializies a PING RPC JSON request to be sent.
	 * <p>
	 * @return JSON Object with relevant information.
	 */
	public JSONObject buildPING() throws RequiredParametersNotSetException {
		if (sourceIP == null || sourcePort == -1 || destinationO == null) {
			throw new RequiredParametersNotSetException();
		}
		else{
			JSONObject rpc = new JSONObject();
			rpc.put("jsonrpc", "2.0");
			rpc.put("method", "ping");
			JSONObject params = getDefaultParams();
			JSONObject extensions = new JSONObject();
			params.put("extensions", extensions);
			rpc.put("params", params);
			rpc.put("id", id);
			return rpc;
		}
	}
	
	/**
	 * Initiliazes a SHUTDOWN RPC JSON request to be sent
	 *
	 * @param port - Port to be shutdown
	 */
	public JSONObject buildSHUTDOWN() throws RequiredParametersNotSetException {
		if (sourceIP == null || sourcePort == -1 || destinationO == null) {
			throw new RequiredParametersNotSetException();
		}
		else{
			JSONObject rpc = new JSONObject();
			rpc.put("jsonrpc", "2.0");
			rpc.put("method", "shutdown");
			JSONObject params = getDefaultParams();
			JSONObject extensions = new JSONObject();
			params.put("extensions", extensions);
			rpc.put("params", params);
			rpc.put("id", id);
			return rpc;
		}
	}

	private void setID() {
	  Random rand = new Random();
	  id = rand.nextInt(Integer.MAX_VALUE);
	}

	public Address getSourceO() {
		return new L3Address(sourceIP, sourcePort);
	}

	public InetAddress getSourceIP() {
		return sourceIP;
	}

	public void setSourceIP(InetAddress sourceIP) {
		this.sourceIP = sourceIP;
	}

	public int getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(int sourcePort) {
		this.sourcePort = sourcePort;
	}

	public Address getDestinationO() {
		return destinationO;
	}

	public void setDestinationO(Address destinationO) {
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