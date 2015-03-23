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
	private SocketIOWrapper io = null;

	public RPCValidator() {

	}

	public RPCValidator(SocketIOWrapper io) {
		this.io = io;
	}

	public RpcResponse handle(String rpcRequest) {
		RpcResponse response = null;
		try {
			Rpc request = Rpc.fromJsonString(rpcRequest);
			if (isValid(request)) {
				RPCHandler handler = new RPCHandler(io);
				response = handler.handle(request);

			} else {
				response = buildError(request);
			}
		} catch (RPCException e) {
			// problem putting the rpc into the rpc object --
			// so a parsing problem probably....

			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	public RpcResponse buildError(Rpc erpc) {
		return null;
	}

	/**
	 * <p>
	 * Checks for semantics, syntax and if the RPC is supported by this system.
	 *
	 * @param String
	 * @return String detailing whether the JSONObject is valid or not.
	 */
	public boolean isValid(Rpc request) {
		Rpc
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
		return true;
	}

	public boolean hasValidAddress(Rpc request) {
		return true;
	}

	public boolean hasValidSourceport(Rpc request) {
		int port = request.getSource().getPort();
		if (port < 0 || port > 61001) {
			return false;
		}
		return true;
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
					|| !response.has("id")) {
				// TODO maybe add checking for value of id
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
