package blackdoor.cqbe.rpc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import blackdoor.cqbe.addressing.*;
import blackdoor.cqbe.rpc.RPCException.JSONRPCError;

/**
* Created by nfischer3 on 1/22/15.
*/
public abstract class Rpc {
	
   public static Rpc fromJsonString(String jsonText) throws RPCException{// UnknownHostException, JSONException, AddressException{
	   //TODO validate string
	   JSONObject rpcJson;
	   try{
		   rpcJson = new JSONObject(jsonText);
	   }catch(JSONException e){
		   throw new RPCException(JSONRPCError.PARSE_ERROR);
	   }
	   Rpc rpcObject = null;
	   try{
		   switch(rpcJson.getString("method")){
		   case "PUT":
			   break;
		   case "SHUTDOWN":
			   break;
		   case "GET":
			   rpcObject = new GetRpc();
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
	   }catch(JSONException jsE){
		   throw new RPCException(JSONRPCError.INVALID_REQUEST);
	   }
	   
	   populateCommonFields(rpcObject, rpcJson);
	   
	   return rpcObject;
   }
   
   private static void populateCommonFields(Rpc rpcObject, JSONObject rpcJson) throws RPCException{
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

   protected Method method;
   protected L3Address source;
   protected Address destination;
   protected int id;
   
   protected Rpc(Method method){
	   this.method = method;
	   id = new Random().nextInt();
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


