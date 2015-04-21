package blackdoor.cqbe.headcount;

import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import blackdoor.cqbe.addressing.L3Address;

public class Ping {
	private L3Address l3 = null;

	public Ping(String json) throws UnknownHostException, JSONException {
		this.setL3(L3Address.fromJSON(new JSONObject(json)));
	}

	public String toJSON() {
		return getL3().toJSONString();
	}

	public L3Address getL3() {
		return l3;
	}

	public void setL3(L3Address l3) {
		this.l3 = l3;
	}
}
