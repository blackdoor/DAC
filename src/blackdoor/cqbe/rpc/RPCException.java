package blackdoor.cqbe.rpc;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An Exception that can be thrown if a fatal RPC error has occurred. These exceptions should only be handled by the method that is prepared to send a JSON RPC Error response to the caller. 
 * @author nfischer3
 *
 */
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
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RPCException [e=" + e + " " + e.getCode() + " " + e.getMessage() +"]";
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
		/**
		 * The response contained a string representation of an overlay address that was not valid.
		 */
		INVALID_ADDRESS_FORMAT(-32001, "Invalid Address Format"),
		/**
		 * A member of the JSON object was expected to contain a Base64 string, but the actual string was not valid Base 64.
		 */
		INVALID_BASE64(-32002, "Invalid Base 64"),
		/**
		 * The result member of a JSON RPC response did not contain an acceptable type or value.
		 * This error should not be put into RPC requests.
		 */
		 INVALID_RESULT(-32003, "Invalid RPC result"),
		/**
		 * Unable to retriece the corresponding value from storage
		 */
		 NODE_STORAGE_ERROR(-32004, "Unable to retrieve this value from storage"),
		 /**
		  * A shutdown request was received, but it did not come over the loopback interface.
		  */
		 NON_LO_SHUTDOWN(-32005, "Shutdown requests must come from the loopback network interface"),
		 /**
		  * Response object from node was not a valid JSON-RPC 2.0 response.
		  * This error should never go into a response.
		  */
		 INVALID_RESPONSE(-32006, "Response object from node was not a valid JSON-RPC 2.0 response"),
		 /*
		  * Result type indicator was missing
		  * Were you trying to parse a response created by an older version using a newer parser? Older parsers are fowards compatible but new parsers are not backwards compatible.
		  * This error should never go into a response.
		  */
		 MISSING_RESULT_TYPE(-32007, "Result type indicator was missing");

		/**
		 * get a JSONRPCError java object from a JSONRPC error object
		 * @param error
		 * @return
		 * @throws JSONException
		 */
		public static JSONRPCError fromJSON(JSONObject error) throws JSONException{
			int code = error.getInt("code");
			for(JSONRPCError e : JSONRPCError.values()){
				if(e.code == code)
					return e;
			}
			throw new RuntimeException("unsupported JSONRPC error code");
		}
	
		private final int code;
		private final String message;
	

		/**
		 * true if this error was the result of a weird RPC response rather than an unsuccessful RPC request.
		 * This can happen if a valid RPC request is sent, but the server replies with a wonky response.
		 */
		public boolean serverSideError = false;

		JSONRPCError(int code, String message){
			if(code == -32003)
				serverSideError = true;
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