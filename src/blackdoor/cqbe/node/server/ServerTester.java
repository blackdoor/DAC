package blackdoor.cqbe.node.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;


public class ServerTester {
  public static void main(String[] args) {
    int port = 1778;
    String hostname = "localhost";

    Scanner s = new Scanner(System.in);
    String in = s.next();
    for (int i = 0; i < 2; i++) {
      try {
        Socket kkSocket = new Socket(hostname, port);
        ObjectOutputStream out = new ObjectOutputStream(kkSocket.getOutputStream());
        // PrintWriter out = new PrintWriter(kkSocket.getOutputStream());
        // BufferedReader sin = new BufferedReader(new
        // InputStreamReader(kkSocket.getInputStream()));
        // ObjectOutput oo = new ObjectOutputStream(kkSocket.getOutputStream());
        out.writeBytes(in);
        System.out.println("Sending " + in);
        //Thread.sleep(1000);
       // out.close();
        out.flush();
        //kkSocket.close();
      } catch (Exception e) {
        System.out.println("NOPE");
      }
      in = s.next();

    }
    // STOP
    s.close();

  }
}
