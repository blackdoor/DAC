package blackdoor.cqbe.rpc;

import org.json.JSONObject;

import com.sun.org.apache.xml.internal.security.utils.Base64;

public class PutRpc extends Rpc {
	
	protected byte[] value;

	protected PutRpc() {
		super(Method.PUT);
	}
	
	public byte[] getValue(){
		return value;
	}
	
	protected void setValue(byte[] value){
		this.value = value;
	}
	
	public String getBase64Value(){
		return Base64.encode(value);
	}

	@Override
	public JSONObject toJSON() {
		JSONObject rpc = super.getRpcOuterShell();
		JSONObject params = super.getRpcParameterShell();
		params.put("value", this.getBase64Value());
		rpc.put("params", params);
		return rpc;
	}

}