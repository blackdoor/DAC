package blackdoor.cqbe.rpc;

import org.json.JSONObject;

public class PingRpc extends Rpc {

	protected PingRpc() {
	}

	@Override
	public String toJSONString() {
		JSONObject rpc = getRpcOuterShell();
		rpc.put("params", getRpcParameterShell());
		return rpc.toString();
	}

}
