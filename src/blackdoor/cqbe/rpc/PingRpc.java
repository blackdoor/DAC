package blackdoor.cqbe.rpc;

import org.json.JSONObject;

/**
 * @author Nathaniel Fischer
 * @version v1.0.0 - May 4, 2015
 */
public class PingRpc extends Rpc {

	protected PingRpc() {
		super(Method.PING);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blackdoor.cqbe.rpc.Rpc#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		JSONObject rpc = getRpcOuterShell();
		rpc.put("params", getRpcParameterShell());
		return rpc;
	}

}
