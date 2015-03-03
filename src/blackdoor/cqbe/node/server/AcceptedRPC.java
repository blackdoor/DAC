package blackdoor.cqbe.node.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.json.JSONObject;

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
	}

	/**
 * 
 */
	@Override
	public void run() {
		String input = read();
		RPCValidator rv = new RPCValidator(input, io);
		write(rv.handle().toString());
		try {
			io.close();
		} catch (IOException e) {
			DBP.printerror("Problem closing socket...");
		}
	}

	/**
	 * 
	 * @return
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
	 * 
	 * @param output
	 */
	private void write(String output) {
		try {
			if(output != null)
				io.write(output);
		} catch (IOException e) {
			DBP.printerror("Problem writing to Socket...");
			DBP.printException(e);
		}
	}
}
