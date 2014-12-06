package blackdoor.cqbe.rpc;
public class RPCException extends Exception {
	RPCBuilder.JSONRPCError e;
	public RPCException(RPCBuilder.JSONRPCError e){
		this.e = e;
	}
	public RPCBuilder.JSONRPCError getRPCError(){
		return e;
	}

	public static class RPCCreationException extends RuntimeException{
		public RPCCreationException(String message){
			super(message);
		}
	}

}