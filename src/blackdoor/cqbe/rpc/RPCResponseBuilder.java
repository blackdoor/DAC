package blackdoor.cqbe.rpc;

import org.json.JSONObject;

import blackdoor.cqbe.rpc.RPCException.RequiredParametersNotSetException;

public class RPCResponseBuilder {
	
	Object result;
	
	int id;
	
	public RPCResponseBuilder(int index) {
		this.id = index;
		result = null;
	}
	
	public JSONObject buildResponse() throws RequiredParametersNotSetException {
		if (result != null){
			JSONObject responceRPC = new JSONObject();
			responceRPC.put("jsonrpc", "2.0");
			responceRPC.put("result", result);
			responceRPC.put("id", id);
			return responceRPC;
		}
		else
			throw new RequiredParametersNotSetException();
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}