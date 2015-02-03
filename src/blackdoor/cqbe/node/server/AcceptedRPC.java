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
	private SocketIOWrapper io;

	public AcceptedRPC(Socket socket) throws IOException {
		this.socket = socket;
		io = new SocketIOWrapper(socket);
	}

	@Override
	public void run() {
		try {
			String input = io.read();
			io.getSocket().getInputStream().close();
			RPCValidator rv = new RPCValidator(input);
			io.write(rv.handle());
		} catch (IOException e) {
			DBP.printerror("Problem with SocketIO in AcceptedRPC");
			DBP.printException(e);
		}
	}


}
