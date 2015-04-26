package blackdoor.cqbe.node.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.headcount.HeartbeatServerThread.HeartbeatServerThreadFactory;
import blackdoor.cqbe.node.Node;
import blackdoor.cqbe.node.Updater;
import blackdoor.cqbe.node.server.AcceptedRPC.AcceptedRPCServerThreadFactory;
import blackdoor.cqbe.node.server.ServerException.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import blackdoor.cqbe.settings.Config;
import blackdoor.net.Server;
import blackdoor.net.ServerThread;
import blackdoor.util.DBP;

/**
 * Node server that accepts and handles RPCs.
 * <p>
 * 
 * @author Cj Buresch
 * @version v0.1.1 - Feb 22, 2015
 */
public class NodeServer extends Server {

	private static final int SERVER_PARALLELISM;
	public static final int TIMEOUT;
	public static final int SERVERPORT;
	private static final int QUEUE_SIZE;

	static {
		TIMEOUT = (int) Config.getReadOnly("node_timeout_in_seconds",
				"default.config");
		SERVER_PARALLELISM = (int) Config.getReadOnly("server_parallelism",
				"default.config");
		SERVERPORT = (int) Config.getReadOnly("port", "default.config");
		QUEUE_SIZE = (int) Config.getReadOnly("max_server_queue_size",
				"default.config");

	}

	/**
	 * Initialize with default port.
	 * 
	 * @throws ServerException
	 */
	public NodeServer(int port, int core, int max, int pooltimeout)
			throws ServerException {
		super(new AcceptedRPCServerThreadFactory(), port, core, max,
				pooltimeout);
		openNodeServerSocket();
	}

	/**
	 * Initialize with default port.
	 * 
	 * @throws ServerException
	 */
	public NodeServer() throws ServerException {
		this(SERVERPORT, 15, SERVER_PARALLELISM, TIMEOUT);

	}

	/**
	 * Initialize with specific port.
	 * 
	 * @param port
	 * @throws ServerException
	 */
	public NodeServer(int port) throws ServerException {
		this(port, 15, 45, 10);
	}

	/**
	 * Starts the node server.
	 * <p>
	 * 
	 */
	@Override
	public void run() {
		DBP.printdevln("Server Detects " + cpus + " cores.");
		running = true;
		synchronized (this) {
			try {
				while (this.isRunning()) {

					Socket sock = serverSocket.accept();
					ServerThread thread = threadBuilder.build(sock);
					if (thread != null)
						pool.execute(thread);
				}
			} catch (IOException e) {
				DBP.printdevln("NodeServer coming down.");
			}
		}
	}

	/**
 * 
 */
	protected void openNodeServerSocket() throws ServerException {
		try {
			serverSocket = new ServerSocket(this.port);
			// serverSocket.setSoTimeout(TIMEOUT * 1000);
		} catch (IOException e) {
			throw new ServerSocketException(
					"COULD NOT OPEN SERVERSOCKET on port: " + port);
		}
	}

}
