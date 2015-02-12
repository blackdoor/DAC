package blackdoor.cqbe.node.server;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 20, 2014
 */
public class ServerException extends Exception {

	public ServerException() {
		super();
	}

	public ServerException(String e) {
		super(e);
	}

	public static class ServerSocketException extends ServerException {
		public ServerSocketException() {
			super();
		}

		public ServerSocketException(String e) {
			super(e);
		}
	}
}
