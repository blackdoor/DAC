package blackdoor.cqbe.rpc;

import org.json.JSONObject;

public class LookupRpc extends Rpc {

	protected LookupRpc() {
		super(Method.LOOKUP);
	}

	@Override
	public JSONObject toJSON() {
		JSONObject rpc = getRpcOuterShell();
		rpc.put("params", getRpcParameterShell());
		return rpc;
	}

}
