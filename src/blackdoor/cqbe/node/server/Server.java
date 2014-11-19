package blackdoor.cqbe.node.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import blackdoor.cqbe.settings.Config;

/**
 * Node server that accepts and handles RPCs.
 * <p>
 * 
 * HOW TO START: Server server = new Server(); new Thread(server).start();
 * 
 * HOT TO STOP: server.stop();
 * 
 * @author Cj Buresch
 * @version v0.0.2 - Nov 17, 2014
 */
public class Server implements Runnable {

  private int port = Config.Port();
  private ServerSocket serverSocket;
  private boolean running = true;
  private ThreadPoolExecutor pool;
  private BlockingQueue<Runnable> blockingQueue;
  private Thread runningThread = null;

  public Server() {
    blockingQueue = new SynchronousQueue<Runnable>();
    pool = getPool();
  }

  public Server(int port) {
    this.port = port;
    blockingQueue = new SynchronousQueue<Runnable>();
    pool = getPool();
  }

  /**
   * Starts the node server.
   * <p>
   * 
   */
  @Override
  public void run() {
    synchronized (this) {
      this.runningThread = Thread.currentThread();
    }
    openServerSocket();
    while (this.isRunning()) {
      try {
        pool.execute(new AcceptedRPC(this.serverSocket.accept(), blockingQueue));
      } catch (IOException e) {
        if (!isRunning()) {
          System.out.println("Server Stopped.");
          return;
        }
        throw new RuntimeException("Error accepting client connection", e);
      }
    }
  }

  /**
   * Stops the running node server.
   * <p>
   * 
   */
  public synchronized void stop() {
    pool.shutdown(); // Disable new tasks from being submitted
    try {
      this.serverSocket.close();
      // Wait a while for existing tasks to terminate
      if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
        pool.shutdownNow(); // Cancel currently executing tasks
        // Wait a while for tasks to respond to being cancelled
        if (!pool.awaitTermination(60, TimeUnit.SECONDS))
          System.err.println("Pool did not terminate");
      }
    } catch (IOException e) {
      System.err.println("Error closing Socket. \n" + e);
    } catch (InterruptedException ie) {
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
  private synchronized boolean isRunning() {
    return this.running;
  }

  /**
 * 
 */
  private void openServerSocket() {
    try {
      this.serverSocket = new ServerSocket(this.port);
    } catch (IOException e) {
      throw new RuntimeException("Cannot open port" + this.port + e);
    }
  }

  /**
   * 
   * @return
   */
  private ThreadPoolExecutor getPool() {
    int cpus = Runtime.getRuntime().availableProcessors();
    int core = 5 * cpus;
    int max = 15 * cpus;
    int timeout = 60;
    TimeUnit time = TimeUnit.SECONDS;
    ThreadPoolExecutor tmp = new ThreadPoolExecutor(core, max, timeout, time, blockingQueue);
    return tmp;
  }

}
