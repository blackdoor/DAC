package blackdoor.cqbe.rpc;

import org.json.JSONObject;

import blackdoor.cqbe.addressing.AddressTable;

/**
 * @author Nathaniel Fischer
 * @version v1.0.0 - May 4, 2015
 */
public class TableResult extends JSONRPCResult {

	public static TableResult fromJSON(JSONObject json) {
		return new TableResult(AddressTable.fromJSONArray(json
				.getJSONArray("result")));
	}

	private AddressTable table;

	/**
	 * @param table
	 */
	public TableResult(AddressTable table) {
		super(ResultType.AT);
		this.table = table;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blackdoor.cqbe.rpc.JSONRPCResult#getValue()
	 */
	@Override
	public AddressTable getValue() {
		return table;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blackdoor.cqbe.rpc.JSONRPCResult#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		JSONObject result = shell();
		result.put("result", table.toJSONArray());
		return result;
	}

}
