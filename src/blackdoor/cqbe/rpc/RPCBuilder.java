package blackdoor.cqbe.rpc;

import java.net.InetAddress;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.L3Address;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 3, 2014
 */
public class RPCBuilder {

	private InetAddress sourceIP;
	private int sourcePort;
	private Address destinationO;
	private byte[] value;
	
	private int index;
	private int id;

	public RPCBuilder() {
		setID();
		sourcePort = -1;
		index = -1;
	}
	
	private JSONObject getDefaultParams() throws RPCException {
		if (sourceIP == null || sourcePort == -1 || destinationO == null) {
			throw new RPCException(RPCException.JSONRPCError.INVALID_PARAMS);
		}
		else
		{
			JSONObject params = new JSONObject();
			params.put("sourceO", new L3Address(sourceIP, sourcePort).overlayAddressToString());
			params.put("sourceIP", sourceIP.getHostAddress());
			params.put("sourcePort", sourcePort);
			params.put("destinationO", destinationO.overlayAddressToString());
			return params;
		}
	}

	/**
	 * Initializies a GET RPC JSON request to be sent.
	 * <p>
	 * @return JSON Object with relevant information.
	 */
	public JSONObject buildGET() throws RPCException {
		if (sourceIP == null || sourcePort == -1 || destinationO == null || index == -1) {
			throw new RPCException(RPCException.JSONRPCError.INVALID_PARAMS);
		}
		else{
			JSONObject rpc = new JSONObject();
			rpc.put("jsonrpc", "2.0");
			rpc.put("method", "GET");
			JSONObject params = getDefaultParams();
			params.put("index", index);
			JSONObject extensions = new JSONObject();
			params.put("extensions", extensions);
			rpc.put("params", params);
			rpc.put("id", id);
			return rpc;
		}
	}
	
	public GetRpc buildGetObject(){
		if(destinationO == null || sourceIP == null|| sourcePort == -1|| index == -1)
			throw new RPCException.RPCCreationException("not enough parameters set");
		GetRpc ret = new GetRpc();
		ret.setDestination(getDestinationO());
		ret.setSource(new L3Address(getSourceIP(), getSourcePort()));
		ret.index = getIndex();
		return ret;
	}

	/**
	 * Initializies a PUT RPC JSON request to be sent.
	 * <p>
	 * @return JSON Object.
	 */
	public JSONObject buildPUT() throws RPCException  {
		if (sourceIP == null || sourcePort == -1 || destinationO == null || value == null) {
			throw new RPCException(RPCException.JSONRPCError.INVALID_PARAMS);
		}
		else{
			JSONObject rpc = new JSONObject();
			rpc.put("jsonrpc", "2.0");
			rpc.put("method", "PUT");
			JSONObject params = getDefaultParams();
			params.put("value", Base64.encode(value));
			JSONObject extensions = new JSONObject();
			params.put("extensions", extensions);
			rpc.put("params", params);
			rpc.put("id", id);
			return rpc;
		}
	}
	
	public PutRpc buildPutObject(){
		PutRpc ret = new PutRpc();
		populateDefaultParams(ret);
		ret.setValue(this.value);
		return ret;
	}
	
	/**
	 * Initializies a LOOKUP RPC JSON request to be sent.
	 * <p>
	 * @return JSON Object with relevant information.
	 */
	public JSONObject buildLOOKUP() throws RPCException  {
		if (sourceIP == null || sourcePort == -1 || destinationO == null) {
			throw new RPCException(RPCException.JSONRPCError.INVALID_PARAMS);
		}
		else
		{
			JSONObject rpc = new JSONObject();
			rpc.put("jsonrpc", "2.0");
			rpc.put("method", "LOOKUP");
			JSONObject params = getDefaultParams();
			JSONObject extensions = new JSONObject();
			params.put("extensions", extensions);
			rpc.put("params", params);
			rpc.put("id", id);
			return rpc;
		}
	}
	
	private void populateDefaultParams(Rpc rpc){
		if(destinationO == null || sourceIP == null || sourcePort <= 0)
			throw new RPCException.RPCCreationException("not enough parameters set");
		rpc.setDestination(getDestinationO());
		rpc.setSource(new L3Address(getSourceIP(), getSourcePort()));
	}
	
