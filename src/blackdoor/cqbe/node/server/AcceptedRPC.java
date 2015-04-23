package blackdoor.cqbe.node.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Set;

import blackdoor.cqbe.headcount.Entry;
import blackdoor.cqbe.headcount.HeadcountServerThread;
import blackdoor.cqbe.rpc.RpcResponse;
import blackdoor.net.ServerThread;
import blackdoor.net.SocketIOWrapper;
import blackdoor.net.ServerThread.ServerThreadBuilder;
import blackdoor.util.DBP;

/**
 * 
 * @author Cj Buresch
 * @version v0.1.0 - April 23, 2015
 */
public class AcceptedRPC implements ServerThread {
	private SocketIOWrapper io;

	public AcceptedRPC(SocketIOWrapper socket) {
		this.io = socket;
	}

	/**
 * 
 */
	@Override
	public void run() {
		try {
			RPCHandler rv = new RPCHandler(io);
			RpcResponse result;
			result = rv.handle(read());
			write(result);
		} catch (IOException e) {
			DBP.printerror(" RPC HANDLE ERROR...");
			e.printStackTrace();
		} finally {
			close();
		}
	}

	private void close() {
		try {
			io.close();
		} catch (IOException e) {
			DBP.printerror("Problem closing RPC socket...");
			e.printStackTrace();
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
			DBP.printerror("Problem reading RPC from Socket.");
			DBP.printException(e);
			return null;
		}
		return input;
	}

	/**
	 * 
	 * @param result
	 * @throws IOException
	 */
	private void write(RpcResponse result) throws IOException {
		if (result != null) {
			io.write(result.toJSONString());
			return;
		} else
			DBP.printerrorln("Reponse to client is null object");

	}

	@Override
	public Socket getSocket() {
		return io.getSocket();
	}

	public static class AcceptedRPCServerThreadFactory implements
			ServerThreadBuilder {

		public AcceptedRPCServerThreadFactory() {

		}

		@Override
		public ServerThread build(Socket s) {
			SocketIOWrapper io;
			try {
				io = new SocketIOWrapper(s);
				io.setMaxReadSize(712000000);
				return new AcceptedRPC(io);
			} catch (IOException e) {
				DBP.printerror("Problem with socket connection!");
				DBP.printException(e);
			}
			return null;
		}

	}

}
