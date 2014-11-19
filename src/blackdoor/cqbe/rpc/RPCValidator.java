package blackdoor.cqbe.rpc;

import java.io.IOException;
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

	public RPCValidator(String call, Socket s) {
		Socket socket = s;
	}
	
	public void handle(String call){
		JSONObject k = new JSONObject();
		try{
			k.testValidity(call);
			if(isValid(call)){
				//Handle the call by passing off to the handler.
				//String methodCalled = call.getString("method");
				RPCHandler handler = new RPCHandler();
				//handler.handleRPC(call);
			}
			else {
				
			}
		}
		catch(Exception e){

		}

	}

	public JSONObject buildError(String errorStyle, String id, String call){
		JSONObject error = new JSONObject();
		JSONObject errorObject = new JSONObject();
		if(errorStyle.equals("i")){
			
		}
		if(errorStyle.equals("bm")){	
			error.append("code",-32601);
			error.append("message","Method not found");
		}
		errorObject.append("error", error);
		errorObject.append("id",id);
		
		return error;
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
	public boolean isValid(String call){
		JSONObject jCall = new JSONObject(call);
		JSONObject params = new JSONObject(jCall.get("params"));
		if(!jCall.has("method") || !params.has("sourceO") || !params.has("sourceIP") || !params.has("sourcePort")
				|| !params.has("destinationO") || !params.has("extensions")){
			return false;
		}
		if(jCall.getString("method").equals("PUT") && !params.has("value")){
			return false;
		}
		if(jCall.getString("method").equals("GET") && !params.has("index")){
			return false;
		}
		if(jCall.getString("method").equals("SHUTDOWN") && !params.has("port")){
			return false;
		}
		return true;
	}
	
}
