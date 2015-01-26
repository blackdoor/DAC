package blackdoor.cqbe.rpc;

import org.json.JSONObject;

public class PingRpc extends Rpc {

	protected PingRpc() {
		super(Method.PING);
	}

	@Override
	public JSONObject toJSON() {
		JSONObject rpc = getRpcOuterShell();
		rpc.put("params", getRpcParameterShell());
		return rpc;
	}

}
