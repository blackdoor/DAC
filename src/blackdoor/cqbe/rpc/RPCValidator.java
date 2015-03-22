package blackdoor.cqbe.rpc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import blackdoor.net.SocketIOWrapper;
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
	private SocketIOWrapper io;

	public RPCValidator(String rpcCall, SocketIOWrapper io) {
		call = rpcCall;
		this.io = io;
	}

	public JSONObject handle() {
		String validity = isValid(call);
		JSONObject jo = null;
		try {
			if (validity.equals("valid")) {
				// Handle the call by passing off to the handler.
				// String methodCalled = call.getString("method");
				RPCHandler handler = new RPCHandler(new JSONObject(call), io);
				jo = handler.handle();
			} else {
				jo = buildError(
						validity,
						validity.equals("parse") ? -1 : new JSONObject(call)
								.getInt("id"));
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			DBP.printException(e1);
		}
		return jo;
	}

	public JSONObject buildError(String errorStyle, int id) {
		JSONObject error = null;
		Integer _id = id == -1 ? null : id;
		if (errorStyle.equals("parse"))
			error = RPCBuilder.RPCResponseFactory(_id, false, null,
					RPCException.JSONRPCError.PARSE_ERROR);
		if (errorStyle.equals("invalid"))
			error = RPCBuilder.RPCResponseFactory(_id, false, null,
					RPCException.JSONRPCError.INVALID_REQUEST);
		if (errorStyle.equals("method"))
			error = RPCBuilder.RPCResponseFactory(_id, false, null,
					RPCException.JSONRPCError.METHOD_NOT_FOUND);
		if (errorStyle.equals("params"))
			error = RPCBuilder.RPCResponseFactory(_id, false, null,
					RPCException.JSONRPCError.INVALID_PARAMS);
		if (errorStyle.equals("internal"))
			error = RPCBuilder.RPCResponseFactory(_id, false, null,
					RPCException.JSONRPCError.INTERNAL_ERROR);
		if (error == null)
			error = RPCBuilder.RPCResponseFactory(_id, false, null,
					RPCException.JSONRPCError.NODE_SHAT);
		return error;
	}

	/**
	 * <p>
	 * Checks for semantics, syntax and if the RPC is supported by this system.
	 *
	 * @param String
	 * @return String detailing whether the JSONObject is valid or not.
	 */
	public static String isValid(String call) {
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
		if (!params.has("sourceIP") || !params.has("sourcePort") || !params.has("destinationO")) {
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
		if(ip.equals("localhost"))
			return "valid";
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

	/**
	 * 
	 * @param response
	 * @return true if response is a valid successful JSONRPC response object,
	 *         false if it is not a valid JSON-RPC response object
	 * @throws RPCException
	 *             if response is a valid unsuccessful JSONRPC object, the error
	 *             from the response is thrown
	 */
	public static boolean isValidoopResponse(JSONObject response)
			throws RPCException {
		try {
			// check version and id
			if (!response.getString("jsonrpc").equals("2.0")
					|| !response.has("id")) {// TODO maybe add checking for
												// value of id
				return false;
			}
			if (response.has("result"))
				return true;
			if (response.has("error")) {
				JSONObject error = response.getJSONObject("error");
				if (!error.has("message"))
					return false;
				throw new RPCException(JSONRPCError.fromJSON(error));
			}
		} catch (JSONException e) {
			DBP.printException(e);
			return false;
		}
		return false;

	}

}
