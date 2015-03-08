package blackdoor.cqbe.rpc;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.rpc.RPCException.JSONRPCError;

public class IndexResult extends JSONRPCResult {
	
	public static IndexResult fromJSON(JSONObject result) throws RPCException{
		try {
			return new IndexResult(ugh(result.getJSONArray("result")));
		} catch (JSONException e) {
			throw new RPCException(JSONRPCError.PARSE_ERROR);
		} catch (AddressException e) {
			throw new RPCException(JSONRPCError.INVALID_ADDRESS_FORMAT);
		}
	}
	
	private static Set<Address> ugh(JSONArray arr) throws AddressException{
		HashSet<Address> out = new HashSet<Address>();
		for(int i = 0; i < arr.length(); i++){
			out.add(new Address(arr.getString(i)));
		}
		return out;
	}
	
	private Set<Address> index;

	protected IndexResult(Set<Address> addresses) {
		super(ResultType.INDEX);
		this.index = addresses;
	}

	@Override
	public Set<Address> getValue() {
		return index;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject result = shell();
		JSONArray array = new JSONArray();
		for(Address a : index){
			array.put(a.overlayAddressToString());
		}
		result.put("result", array);
		return result;
	}

}
