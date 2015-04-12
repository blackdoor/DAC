package blackdoor.cqbe.headcount;

import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import blackdoor.cqbe.addressing.L3Address;

public class Ping {
	L3Address l3;
	public Ping(String json) throws UnknownHostException, JSONException{
		this.l3 = L3Address.fromJSON(new JSONObject(json));
	}
	
	public String toJSON(){
		return l3.toJSONString();
	}
}
