package blackdoor.cqbe.rpc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import blackdoor.cqbe.node.server.RPCHandler;

/**
 * 
 * @author Cyril Van Dyke
 * @version 0.0.3 11/20/2014
 *
 */
public class RPCValidator {
	private String call;
	private OutputStream os;
	
	public RPCValidator(String rpcCall, OutputStream oStream) {
		call = rpcCall;
		os = oStream;
	}
	
	public void handle(){
		JSONObject k = new JSONObject();
		BufferedWriter buffy = new BufferedWriter(new OutputStreamWriter(os));
		try{
			JSONObject.testValidity(call);
			String validity = isValid(call);
			if(validity.equals("valid")){
				//Handle the call by passing off to the handler.
				//String methodCalled = call.getString("method");
				RPCHandler handler = new RPCHandler();
				//handler.handleRPC(call);
			}
			else {
				JSONObject error = buildError(validity,new JSONObject(call).getInt("id"),call);
				try {
					buffy.write(error.toString());
					buffy.flush();
					buffy.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		catch(Exception e){
			JSONObject error = buildError("parse",-1,call);
			try {
				buffy.write(error.toString());
				buffy.flush();
				buffy.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public JSONObject buildError(String errorStyle, int id, String call){
		JSONObject error = new JSONObject();
		JSONObject errorObject = new JSONObject();
		errorObject.put("jsonrpc", "2.0");
		if(errorStyle.equals("parse")){
			error.put("code",-32700);
			error.put("message","Parse Error");
		}
		if(errorStyle.equals("invalid")){
			error.put("code",-32600);
			error.put("message","Invalid Request");
		}
		if(errorStyle.equals("method")){	
			error.put("code",-32601);
			error.put("message","Method not found");
		}
		if(errorStyle.equals("params")){
			error.put("code",-32602);
			error.put("message","Invalid params");
		}
		if(errorStyle.equals("internal")){
			error.put("code",-32603);
			error.put("message","Internal error");
		}
		if(errorStyle.equals("server")){
			error.put("code",-32000);
			error.put("message","Server error");
		}
		errorObject.put("error", error);
		errorObject.put("id",id);
		
		return errorObject;
	}
	
	/**
	 * <p>
	 * Checks for semantics, syntax and if the RPC is supported by this system.
	 *
	 * @param String
	 * @return String detailing whether the JSONObject is valid or not.
	 */
	public String isValid(String call){
		JSONObject jCall = new JSONObject(call);
		JSONObject params = jCall.getJSONObject("params");
		String methodName = jCall.getString("method");
		if(!jCall.has("method") || !params.has("sourceO") || !params.has("sourceIP") || !params.has("sourcePort")
				|| !params.has("destinationO") || !params.has("extensions")){
			return "invalid";
		}
		if(methodName.equals("PUT") && !params.has("value")){
			return "params";
		}
		if(methodName.equals("GET") && !params.has("index")){
			return "params";
		}
		if(methodName.equals("SHUTDOWN") && !params.has("port")){
			return "params";
		}
		if(!methodName.equals("PING") && !methodName.equals("PONG") && !methodName.equals("LOOKUP") && 
				!methodName.equals("PUT") && !methodName.equals("GET") && !methodName.equals("SHUTDOWN")){
			return "method";
		}
		//Check for validity of params
		//Not really sure how to do this with overlay addresses yet lawl
		String ip = params.getString("SourceIP");
		int port = params.getInt("sourcePort");
		final String PATTERN = 
		        "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(ip);
		if(!matcher.matches()){
			return "params";
		}
		if(port < 0 || port > 61001){
			return "params";
		}
		
		return "valid";
	}
	
}
