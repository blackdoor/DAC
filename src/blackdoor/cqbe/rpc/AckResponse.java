package blackdoor.cqbe.rpc;

import org.json.JSONObject;

/**
 * An RPC response that confirms that the operation requested was completed
 * successfully, but contains no other information.
 * <p>
 * 
 * @author Nathaniel Fischer
 * @version v1.0.0 - May 4, 2015
 */
public class AckResponse extends JSONRPCResult {

	public AckResponse() {
		super(ResultType.ACK);
	}

	/**
	 * Returns a JSONObject value filled with this RPC's information.
	 * <p>
	 * 
	 * @see blackdoor.cqbe.rpc.JSONRPCResult#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		JSONObject response = shell();
		response.put("result", true);
		return response;
	}

	/**
	 * 
	 * @see blackdoor.cqbe.rpc.JSONRPCResult#getValue()
	 */
	@Override
	public Boolean getValue() {
		return true;
	}

}
