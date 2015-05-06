package blackdoor.cqbe.rpc;

import org.json.JSONObject;

/**
 * 
 * @author Nathaniel Fischer
 * @version v1.0.0 - May 4, 2015
 */
public class GetRpc extends Rpc {
	protected int index;

	public GetRpc() {
		super(Method.GET);
		setIndex(index);
	}

	/**
	 * @return
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blackdoor.cqbe.rpc.Rpc#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		JSONObject rpc = getRpcOuterShell();
		JSONObject params = super.getRpcParameterShell();
		params.put("index", index);
		rpc.put("params", params);
		return rpc;
	}

}
