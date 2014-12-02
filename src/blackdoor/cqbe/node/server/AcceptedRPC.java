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
import blackdoor.util.DBP;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.2 - Nov 17, 2014
 */
public class AcceptedRPC implements Runnable {
  private final int BUFFER_SIZE = 64 * 1024;
  private Socket socket = null;
  private OutputStream out;
  private InputStream in;

  public AcceptedRPC(Socket socket) {
    this.socket = socket;
    openInputStream();
    openOutputStream();
  }

  /**
 * 
 */
  @Override
  public void run() {
    String input = read();
    DBP.printdevln(":::RECIEVED::\t " + input);
    shutdownSocketInput();

  }

  /**
   * 
   * @return
   */
  private String read() {
    StringBuilder sb = new StringBuilder();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(in));
      String inputLine;
      while ((inputLine = br.readLine()) != null) {
        sb.append(inputLine);
      }

    } catch (IOException e) {
      DBP.printerror("Problem reading from InputStream...");
      DBP.printException(e);
      return null;
    }

    return sb.toString();
  }

  /**
   * 
   */
  private void openInputStream() {
    try {
      in = socket.getInputStream();
    } catch (IOException e) {
      DBP.printerror("Problem opening InputStream...");
      DBP.printException(e);
    }
  }

  /**
 * 
 */
  private void openOutputStream() {
    try {
      out = socket.getOutputStream();
    } catch (IOException e) {
      DBP.printerror("Problem opening OutputStream...");
      DBP.printException(e);
    }
  }

  /**
 * 
 */
  private void shutdownSocketInput() {
    try {
      // socket.shutdownInput();
      socket.close();
    } catch (IOException e) {
      DBP.printerror("Problem shutting down InputStream...");
      DBP.printException(e);
    }
  }

}
