package blackdoor.cqbe.node.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import blackdoor.cqbe.rpc.RPCValidator;
import blackdoor.net.SocketIOWrapper;
import blackdoor.util.DBP;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.2 - Nov 17, 2014
 */
public class AcceptedRPC implements Runnable {
	private final int BUFFER_SIZE = 64 * 1024;
	private Socket socket = null;
	private OutputStream out;
	private InputStream in;
	private SocketIOWrapper io;

	public AcceptedRPC(Socket socket) throws IOException {
		this.socket = socket;
		io = new SocketIOWrapper(socket);
		openInputStream();
		openOutputStream();
	}

	/**
 * 
 */
	@Override
	public void run() {
		String input = read();
		//shutdownSocketInput();
		RPCValidator rv = new RPCValidator(input, io);
		rv.handle();
		closeSocket();
	}

	/**
	 * 
	 * @returngit
	 */
	private String read() {
		byte[] buffer = new byte[BUFFER_SIZE];
		int count = 0;
		try {
			count = in.read(buffer);
		} catch (IOException e) {
			DBP.printerror("Problem reading from InputStream...");
			DBP.printException(e);
			return null;
		}
		return new String(buffer).substring(0, count);
	}

	/**
   * 
   */
	private void openInputStream() {
		try {
			in = socket.getInputStream();
		} catch (IOException e) {
			DBP.printerror("Problem opening InputStream...");
			DBP.printException(e);
		}
	}

	/**
 * 
 */
	private void openOutputStream() {
		try {
			out = socket.getOutputStream();
		} catch (IOException e) {
			DBP.printerror("Problem opening OutputStream...");
			DBP.printException(e);
		}
	}

	/**
 * 
 */
	private void shutdownSocketInput() {
		try {
			socket.shutdownInput();
		} catch (IOException e) {
			DBP.printerror("Problem shutting down InputStream...");
			DBP.printException(e);
		}
	}

	private void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			DBP.printerror("Problem shutting down InputStream...");
			DBP.printException(e);
		}
	}

}
