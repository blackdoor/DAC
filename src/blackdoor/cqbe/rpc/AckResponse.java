package blackdoor.cqbe.rpc;

import org.json.JSONObject;

public class AckResponse extends RpcResponse {

	public AckResponse(int id) {
		super(id, true);
	}

	@Override
	public JSONObject toJSON() {
		JSONObject response = super.getRpcOuterShell();
		response.put("result", "ACK");
		return response;
	}

}
