package blackdoor.cqbe.rpc;

import org.json.JSONObject;

public class GetRpc extends Rpc{
	protected int index;

	public GetRpc() {
		super(Method.GET);
		setIndex(index);
	}
	
	public int getIndex(){
		return index;
	}
	
	public void setIndex(int index){
		this.index = index;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject rpc = getRpcOuterShell();
		JSONObject params = super.getRpcParameterShell();
		params.put("index", index);
		rpc.put("params", params);
		return rpc;
	}

}
