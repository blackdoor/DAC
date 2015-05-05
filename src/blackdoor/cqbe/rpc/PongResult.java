package blackdoor.cqbe.rpc;

import org.json.JSONObject;

/**
 * @author Nathaniel Fischer
 * @version v1.0.0 - May 4, 2015
 */
public class PongResult extends JSONRPCResult {

	public PongResult() {
		super(ResultType.PONG);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blackdoor.cqbe.rpc.JSONRPCResult#getValue()
	 */
	@Override
	public String getValue() {
		return "pong";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blackdoor.cqbe.rpc.JSONRPCResult#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		JSONObject response = shell();
		response.put("result", "pong");
		return response;
	}

}
