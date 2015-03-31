package blackdoor.cqbe.node.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.json.JSONObject;

import blackdoor.cqbe.rpc.RPCValidator;
import blackdoor.cqbe.rpc.RpcResponse;
import blackdoor.net.SocketIOWrapper;
import blackdoor.util.DBP;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.2 - Nov 17, 2014
 */
public class AcceptedRPC implements Runnable {
	private Socket socket = null;
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
	 * @param result
	 */
	private void write(RpcResponse result) {
		try {
			if (result != null)
				io.write(result);
		} catch (IOException e) {
			DBP.printerror("Problem writing to Socket...");
			DBP.printException(e);
		}
	}
}
