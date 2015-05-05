package blackdoor.cqbe.rpc;

import org.json.JSONException;
import org.json.JSONObject;

import blackdoor.cqbe.rpc.RPCException.JSONRPCError;

/**
 * @author Nathaniel Fischer
 * @version v1.0.0 - May 4, 2015
 */
public class ResultRpcResponse extends RpcResponse {

	/**
	 * @param response
	 * @return
	 * @throws RPCException
	 */
	public static ResultRpcResponse fromJson(JSONObject response)
			throws RPCException {
		try {
			JSONRPCResult result = JSONRPCResult.fromJSON(response
					.getJSONObject("result"));
			return new ResultRpcResponse(response.getInt("id"), result);
		} catch (JSONException e) {
			throw new RPCException(JSONRPCError.INVALID_RESPONSE);
		}
	}

	private JSONRPCResult result;

	/**
	 * @param id
	 * @param result
	 */
	public ResultRpcResponse(int id, JSONRPCResult result) {
		super(id, true);
		this.result = result;
	}

	/**
	 * @return
	 */
	public JSONRPCResult getResult() {
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blackdoor.cqbe.rpc.RpcResponse#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		JSONObject shell = super.getRpcOuterShell();
		shell.put("result", result.toJSON());
		return shell;
	}

}
