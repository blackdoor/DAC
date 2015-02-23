package blackdoor.cqbe.rpc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import blackdoor.cqbe.addressing.*;
import blackdoor.cqbe.rpc.RPCException.JSONRPCError;

/**
* Created by nfischer3 on 1/22/15.
*/
public abstract class Rpc {
	
	public static Rpc fromJson(JSONObject rpcJson) throws RPCException {
		Rpc rpcObject = null;
		try {
			switch (rpcJson.getString("method")) {
			case "PUT":
				rpcObject = new PutRpc();
				PutRpc rpcObjectCast = (PutRpc) rpcObject;
				try {
					rpcObjectCast.value = Base64.decode(rpcJson.getJSONObject("params")
							.getString("value"));
				} catch (Base64DecodingException e) {
					throw new RPCException(JSONRPCError.INVALID_BASE64);
				} catch (JSONException e) {
					throw new RPCException(JSONRPCError.INVALID_PARAMS);
				}
				break;
			case "SHUTDOWN":
				rpcObject = new ShutdownRpc(rpcJson.toString());
				return rpcObject;
			case "GET":
				rpcObject = new GetRpc();
				GetRpc rpcGetObject = (GetRpc) rpcObject;
				try {
					rpcGetObject.index = rpcJson.getJSONObject("params")
							.getInt("index");
				}
				catch (JSONException e){
					throw new RPCException(JSONRPCError.INVALID_PARAMS);
				}
				rpcObject.method = Method.GET;
				break;
			case "PING":
				rpcObject = new PingRpc();
				rpcObject.method = Method.PING;
				break;
			case "LOOKUP":
				rpcObject = new LookupRpc();
				rpcObject.method = Method.LOOKUP;
				break;

			default:
				throw new RPCException(JSONRPCError.METHOD_NOT_FOUND);
			}
		} catch (JSONException jsE) {
			throw new RPCException(JSONRPCError.INVALID_REQUEST);
		}

		populateCommonFields(rpcObject, rpcJson);

		return rpcObject;
	}
	
   public static Rpc fromJsonString(String jsonText) throws RPCException{
	   //TODO validate string
	   JSONObject rpcJson;
	   try{
		   rpcJson = new JSONObject(jsonText);
	   }catch(JSONException e){
		   throw new RPCException(JSONRPCError.PARSE_ERROR);
	   }

	   return fromJson(rpcJson);

   }
   
   protected static void populateCommonFields(Rpc rpcObject, JSONObject rpcJson) throws RPCException{
	   try{
		   JSONObject params = rpcJson.getJSONObject("params");
		   rpcObject.source = new L3Address(InetAddress.getByName(params.getString("sourceIP")), params.getInt("sourcePort"));
		   rpcObject.destination = new Address(params.getString("destinationO"));
	   }catch(UnknownHostException | AddressException addressE){
		   throw new RPCException(JSONRPCError.INVALID_ADDRESS_FORMAT);
	   }catch(JSONException jsE){
		   throw new RPCException(JSONRPCError.INVALID_PARAMS);
	   }
	   
	   try{
		   rpcObject.id = rpcJson.getInt("id");
	   }catch(JSONException e){
		   throw new RPCException(JSONRPCError.INVALID_REQUEST);
	   }
   }

   private Method method;
   private L3Address source;
   private Address destination;
   private int id;
   
   protected Rpc(Method method){
	   this.method = method;
	   id = new Random().nextInt();
   }
   
   
	/**
	 * @param method
	 *            the method to set
	 */
	protected void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	protected void setSource(L3Address source) {
		this.source = source;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	protected void setDestination(Address destination) {
		this.destination = destination;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	protected void setId(int id) {
		this.id = id;
	}

	public Method getMethod() {
		return method;
	}

	public L3Address getSource() {
		return source;
	}

	public Address getDestination() {
		return destination;
	}

	public int getId() {
		return id;
	}


   protected JSONObject getRpcOuterShell(){
	   JSONObject shell = new JSONObject();
	   shell.put("jsonrpc", "2.0");
	   shell.put("method", getMethod().toString());
	   shell.put("id", new Random().nextInt(Integer.MAX_VALUE));
	   return shell;
   }
   
   protected JSONObject getRpcParameterShell(){
	   JSONObject shell = new JSONObject();
	   //shell.put("sourceO", getSource().overlayAddressToString());
	   shell.put("sourceIP", getSource().getLayer3Address().getHostName());
	   shell.put("sourcePort", getSource().getPort());
	   shell.put("destinationO", getDestination().overlayAddressToString());
	   return shell;
   }
   
   public abstract JSONObject toJSON();
   
   public String toJSONString(){
	   return toJSON().toString();
   }

   public static enum Method{
       GET, PUT, LOOKUP, PING, SHUTDOWN
   }
}


