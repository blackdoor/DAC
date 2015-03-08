package blackdoor.cqbe.rpc;

import org.json.JSONException;
import org.json.JSONObject;

import blackdoor.cqbe.rpc.RPCException.JSONRPCError;

public class ResultRpcResponse extends RpcResponse {
	
	public static ResultRpcResponse fromJson(JSONObject response) throws RPCException{
		try{
			JSONRPCResult result = JSONRPCResult.fromJSON(response.getJSONObject("result"));
			return new ResultRpcResponse(response.getInt("id"), result);
		}catch(JSONException e){
			throw new RPCException(JSONRPCError.INVALID_RESPONSE);
		}
	}
	
	private JSONRPCResult result;

	public ResultRpcResponse(int id, JSONRPCResult result) {
		super(id, true);
		this.result = result;
	}

	public JSONRPCResult getResult(){
		return result;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject shell = super.getRpcOuterShell();
		shell.put("result", result.toJSON());
		return shell;
	}
		

	
	
}
