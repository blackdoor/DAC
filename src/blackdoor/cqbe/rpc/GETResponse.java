package blackdoor.cqbe.rpc;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.rpc.RPCException.JSONRPCError;
import blackdoor.util.DBP;

public interface GETResponse {
	public Object getResult();
	
	public static class GETResponseFactory{
		public static GETResponse multipleReturnTypeDerp(JSONObject responseObject) throws RPCException{
			if(!RPCValidator.isValidoopResponse(responseObject))
				throw new RPCException(JSONRPCError.NODE_SHAT);
			String b64Value;
			JSONArray indexOrLookup;
			b64Value = responseObject.optString("result", null);
			if(b64Value != null && b64Value.charAt(0) != '['){ //response contained a value!
				GETValueResponse response = new GETValueResponse();
				try {
					response.result = Base64.decode(b64Value);
				} catch (Base64DecodingException e) {
					JSONRPCError x = JSONRPCError.INVALID_BASE64;
					x.serverSideError = true;
					throw new RPCException(x);
				}
				return response;
			}else{ // response contained an index or an address table
				indexOrLookup = responseObject.optJSONArray("result");
				if(indexOrLookup != null){
					JSONObject index = indexOrLookup.optJSONObject(0);
					if(index == null){ // response contained an index
						GETIndexResponse response = new GETIndexResponse();
						ArrayList<Address> result = new ArrayList<>(indexOrLookup.length());
						for(int i = 0; i < indexOrLookup.length(); i++){
							try {
								DBP.printdebugln(indexOrLookup);
								result.add(new Address(indexOrLookup.getString(i)));
							} catch (JSONException e) {
								DBP.printException(e);
							} catch (AddressException e) {
								JSONRPCError x = JSONRPCError.INVALID_ADDRESS_FORMAT;
								x.serverSideError = true;
								throw new RPCException(x);
							}
						}
						response.result = result;
						return response;
					}else{//response contained an address table
						try{
							AddressTable result = AddressTable.fromJSONArray(indexOrLookup);
							GETLookupResponse response = new GETLookupResponse();
							response.result = result;
							return response;
						}catch (JSONException e){
							throw new RPCException(JSONRPCError.INVALID_RESULT);
						}
					}
				}else throw new RPCException(JSONRPCError.INVALID_RESULT);
			}
		}
	}
	
	public static class GETIndexResponse implements GETResponse{
		private List<Address> result;
		@Override
		public List<Address> getResult() {
			return result;
		}
	}
	public static class GETValueResponse implements GETResponse{
		private byte[] result;
		@Override
		public byte[] getResult() {
			return result;
		}
	}
	public static class GETLookupResponse implements GETResponse{
		private AddressTable result;
		@Override
		public AddressTable getResult() {
			return result;
		}
	}
}
