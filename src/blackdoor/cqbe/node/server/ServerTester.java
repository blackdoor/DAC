package blackdoor.cqbe.node.server;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.json.JSONObject;


public class ServerTester {

  private String hostname;
  private int port;
  private Socket csock = null;

  public static void main(String[] args) {
    ServerTester ts = new ServerTester();
    ts.sendJSON(ts.getStubRPC());
    ts.closeOut();
  }

  public ServerTester() {
    this.port = 1778;
    hostname = "localhost";
    grabSocket();
  }

  public void grabSocket() {
    try {
      csock = new Socket(hostname, port);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void closeOut() {
    try {
      csock.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sendJSON(JSONObject jo) {
    String input = jo.toString();
    PrintWriter out;
    try {
      out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(csock.getOutputStream())));
      out.print(input);
      out.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public JSONObject getStubRPC() {
    JSONObject rpc = new JSONObject();
    rpc.put("jsonrpc", "2.0");
    rpc.put("method", "TESTER");
    rpc.put("id", "FAKETESTER");
    return rpc;
  }
}
