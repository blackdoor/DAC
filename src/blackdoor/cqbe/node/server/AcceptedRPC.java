package blackdoor.cqbe.node.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
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
  private BufferedReader in = null;
  // private BufferedReader stdIn = null;
  private OutputStream out;
  private ObjectOutputStream outputStream;
  // private InputStream inputBuffer;
  private ObjectInputStream inputStream;

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

    String input = read();

    // pass outstream and string
    // RPCValidator validator = new RPCValidator();
    // TODO Pass off!!
    // FOR TESTING
    System.out.println("RECIEVED::: " + input);
    closeIn();
  }

  /**
   * 
   * @return
   */
  private String read() {
    String result = "";
    try {
      // StringBuilder sb = new StringBuilder();
      // String line = in.readLine();
      //
      // while ((line = in.readLine()) != null) {
      // sb.append(line);
      // // Nessessary???? private ObjectOutput outputObject;
      // sb.append(System.lineSeparator());
      // if (line == "stop")
      // break;
      // }
      // result = sb.toString();
      result = (String) inputStream.readObject();
    } catch (IOException e) {
      System.err.println("Not reading Correctly???");
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      System.err.println("Class No Found?");
    } finally {

    }
    return result;
  }

  /**
   * 
   */
  private void openInput() {
    try {
      // in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      // stdIn = new BufferedReader(new InputStreamReader(System.in));
      inputStream = new ObjectInputStream(socket.getInputStream());
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
      outputStream = new ObjectOutputStream(socket.getOutputStream());
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
      socket.close();
      // inputBuffer.close();
      // outputObject.close();
      this.inputStream.close();
      this.outputStream.close();
    } catch (IOException e) {
      System.err.println("Problem with connection...");
    } catch (NullPointerException e) {
      System.err.println("NULLPOINTER in CloseIn");
    }
    System.err.println("Couldn't close connections. Was the connection reset?");
  }

}