	public LookupRpc buildLookupObject(){
		if(destinationO == null || sourceIP == null || sourcePort <= 0)
			throw new RPCException.RPCCreationException("not enough parameters set");
		LookupRpc ret = new LookupRpc();
		ret.setDestination(getDestinationO());
		ret.setSource(new L3Address(getSourceIP(), getSourcePort()));
		return ret;
	}

	/**
	 * Initializies a PING RPC JSON request to be sent.
	 * <p>
	 * @return JSON Object with relevant information.
	 */
	public JSONObject buildPING() throws RPCException {
		if (sourceIP == null || sourcePort == -1 || destinationO == null) {
			throw new RPCException(RPCException.JSONRPCError.INVALID_PARAMS);
		}
		else{
			JSONObject rpc = new JSONObject();
			rpc.put("jsonrpc", "2.0");
			rpc.put("method", "PING");
			JSONObject params = getDefaultParams();
			JSONObject extensions = new JSONObject();
			params.put("extensions", extensions);
			rpc.put("params", params);
			rpc.put("id", id);
			return rpc;
		}
	}
	
	public PingRpc buildPingObject(){
		if(destinationO == null || sourceIP == null || sourcePort <= 0)
			throw new RPCException.RPCCreationException("not enough parameters set");
		PingRpc ret = new PingRpc();
		ret.setDestination(getDestinationO());
		ret.setSource(new L3Address(getSourceIP(), getSourcePort()));
		return ret;
	}
	
	/**
	 * Initiliazes a SHUTDOWN RPC JSON request to be sent
	 *
	 */
	public JSONObject buildSHUTDOWN() throws RPCException {
		if (sourceIP == null || sourcePort == -1 || destinationO == null) {
			throw new RPCException(RPCException.JSONRPCError.INVALID_PARAMS);
		}
		else{
			JSONObject rpc = new JSONObject();
			rpc.put("jsonrpc", "2.0");
			rpc.put("method", "SHUTDOWN");
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

	public L3Address getSource() {
		return new L3Address(sourceIP, sourcePort);
	}

	@Deprecated
	public InetAddress getSourceIP() {
		return sourceIP;
	}

	@Deprecated
	public void setSourceIP(InetAddress sourceIP) {
		this.sourceIP = sourceIP;
	}

	@Deprecated
	public int getSourcePort() {
		return sourcePort;
	}

	@Deprecated
	public void setSourcePort(int sourcePort) {
		this.sourcePort = sourcePort;
	}

	public void setSource(L3Address source){
		this.sourceIP = source.getLayer3Address();
		this.sourcePort = source.getPort();
	}

	public Address getDestinationO() {
		return destinationO;
	}

	public void setDestinationO(Address destinationO) {
		this.destinationO = destinationO;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
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


	/**

	 * A factory for JSON RPC responses. Creates both successful and unsuccessful responses.
	 * If successful then error and errorData are irrelevant. else error and errorData are considered and result is irrelevant.
	 * @param id the id of the JSON RPC request object for which this response is being created. If the error was a parse error or the request was a notification then id should be null.
	 * @param successful a boolean indicating if this is a response to a successful request or not i.e. true if creating a response, false if creating an error.
	 * @param result ignored if !successful
	 * @param error ignored if successful
	 * @param errorData ignored if successful
	 * @return a JSON RPC response object
	 */
	public static JSONObject RPCResponseFactory(Integer id, boolean successful, Object result, RPCException.JSONRPCError error, Object errorData ){
		JSONObject response = new JSONObject();
		response.put("jsonrpc", "2.0");
		if(id == null){
			if(successful)
				throw new RPCException.RPCCreationException("Awww Heeeelll naw. id is null and successful is true.");
			response.put("id", JSONObject.NULL);
		}else {
			response.put("id", id);
		}
		if(successful){
			response.put("result", result);
		}else{
			JSONObject errorObject = new JSONObject();
			errorObject.put("code", error.getCode());
			errorObject.put("message", error.getMessage());
			if(errorData != null)
				errorObject.put("data", errorData);
			response.put("error", errorObject);
		}
		return response;
	}

	/**
	 * Convenience method for making an RPC response that is either a successful response or whose error object will not have a "data" member.
	 * Equivalent to calling RPCResponseFactory(id, successful, result, error, null);
	 * @param id
	 * @param successful
	 * @param result
	 * @param error
	 * @return
	 */
	public static JSONObject RPCResponseFactory(Integer id, boolean successful, Object result, RPCException.JSONRPCError error){
		return RPCResponseFactory(id, successful, result, error, null);
	}
}