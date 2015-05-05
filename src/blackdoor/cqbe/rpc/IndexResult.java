package blackdoor.cqbe.rpc;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.rpc.RPCException.JSONRPCError;

/**
 * 
 * @author Nathaniel Fischer
 * @version v1.0.0 - May 4, 2015
 */
public class IndexResult extends JSONRPCResult {

	/**
	 * @param result
	 * @return
	 * @throws RPCException
	 */
	public static IndexResult fromJSON(JSONObject result) throws RPCException {
		try {
			return new IndexResult(ugh(result.getJSONArray("result")));
		} catch (JSONException e) {
			throw new RPCException(JSONRPCError.PARSE_ERROR);
		} catch (AddressException e) {
			throw new RPCException(JSONRPCError.INVALID_ADDRESS_FORMAT);
		}
	}

	/**
	 * @param arr
	 * @return
	 * @throws AddressException
	 */
	private static Set<Address> ugh(JSONArray arr) throws AddressException {
		HashSet<Address> out = new HashSet<Address>();
		for (int i = 0; i < arr.length(); i++) {
			out.add(new Address(arr.getString(i)));
		}
		return out;
	}

	private Set<Address> index;

	/**
	 * @param addresses
	 */
	public IndexResult(Set<Address> addresses) {
		super(ResultType.INDEX);
		this.index = addresses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blackdoor.cqbe.rpc.JSONRPCResult#getValue()
	 */
	@Override
	public Set<Address> getValue() {
		return index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blackdoor.cqbe.rpc.JSONRPCResult#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		JSONObject result = shell();
		JSONArray array = new JSONArray();
		for (Address a : index) {
			array.put(a.overlayAddressToString());
		}
		result.put("result", array);
		return result;
	}

}
