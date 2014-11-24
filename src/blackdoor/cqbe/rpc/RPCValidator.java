package blackdoor.cqbe.rpc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
				JSONObject error = buildError(validity,new JSONObject(call).getString("id"),call);
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
			JSONObject error = buildError("parse",null,call);
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

	public JSONObject buildError(String errorStyle, String id, String call){
		JSONObject error = new JSONObject();
		JSONObject errorObject = new JSONObject();
		errorObject.append("jsonrpc", "2.0");
		if(errorStyle.equals("parse")){
			error.append("code",-32700);
			error.append("message","Parse Error");
		}
		if(errorStyle.equals("invalid")){
			error.append("code",-32600);
			error.append("message","Invalid Request");
		}
		if(errorStyle.equals("method")){	
			error.append("code",-32601);
			error.append("message","Method not found");
		}
		if(errorStyle.equals("params")){
			error.append("code",-32602);
			error.append("message","Invalid params");
		}
		if(errorStyle.equals("internal")){
			error.append("code",-32603);
			error.append("message","Internal error");
		}
		if(errorStyle.equals("server")){
			error.append("code",-32000);
			error.append("message","Server error");
		}
		errorObject.append("error", error);
		errorObject.append("id",id);
		
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
		JSONObject params = new JSONObject(jCall.get("params"));
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
		if(!methodName.equals("PING") || !methodName.equals("PONG") || !methodName.equals("LOOKUP") || 
				!methodName.equals("PUT") || !methodName.equals("GET") || !methodName.equals("SHUTDOWN")){
			return "method";
		}
		return "valid";
	}
	
}
