package blackdoor.cqbe.node.server;

import java.io.IOException;
import java.net.Socket;

import blackdoor.cqbe.rpc.RpcResponse;
import blackdoor.net.SocketIOWrapper;
import blackdoor.util.DBP;

/**
 * Handles a connection to a node's server socket to handle a RPC.
 *<p>
 * 
 * @author Cj Buresch
 * @version v1.0.0 - May 5, 2015
 */
public class AcceptedRPC implements Runnable {
	private SocketIOWrapper io;

	/**
	 * Creates an Object to handle an Accepted RPC at the server socket.
	 * <p>
	 * 
	 * @param socket
	 * @throws IOException
	 */
	public AcceptedRPC(Socket socket) throws IOException {
		socket.setSoTimeout(Server.TIMEOUT * 1000);
		io = new SocketIOWrapper(socket);
		io.setMaxReadSize(712000000);
	}

	/**
	 * 
	 */
	@Override
	public void run() {
		RPCHandler rv = new RPCHandler(io);
		RpcResponse result;
		try {
			String request = read();
			result = rv.handle(request);
			write(result);
			io.close();
		} catch (IOException e) {
			DBP.printerror("Problem closing socket...");
		}
	}

	/**
	 * Reads the RPC request response to the sender.
	 * <p>
	 * 
	 * @return request RPC string If there were errors with reading from the
	 *         stream, this
	 */
	private String read() {
		String input;
		try {
			input = io.read();
		} catch (IOException e) {
			DBP.printerror("Problem reading from Socket...");
			DBP.printException(e);
			return null;
		}
		return input;
	}

	/**
	 * Writes the RPC request response to the sender.
	 * <p>
	 * 
	 * @param result
	 *            If result is null there must have been errors with shutting
	 *            down or in handling another request. Ideally this would not be
	 *            error and would instead have other error data to send back.
	 */
	private void write(RpcResponse result) {
		try {
			if (result != null)
				io.write(result.toJSONString());
			else
				DBP.printerrorln("result to be send back to client is null object");
		} catch (IOException e) {
			DBP.printerror("Problem writing to Socket...");
			DBP.printException(e);
		}
	}
}
