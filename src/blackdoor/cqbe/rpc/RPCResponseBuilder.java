package blackdoor.cqbe.rpc;

import org.json.JSONArray;
import org.json.JSONObject;

import blackdoor.cqbe.rpc.RPCException.RequiredParametersNotSetException;

public class RPCResponseBuilder {
	
	JSONArray result;
	
	int id;
	
	public RPCResponseBuilder(int index) {
		this.id = index;
		result = null;
	}
	
	public JSONObject buildGETResponse() throws RequiredParametersNotSetException {
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
	
	public JSONObject buildPUTResponse() throws RequiredParametersNotSetException {
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
	
	public JSONObject buildLOOKUPResponse() throws RequiredParametersNotSetException {
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

	public JSONObject buildPINGResponse() throws RequiredParametersNotSetException {
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

	public JSONObject buildSHUTDOWNResponse() throws RequiredParametersNotSetException {
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

	public JSONArray getResult() {
		return result;
	}

	public void setResult(JSONArray result) {
		this.result = result;
	}
}