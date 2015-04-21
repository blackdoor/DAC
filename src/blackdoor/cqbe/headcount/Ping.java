package blackdoor.cqbe.headcount;

import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import blackdoor.cqbe.addressing.L3Address;

public class Ping {
	private L3Address l3 = null;

	public Ping(String json) throws UnknownHostException, JSONException {
		JSONObject obj = new JSONObject(json);
		L3Address tmp = L3Address.fromJSON(obj);
		l3 = tmp;

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
