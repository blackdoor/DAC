package blackdoor.cqbe.rpc;

import org.json.JSONObject;

import blackdoor.cqbe.rpc.JSONRPCResult.ResultType;

public class NakResult extends JSONRPCResult {

	public NakResult() {
		super(ResultType.NAK);
	}

	@Override
	public Boolean getValue() {
		return false;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject response = shell();
		response.put("result", false);
		return response;
	}

}
