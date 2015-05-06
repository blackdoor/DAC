package blackdoor.cqbe.rpc;

import org.json.JSONObject;

import blackdoor.cqbe.rpc.JSONRPCResult.ResultType;

/**
 * * An RPC response that confirms that the operation requested was completed
 * unsuccessfully, but contains no other information.
 * <p>
 * 
 * @author Nathaniel Fischer
 * @version v1.0.0 - May 4, 2015
 */
public class NakResult extends JSONRPCResult {

	public NakResult() {
		super(ResultType.NAK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blackdoor.cqbe.rpc.JSONRPCResult#getValue()
	 */
	@Override
	public Boolean getValue() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blackdoor.cqbe.rpc.JSONRPCResult#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		JSONObject response = shell();
		response.put("result", false);
		return response;
	}

}
