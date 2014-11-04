package blackdoor.cqbe.rpc;

/**
 * 
 * @author Cj Buresch @ *
 */
public class RPC {

	public enum Command {
		PUT, GET, LOOKUP, PING
	}

	Command command;

	// VARIABLES

	public RPC(Command cmd) {
		command = cmd;
	}
	/*
	 * GETTERS and SETTERS
	 */
}
