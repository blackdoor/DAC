package blackdoor.cqbe.rpc;

import org.json.JSONObject;

import blackdoor.cqbe.rpc.RPCException.JSONRPCError;

/**
 * @author Nathaniel Fischer
 * @version v1.0.0 - May 4, 2015
 */
public abstract class JSONRPCResult {

	public static JSONRPCResult fromJSON(JSONObject result) throws RPCException {
		ResultType type = ResultType.fromID(result.getString("type").charAt(0));
		switch (type) {
		case ACK:
			return new AckResponse();
		case AT:
			return TableResult.fromJSON(result);
		case INDEX:
			return IndexResult.fromJSON(result);
		case NAK:
			return new NakResult();
		case PONG:
			return new PongResult();
		case VALUE:
			return ValueResult.fromJSON(result);
		default:
			throw new RPCException(JSONRPCError.MISSING_RESULT_TYPE);
		}
	}

	protected ResultType type;

	/**
	 * @param type
	 */
	protected JSONRPCResult(ResultType type) {
		this.type = type;
	}

	/**
	 * @return
	 */
	public ResultType getType() {
		return type;
	}

	/**
	 * @return
	 */
	public abstract Object getValue();

	/**
	 * @return
	 */
	protected JSONObject shell() {
		JSONObject response = new JSONObject();
		response.put("type", "" + type.getID());
		return response;
	}

	/**
	 * @return
	 */
	public abstract JSONObject toJSON();

	/**
	 * @return
	 */
	public String toJSONString() {
		return toJSON().toString();
	}

	/**
	 * @author Nathaniel Fischer
	 * @version v1.0.0 - May 4, 2015
	 */
	public static enum ResultType {
		ACK('A'), NAK('N'), PONG('P'), AT('T'), INDEX('I'), VALUE('V');

		public static ResultType fromID(char id) throws RPCException {
			for (ResultType t : ResultType.values()) {
				if (id == t.id) {
					return t;
				}
			}
			throw new RPCException(JSONRPCError.MISSING_RESULT_TYPE);
		}

		private char id;

		/**
		 * @param id
		 */
		private ResultType(char id) {
			this.id = id;
		}

		/**
		 * @return
		 */
		public char getID() {
			return id;
		}

	}
}
