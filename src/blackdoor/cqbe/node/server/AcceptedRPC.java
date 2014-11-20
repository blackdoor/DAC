package blackdoor.cqbe.node.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import org.json.JSONObject;

import blackdoor.auth.AuthRequest;
import blackdoor.cqbe.rpc.RPCValidator;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.2 - Nov 17, 2014
 */
public class AcceptedRPC implements Runnable {
  private BlockingQueue<Runnable> blockingQueue;
  private Socket socket = null;
  private OutputStream out;
  private InputStream in;
  private BufferedInputStream bis;

  public AcceptedRPC(Socket socket, BlockingQueue<Runnable> blockingQueue) {
    this.socket = socket;
    this.blockingQueue = blockingQueue;
    openInput();
    openOutput();
  }

  /**
 * 
 */
  @Override
  public void run() {
    System.out.println("Got it.");
    String input = read();

    // pass outstream and string
    // RPCValidator validator = new RPCValidator(); TODO Pass off!!

    // FOR TESTING
    System.out.println(":::RECIEVED:::\n " + input);
    //closeIn();
  }

  /**
   * 
   * @return
   */
  private String read() {
    StringBuilder sb = new StringBuilder();
    bis = new BufferedInputStream(in);
    byte[] bytes = new byte[100];
    int s = 0;
    int index = 0;

    try {
      while (true) {
        s = bis.read();
        if (s == 10) {
          break;
        }
        bytes[index++] = (byte) (s);
        if (index == bytes.length) {
          sb.append(new String(bytes));
          bytes = new byte[100];
          index = 0;
        }
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


    if (index > 0) {
      sb.append(new String(Arrays.copyOfRange(bytes, 0, index)));
    }

    return sb.toString();
  }

  /**
   * 
   */
  private void openInput() {
    try {
      // in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      // stdIn = new BufferedReader(new InputStreamReader(System.in));
      in = socket.getInputStream();
    } catch (IOException e) {
      System.err.println("BufferReader is no GO.");
    }
  }

  /**
 * 
 */
  private void openOutput() {
    try {
      // out = socket.getOutputStream();
      out = socket.getOutputStream();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      System.err.println("Could not Open OutputSocket...");
    }
  }

  /**
 * 
 */
  private void closeIn() {
    try {
      // in.close();
      // out.close(); // TODO for Testing
      bis.close();
      in.close();
      out.close();
      socket.close();
      // inputBuffer.close();
      // outputObject.close();

    } catch (IOException e) {
      System.err.println("Problem with connection...");
    } catch (NullPointerException e) {
      System.err.println("NULLPOINTER in CloseIn");
    }
    System.err.println("Couldn't close connections. Was the connection reset?");
  }

}
