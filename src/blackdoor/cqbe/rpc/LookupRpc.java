package blackdoor.cqbe.rpc;

import org.json.JSONObject;

public class LookupRpc extends Rpc {

	protected LookupRpc() {
	}

	@Override
	public String toJSONString() {
		JSONObject rpc = getRpcOuterShell();
		rpc.put("params", getRpcParameterShell());
		return rpc.toString();
	}

}
