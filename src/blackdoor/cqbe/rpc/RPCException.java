package blackdoor.cqbe.rpc;

public class RPCException extends Exception {
	/**
	 * Invalid JSON was received by the server.
	 * An error occurred on the server while parsing the JSON text.
	 * @author nfischer3
	 *
	 */
	public static class ParseException extends RPCException{
		
	}
	/**
	 * The JSON sent is not a valid Request object.
	 * @author nfischer3
	 *
	 */
	public static class InvalidRequestException extends RPCException{
		
	}
	/**
	 * The method does not exist / is not available.
	 * @author nfischer3
	 *
	 */
	public static class MethodNotFoundException extends RPCException{
		
	}
	/*
	 * Invalid method parameter(s).
	 */
	public static class InvalidParameterException extends RPCException{
		
	}
	/**
	 * Internal JSON-RPC error.
	 * @author nfischer3
	 *
	 */
	public static class InternalException extends RPCException{
		
	}
	/**
	 * Reserved for implementation-defined server-errors.
	 * @author nfischer3
	 *
	 */
	public static class ServerException extends RPCException{
		
	}

	public static class RequieredParametersNotSet extends RPCException{
		
	}
}