package blackdoor.cqbe.rpc;

import org.json.JSONArray;
import org.json.JSONObject;

import blackdoor.cqbe.rpc.RPCException.RequieredParametersNotSet;

public class RPCResponseBuilder {
	
	JSONArray result;
	
	int id;
	
	public RPCResponseBuilder(int index) {
		this.id = index;
		result = null;
	}
	
	public JSONObject buildGETResponse() throws RequieredParametersNotSet {
		if (result != null){
			JSONObject responceRPC = new JSONObject();
			responceRPC.put("jsonrpc", "2.0");
			responceRPC.put("result", result);
			responceRPC.put("id", id);
			return responceRPC;
		}
		else
			throw new RequieredParametersNotSet();
	}
	
	public JSONObject buildPUTResponse() throws RequieredParametersNotSet {
		if (result != null){
			JSONObject responceRPC = new JSONObject();
			responceRPC.put("jsonrpc", "2.0");
			responceRPC.put("result", result);
			responceRPC.put("id", id);
			return responceRPC;
		}
		else
			throw new RequieredParametersNotSet();
	}
	
	public JSONObject buildLOOKUPResponse() throws RequieredParametersNotSet {
		if (result != null){
			JSONObject responceRPC = new JSONObject();
			responceRPC.put("jsonrpc", "2.0");
			responceRPC.put("result", result);
			responceRPC.put("id", id);
			return responceRPC;
		}
		else
			throw new RequieredParametersNotSet();
	}

	public JSONObject buildPINGResponse() throws RequieredParametersNotSet {
		if (result != null){
			JSONObject responceRPC = new JSONObject();
			responceRPC.put("jsonrpc", "2.0");
			responceRPC.put("result", result);
			responceRPC.put("id", id);
			return responceRPC;
		}
		else
			throw new RequieredParametersNotSet();
	}

	public JSONObject buildSHUTDOWNResponse() throws RequieredParametersNotSet {
		if (result != null){
			JSONObject responceRPC = new JSONObject();
			responceRPC.put("jsonrpc", "2.0");
			responceRPC.put("result", result);
			responceRPC.put("id", id);
			return responceRPC;
		}
		else
			throw new RequieredParametersNotSet();
	}

	public JSONArray getResult() {
		return result;
	}

	public void setResult(JSONArray result) {
		this.result = result;
	}
}