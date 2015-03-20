package blackdoor.cqbe.rpc;

import org.json.JSONObject;

import blackdoor.cqbe.addressing.AddressTable;

public class TableResult extends JSONRPCResult {
	
	public static TableResult fromJSON(JSONObject json){
		return new TableResult(AddressTable.fromJSONArray(json.getJSONArray("result")));
	}
	
	private AddressTable table;

	protected TableResult(AddressTable table) {
		super(ResultType.AT);
		this.table = table;
	}

	@Override
	public AddressTable getValue() {
		return table;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject result = shell();
		result.put("result", table.toJSONArray());
		return result;
	}

}
