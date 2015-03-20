package blackdoor.cqbe.rpc;

import org.json.JSONObject;

import blackdoor.cqbe.rpc.RPCException.JSONRPCError;

public class ErrorRpcResponse extends RpcResponse {
	
	public static ErrorRpcResponse fromJson(JSONObject response){
		JSONRPCError err;
		Object errorData;
		int id;
		JSONObject error = response.getJSONObject("error");
		err = JSONRPCError.fromJSON(error);
		if(error.has("data")){
			errorData = error.get("data");
		}else{
			errorData = null;
		}
		if(response.has("id")){
			id = response.optInt("response", -1);
		}else{
			id = -1;
		}
		return new ErrorRpcResponse(id, err, errorData);
	}
	
	private Object errorData;
	private RPCException.JSONRPCError error;
	
	public ErrorRpcResponse(int id, RPCException.JSONRPCError error, Object data){
		super(id, false);
		this.error = error;
		this.errorData = data;
	}
	
	public ErrorRpcResponse(Rpc request, RPCException.JSONRPCError error, Object data){
		this(request.getId(), error, data);
	}
	
	public ErrorRpcResponse(Rpc request, RPCException.JSONRPCError error){
		this(request.getId(), error, null);
	}
	
	public ErrorRpcResponse(RPCException.JSONRPCError error, Object data){
		this(-1, error, data);
	}
	
	public ErrorRpcResponse(RPCException.JSONRPCError error){
		this(-1, error, null);
	}
	
	protected void setError(JSONRPCError error){
		this.error = error;
	}
	
	public RPCException.JSONRPCError getError(){
		return error;
	}
	
	protected void setErrorData(Object data){
		this.errorData = data;
	}
	
	public Object getErrorData(){
		return errorData;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject response = new JSONObject();
		response.put("jsonrpc", "2.0");
		if(getId() == -1){
			response.put("id", JSONObject.NULL);
		}else {
			response.put("id", getId());
		}
		JSONObject errorObject = new JSONObject();
		errorObject.put("code", error.getCode());
		errorObject.put("message", error.getMessage());
		if(errorData != null)
			errorObject.put("data", errorData);
		response.put("error", errorObject);
		return response;
	}

}
