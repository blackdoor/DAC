package blackdoor.cqbe.rpc;

import org.json.JSONObject;

public class PongResult extends JSONRPCResult {

	public PongResult() {
		super(ResultType.PONG);
	}

	@Override
	public String getValue() {
		return "pong";
	}

	@Override
	public JSONObject toJSON() {
		JSONObject response = shell();
		response.put("result", "pong");
		return response;
	}

}
