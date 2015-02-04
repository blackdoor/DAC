package blackdoor.cqbe.node.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
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
 * @version v0.1.0 - Nov 17, 2014
 */
public class Server implements Runnable {

  // private int port = Config.Port(); not yet working
  private int port;
  private ServerSocket serverSocket;
  private boolean running = false;
  private ThreadPoolExecutor pool;
  private BlockingQueue<Runnable> blockingQueue;
  private Thread runningThread = null;
  private final int QUEUE_SIZE = 256;//?


  /**
   * Initialize with specific port.
   * 
   * @param port
   */
  public Server(int port) {
    this.port = port;
    blockingQueue = new ArrayBlockingQueue<Runnable>(QUEUE_SIZE);
    pool = getPool();
  }

  /*
   * just for testing this... or example how to run
   */
  public static void main(String[] args) {
	DBP.DEV = true;
    Server server = new Server(1778);
    new Thread(server).start();
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
    openServerSocket();
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
      if (!pool.awaitTermination(60, TimeUnit.SECONDS))
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
  private void openServerSocket() {
    try {
      this.serverSocket = new ServerSocket(this.port);
    } catch (IOException e) {
      running = false;
      DBP.printerror("COULD NOT OPEN SERVERSOCKET on port: " + port);
      DBP.printException(e);
    }
  }

  /**
   * 
   * @return
   */
  private ThreadPoolExecutor getPool() {
    int cpus = Runtime.getRuntime().availableProcessors();
    DBP.printdevln("Server Detects " + cpus + " cores.");
    int core = 5 * cpus;
    int max = 15 * cpus;
    int timeout = 60;
    TimeUnit time = TimeUnit.SECONDS;
    ThreadPoolExecutor tmp = new ThreadPoolExecutor(core, max, timeout, time, blockingQueue);
    return tmp;
  }

}
