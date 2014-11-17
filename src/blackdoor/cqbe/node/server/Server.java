package blackdoor.cqbe.node.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import blackdoor.cqbe.settings.Config;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 12, 2014
 */
public class Server {
  private int port = Config.PORT;
  private ServerSocket serverSocket;
  private boolean running = true;

  public Server() {}

  public void start() {
    listen();
    acceptConnections();
  }

  /**
   * Starts the server.
   * <p>
   * 
   * @param port
   */
  public void listen() {
    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      // System.exit(-1);
    }
  }

  private void acceptConnections() {
    Socket socket = null;
    while (running) {
      try {
        socket = serverSocket.accept();
        System.out.println("connection accepted from " + socket.getRemoteSocketAddress());
      } catch (IOException e) {
        System.err.println("Unable to accept connection on port " + port);
        e.printStackTrace();
      }
      // AuthConnectionHandler handler = new AuthConnectionHandler(socket, authManager);
      // handler.start();
    }
  }

  /**
   * Pushes an RPC request to the RPC-Handler
   */
  public void pushRequest() {}

  /**
   * Accepts the response from the RPC-Handler, sends to RPC-Builder
   */
  public void sendToBuilder() {}

  /**
   * Listens for a returned RPC from builder.
   * Calls sendResponse() after.
   */
  public void receiveBuiltRPC() {}

  /**
   * Sends the built RPC given by RPC Builder
   */
  public void sendResponse() {

  }

}
