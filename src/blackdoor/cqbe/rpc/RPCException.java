package blackdoor.cqbe.rpc;
public class RPCException extends Exception {
	RPCException.JSONRPCError e;
	public RPCException(RPCException.JSONRPCError e){
		this.e = e;
	}
	public RPCException.JSONRPCError getRPCError(){
		return e;
	}

	public static class RPCCreationException extends RuntimeException{
		public RPCCreationException(String message){
			super(message);
		}
	}

	/**
	 * An enumeration of all the possible JSON RPC error objects supported by this system.
	 * Contains both the error code and the error message associated with that code.
	 */
	public static enum JSONRPCError{
	
		/**
		 * Invalid JSON was received by the server.
		 * An error occurred on the server while parsing the JSON text.
		 */
		PARSE_ERROR(-32700, "Parse error"),
		/**
		 * The JSON sent is not a valid Request object.
		 */
		INVALID_REQUEST(-32600, "Invalid Request"),
		/**
		 * The method does not exist / is not available.
		 */
		METHOD_NOT_FOUND(-32601,"Method not found"),
		/**
		 * Invalid method parameter(s).
		 */
		INVALID_PARAMS(-32602,"Invalid params"),
		/**
		 * Internal JSON-RPC error.
		 */
		INTERNAL_ERROR(-32603, "Internal error"),
		/**
		 * When logic goes AWOL, things are worse than SNAFU, and life is FUBAR.
		 */
		NODE_SHAT(-32099,"Node has shat itself"),
		INVALID_ADDRESS_FORMAT(-32001, "Invalid Address Format");
	
		private final int code;
		private final String message;
	
		JSONRPCError(int code, String message){
			this.code = code;
			this.message = message;
		}
	
		public int getCode(){
			return code;
		}
	
		public String getMessage(){
			return message;
		}
	
	
	}

}