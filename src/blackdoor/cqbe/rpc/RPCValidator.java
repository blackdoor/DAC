package blackdoor.cqbe.rpc;

import java.io.IOException;

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

	/**
	 * 
	 * @param rpcRequest
	 * @return
	 */
	public RpcResponse handle(String rpcRequest) {
		RpcResponse response = null;
		Rpc request = null;
		try {
			request = Rpc.fromJsonString(rpcRequest);
			if (isValid(request)) {
				RPCHandler handler = new RPCHandler(io);
				response = handler.handle(request);
			} else
				response = buildError(request);
		} catch (RPCException e) {
			// problems unpacking
			response = new ErrorRpcResponse(request, e.getRPCError());
		} catch (IOException e) {
			// TODO What do? shutdown had issues.....
		}
		return response;
	}

	public RpcResponse buildError(Rpc request) {
		// ErrorRpcResponse response =
		if (!hasValidAddress(request) || !hasValidSourceport(request))
			return new ErrorRpcResponse(request,
					RPCException.JSONRPCError.INVALID_ADDRESS_FORMAT);
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
		// TODO there are probably more things that make an RPC valid that arent
		// being checked here...
		if (!hasValidAddress(request))
			return false;
		if (!hasValidSourceport(request))
			return false;
		return true;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	public boolean hasValidAddress(Rpc request) {
		// TODO what makes a valid address????
		return true;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	public boolean hasValidSourceport(Rpc request) {
		// TODO hardcoded port values???
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
