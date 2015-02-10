package blackdoor.cqbe.rpc;

import org.json.JSONObject;

/**
 * An RPC response that confirms that the operation requested was completed successfully, but contains no other information.
 * @author nfischer3
 *
 */
public class AckResponse extends RpcResponse {

	public AckResponse(int id) {
		super(id, true);
	}

	@Override
	public JSONObject toJSON() {
		JSONObject response = super.getRpcOuterShell();
		response.put("result", true);
		return response;
	}

}
