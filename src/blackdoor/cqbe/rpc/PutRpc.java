package blackdoor.cqbe.rpc;

import org.json.JSONObject;

import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 * @author Nathaniel Fischer
 * @version v1.0.0 - May 4, 2015
 */
public class PutRpc extends Rpc {

	protected byte[] value;

	protected PutRpc() {
		super(Method.PUT);
	}

	/**
	 * @return
	 */
	public byte[] getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	protected void setValue(byte[] value) {
		this.value = value;
	}

	/**
	 * @return
	 */
	public String getBase64Value() {
		return Base64.encode(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blackdoor.cqbe.rpc.Rpc#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		JSONObject rpc = super.getRpcOuterShell();
		JSONObject params = super.getRpcParameterShell();
		params.put("value", this.getBase64Value());
		rpc.put("params", params);
		return rpc;
	}

}