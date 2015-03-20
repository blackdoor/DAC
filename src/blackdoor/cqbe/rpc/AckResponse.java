package blackdoor.cqbe.rpc;

import org.json.JSONObject;

/**
 * An RPC response that confirms that the operation requested was completed successfully, but contains no other information.
 * @author nfischer3
 *
 */
public class AckResponse extends JSONRPCResult{
	
	public AckResponse(){
		super(ResultType.ACK);
	}

	@Override
	public JSONObject toJSON() {
		JSONObject response = shell();
		response.put("result", true);
		return response;
	}

	@Override
	public Boolean getValue() {
		return true;
	}

}
