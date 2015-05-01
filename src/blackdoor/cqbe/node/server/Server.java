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
 * 
 * @author Cj Buresch
 * @version v0.1.1 - Feb 22, 2015
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
	static{
		TIMEOUT =  (int) Config.getReadOnly("node_timeout_in_seconds","default.config");
		SERVER_PARALLELISM = (int) Config.getReadOnly("server_parallelism","default.config");
	}
	private final int QUEUE_SIZE = (int) Config.getReadOnly("max_server_queue_size","default.config");

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
		int size = QUEUE_SIZE * Math.max(cpus * SERVER_PARALLELISM,AddressTable.DEFAULT_MAX_SIZE * cpus);
		blockingQueue = new LinkedBlockingQueue<>(size);
		pool = getPool();
		openServerSocket();
	}

	/**
	 * Starts the node server.
	 * <p>
	 * 
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
	 * 
	 */
	public synchronized void stop() {
		running = false;
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(TIMEOUT, TimeUnit.SECONDS))
				pool.shutdownNow(); // Cancel currently executing tasks
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
		}finally{
			pool.shutdownNow();
		}
	}

	/**
	 * Returns the boolean status of the node server.
	 * <p>
	 * 
	 * @return
	 */
	public synchronized boolean isRunning() {
		return this.running;
	}

	/**
 * 
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
		int core = Math.min(SERVER_PARALLELISM * cpus, AddressTable.DEFAULT_MAX_SIZE);
		int max = Math.max(cpus * SERVER_PARALLELISM, AddressTable.DEFAULT_MAX_SIZE * cpus);
		TimeUnit time = TimeUnit.SECONDS;
		DBP.printdevln("Starting with " +core+ "<=threads<="+max);
		ThreadPoolExecutor tmp = new ThreadPoolExecutor(core, max, TIMEOUT,
				time, blockingQueue);
		return tmp;
	}

}
