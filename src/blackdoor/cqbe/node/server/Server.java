package blackdoor.cqbe.node.server;

import java.io.IOException;
import java.net.ServerSocket;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.node.Node;
import blackdoor.cqbe.node.Updater;
import blackdoor.cqbe.node.server.ServerException.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import blackdoor.cqbe.settings.Config;
import blackdoor.util.DBP;

/**
 * Node server that accepts and handles RPCs.
 * <p>
 * A Node server must be constructed with a port otherwise it will use the
 * default port that is specified in the configuration file "default.config". If
 * there is a problem with the configuration file, or when initializing the
 * server socket, a server exception will be thrown.
 * <p>
 * In the event that a problem has occurred with setting up a node's server it
 * is recommended that the error is printed to log or to the user and the
 * program's execution is terminated.
 * <p>
 * The static variables "SERVER_PARALLESIM" , "TIMEOUT" and "QUEUE_SIZE" are
 * used to tune the performance of the threadpool.
 * <p>
 * To use this server elsewhere simply construct a server object with a valid
 * port number or ensure that the default.config file is available and properly
 * formated.
 * 
 * @author Cj Buresch
 * @version v1.0.0 - May 5, 2015
 */
public class Server implements Runnable {

	private int port;
	private ServerSocket serverSocket;
	private boolean running = false;
	private ThreadPoolExecutor pool;
	private BlockingQueue<Runnable> blockingQueue;
	private Thread runningThread = null;
	private static final int SERVER_PARALLELISM;
	public static final int TIMEOUT;
	static {
		TIMEOUT = (int) Config.getReadOnly("node_timeout_in_seconds",
				"default.config");
		SERVER_PARALLELISM = (int) Config.getReadOnly("server_parallelism",
				"default.config");
	}

	private final int QUEUE_SIZE = (int) Config.getReadOnly(
			"max_server_queue_size", "default.config");

	/**
	 * Initialize with default port.
	 * 
	 * @throws ServerException
	 */
	public Server() throws ServerException {
		this((int) Node.getInstance().getConfig().get("port"));
	}

	/**
	 * Initialize with specific port.
	 * 
	 * @param port
	 * @throws ServerException
	 */
	public Server(int port) throws ServerException {
		this.port = port;
		int cpus = Runtime.getRuntime().availableProcessors();
		int size = QUEUE_SIZE
				* Math.max(cpus * SERVER_PARALLELISM,
						AddressTable.DEFAULT_MAX_SIZE * cpus);
		blockingQueue = new LinkedBlockingQueue<>(size);
		pool = getPool();
		openServerSocket();
	}

	/**
	 * Starts the node server.
	 * <p>
	 * This starts a infinite loop for a server listening on a port. To end this
	 * loop call the stop() method.
	 */
	@Override
	public void run() {
		running = true;
		synchronized (this) {
			this.runningThread = Thread.currentThread();
		}
		while (this.isRunning()) {
			try {
				pool.execute(new AcceptedRPC(this.serverSocket.accept()));
			} catch (IOException e) {
				DBP.printerror("Could not accept socket connection!");
				DBP.printException(e);
			}
		}
	}

	/**
	 * Stops the running node server.
	 * <p>
	 * This method tries to safely handle each request without causing serious
	 * issues in execution by shutting down. It will stop accepting requests to
	 * the pool, and then shut the socket. If there are still requests being
	 * handled it will wait a certain "timeout" period before shutting down.
	 * Turns out that handling a threadpool shutdown is a little difficult.
	 */
	public synchronized void stop() {
		running = false;
		// Disable new tasks from being submitted
		pool.shutdown();
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(TIMEOUT, TimeUnit.SECONDS))
				// Cancel currently executing tasks
				pool.shutdownNow();
			if (!serverSocket.isClosed())
				this.serverSocket.close();
		} catch (IOException e) {
			DBP.printerror("Error closing Socket.");
			DBP.printException(e);
		} catch (InterruptedException ie) {
			DBP.printerror("Error shutting down threadpool.");
			DBP.printException(ie);
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		} finally {
			pool.shutdownNow();
		}
	}

	/**
	 * Returns the boolean status of the server.
	 * <p>
	 * 
	 * @return boolean
	 */
	public synchronized boolean isRunning() {
		return this.running;
	}

	/**
	 * Opens a Server Socket on the port passed to the constructor.
	 * <p>
	 * If the server cannot be opened then a ServerExcetiion will be thrown
	 * back. Wherever this exception is caught should print the error to log or
	 * user and then exit execution.
	 * 
	 * @throws ServerException
	 */
	private void openServerSocket() throws ServerException {
		try {
			this.serverSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			running = false;
			DBP.printerror("COULD NOT OPEN SERVERSOCKET on port: " + port);
			throw new ServerSocketException(
					"COULD NOT OPEN SERVERSOCKET on port: " + port);
		}
	}

	/**
	 * 
	 * @return
	 */
	private ThreadPoolExecutor getPool() {
		int cpus = Runtime.getRuntime().availableProcessors();
		DBP.printdevln("Server Detects " + cpus + " cores.");
		int core = Math.min(SERVER_PARALLELISM * cpus,
				AddressTable.DEFAULT_MAX_SIZE);
		int max = Math.max(cpus * SERVER_PARALLELISM,
				AddressTable.DEFAULT_MAX_SIZE * cpus);
		TimeUnit time = TimeUnit.SECONDS;
		DBP.printdevln("Starting with " + core + "<=threads<=" + max);
		ThreadPoolExecutor tmp = new ThreadPoolExecutor(core, max, TIMEOUT,
				time, blockingQueue);
		return tmp;
	}

}
