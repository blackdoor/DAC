package blackdoor.cqbe.rpc;

import org.json.JSONException;
import org.json.JSONObject;

import blackdoor.cqbe.rpc.RPCException.JSONRPCError;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 * @author Nathaniel Fischer
 * @version v1.0.0 - May 4, 2015
 */
public class ValueResult extends JSONRPCResult {

	public static ValueResult fromJSON(JSONObject result) throws RPCException {
		try {
			return new ValueResult(Base64.decode(result.getString("result")));
		} catch (Base64DecodingException e) {
			throw new RPCException(JSONRPCError.INVALID_BASE64);
		} catch (JSONException e) {
			throw new RPCException(JSONRPCError.INVALID_RESULT);
		}
	}

	private byte[] value;

	public ValueResult(byte[] value) {
		super(ResultType.VALUE);
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blackdoor.cqbe.rpc.JSONRPCResult#getValue()
	 */
	@Override
	public byte[] getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blackdoor.cqbe.rpc.JSONRPCResult#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		JSONObject result = shell();
		result.put("result", Base64.encode(value));
		return result;
	}

}
