package blackdoor.cqbe.rpc;

import java.net.InetAddress;

import org.json.JSONObject;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.L3Address;

public class ShutdownRpc extends Rpc {
	
	public static final String CHALLENGE = "CHALLENGE!";
	public static final String HANDSHAKE = "HANDSHAKE!";

	protected ShutdownRpc(){
		super(Method.SHUTDOWN);
		super.setDestination(Address.getNullAddress());
		super.setSource(new L3Address(InetAddress.getLoopbackAddress(), 0));
	}
	
	protected ShutdownRpc(String json) throws RPCException {
		this();
		JSONObject jsObj = new JSONObject(json);
		populateCommonFields(this, jsObj);
	}
	
	public static ShutdownRpc getShutdownRPC() {
		return new ShutdownRpc();
	}

	@Override
	public JSONObject toJSON() {
		JSONObject rpc = getRpcOuterShell();
		rpc.put("params", getRpcParameterShell());
		return rpc;
	}

}
