package blackdoor.cqbe.rpc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.examples.Utils;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.sun.net.ssl.internal.www.protocol.https.Handler;

import blackdoor.cqbe.node.server.RPCHandler;

/**
 * 
 * @author Cj Buresch
 * @version 0.0.1 11/3/2014
 *
 */
public class RPCValidator {

	public RPCValidator(String rpcCall, OutputStream os) {
		String call = rpcCall;
	}
	
	public void handle(String call,OutputStream os){
		JSONObject k = new JSONObject();
		BufferedWriter buffy = new BufferedWriter(new OutputStreamWriter(os));
		try{
			k.testValidity(call);
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
	 * Returns True if JSONObject is a valid RPC.
	 * <p>
	 * Checks for semantics, syntax and if the RPC is supported by this system.
	 *
	 * @param JSONObject
	 * @return 
	 * @return True if RPC is valid, false if not.
	 * @throws ProcessingException 
	 * @throws IOException 
	 */
	public String isValid(String call){
		JSONObject jCall = new JSONObject(call);
		JSONObject params = new JSONObject(jCall.get("params"));
		if(!jCall.has("method") || !params.has("sourceO") || !params.has("sourceIP") || !params.has("sourcePort")
				|| !params.has("destinationO") || !params.has("extensions")){
			return "invalid";
		}
		if(jCall.getString("method").equals("PUT") && !params.has("value")){
			return "params";
		}
		if(jCall.getString("method").equals("GET") && !params.has("index")){
			return "params";
		}
		if(jCall.getString("method").equals("SHUTDOWN") && !params.has("port")){
			return "params";
		}
		return "valid";
	}
	
}
