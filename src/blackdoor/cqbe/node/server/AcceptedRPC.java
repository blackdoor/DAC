package blackdoor.cqbe.node.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import blackdoor.cqbe.rpc.RPCValidator;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 17, 2014
 */
public class AcceptedRPC implements Runnable {
  private final int CAPACITY = 512;
  private BlockingQueue<Runnable> blockingQueue;
  private Socket socket;
  private BufferedReader in;
  private OutputStream out;

  public AcceptedRPC(Socket socket, BlockingQueue<Runnable> blockingQueue) {
    this.socket = socket;
    this.blockingQueue = blockingQueue;
  }

  @Override
  public void run() {
    openInput();
    openOutput();
    read();
    closeIn();

    RPCValidator validator = new RPCValidator();
    // TODO Pass off!!
  }

  private void openInput() {
    try {
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    } catch (IOException e) {

    }
  }

  private String read() {
    StringBuffer strbuffer = new StringBuffer(CAPACITY);
    String input = "";
    try {
      while ((input = in.readLine()) != null) {
        strbuffer.append(input);
        if (input.equals(""))
          break;
      }

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return strbuffer.toString();
  }

  private void openOutput() {
    try {
      out = socket.getOutputStream();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void closeIn() {
    try {
      in.close();
      // outputBuffer.close();
      socket.close();
    } catch (IOException e) {
      System.err.println("Problem with connection...");
    } catch (NullPointerException e) {
    }
    System.err.println("Couldn't close connections. Was the connection reset?");
  }

}
