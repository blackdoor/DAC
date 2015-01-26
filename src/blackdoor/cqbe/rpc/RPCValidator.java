package blackdoor.cqbe.rpc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import blackdoor.util.DBP;

import org.json.JSONException;
import org.json.JSONObject;

import blackdoor.cqbe.node.server.RPCHandler;
import blackdoor.cqbe.rpc.RPCException.JSONRPCError;

/**
 * 
 * @author Cyril Van Dyke
 * @version 0.0.4 12/03/2014
 *
 */
public class RPCValidator {
	private String call;
	private OutputStream os;

	public RPCValidator(String rpcCall, OutputStream oStream) {
		call = rpcCall;
		os = oStream;
	}

	public void handle() {
		BufferedWriter buffy = new BufferedWriter(new OutputStreamWriter(os));
		String validity = isValid(call);
		if (validity.equals("valid")) {
			DBP.printdemoln("RPC is valid, handing off to RPCHandler");
			// Handle the call by passing off to the handler.
			// String methodCalled = call.getString("method");
			RPCHandler handler = new RPCHandler();
			// handler.handleRPC(call);
		} else {
			JSONObject error = buildError(
					validity,
					validity.equals("parse") 
						? -1 
						: new JSONObject(call).getInt("id"));
			try {
				buffy.write(error.toString());
				buffy.flush();
				buffy.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				DBP.printException(e1);
			}
		}
	}

	public JSONObject buildError(String errorStyle, int id) {
		JSONObject error = null;
		Integer _id = id == -1 ? null : id;
		if (errorStyle.equals("parse")) 
			error = RPCBuilder.RPCResponseFactory(_id, false, null, RPCException.JSONRPCError.PARSE_ERROR);
		if (errorStyle.equals("invalid")) 
			error = RPCBuilder.RPCResponseFactory(_id, false, null, RPCException.JSONRPCError.INVALID_REQUEST);
		if (errorStyle.equals("method")) 
			error = RPCBuilder.RPCResponseFactory(_id, false, null, RPCException.JSONRPCError.METHOD_NOT_FOUND);
		if (errorStyle.equals("params"))
			error = RPCBuilder.RPCResponseFactory(_id, false, null, RPCException.JSONRPCError.INVALID_PARAMS);
		if (errorStyle.equals("internal")) 
			error = RPCBuilder.RPCResponseFactory(_id, false, null, RPCException.JSONRPCError.INTERNAL_ERROR);
		if (error == null)
			error = RPCBuilder.RPCResponseFactory(_id, false, null, RPCException.JSONRPCError.NODE_SHAT);
		return error;
	}

	/**
	 * <p>
	 * Checks for semantics, syntax and if the RPC is supported by this system.
	 *
	 * @param call
	 * @return String detailing whether the JSONObject is valid or not.
	 */
	public String isValid(String call) {
		JSONObject jCall;
		try {
			jCall = new JSONObject(call);

		} catch (JSONException e) {
			DBP.printException(e);
			return "parse";

		}
		JSONObject params = new JSONObject();
		String methodName = "";
		if (!jCall.has("method") || !jCall.has("params")
				|| !jCall.has("jsonrpc")) {
			return "invalid";
		} else {
			methodName = jCall.getString("method");
			params = jCall.getJSONObject("params");
		}
		if (!jCall.getString("jsonrpc").equals("2.0"))
			return "invalid";
		if (!params.has("sourceO") || !params.has("sourceIP")
				|| !params.has("sourcePort") || !params.has("destinationO")
				|| !params.has("extensions")) {
			return "params";
		}
		if (methodName.equalsIgnoreCase("PUT") && !params.has("value")) {
			return "params";
		}
		if (methodName.equalsIgnoreCase("GET") && !params.has("index")) {
			return "params";
		}
		if (methodName.equalsIgnoreCase("SHUTDOWN")
				&& !params.has("sourcePort")) {
			return "params";
		}
		if (!methodName.equalsIgnoreCase("PING")
				&& !methodName.equalsIgnoreCase("PONG")
				&& !methodName.equalsIgnoreCase("LOOKUP")
				&& !methodName.equalsIgnoreCase("PUT")
				&& !methodName.equalsIgnoreCase("GET")
				&& !methodName.equalsIgnoreCase("SHUTDOWN")) {
			return "method";
		}
		// Check for validity of params
		// Not really sure how to do this with overlay addresses yet lawl
		String ip = params.getString("sourceIP");
		int port = params.getInt("sourcePort");
		final String PATTERN = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(ip);
		if (!matcher.matches()) {
			return "params";
		}
		if (port < 0 || port > 61001) {
			return "params";
		}

		return "valid";
	}

}
