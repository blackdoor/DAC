package blackdoor.cqbe.node.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;


public class ServerTester {
  public static void main(String[] args) {
    int port = 1778;
    String hostname = "localhost";
    Socket kkSocket = null;
    PrintWriter out = null;
    Scanner s = new Scanner(System.in);
    String in = s.nextLine();
    try {
      kkSocket = new Socket(hostname, port);
      out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(kkSocket.getOutputStream())));
      for (int i = 0; i < 2; i++) {
        if (!in.equals("stop")) {
          System.out.println("Sending " + in);
          out.println(in);
          out.flush();
          in = s.nextLine();
          // Thread.sleep(1000);
        } else {
          System.out.println("EXITING");
          break;
        }
      }
    } catch (IOException e) {
      System.err.println("NOPE");
    }


    // STOP
    out.close();

    try {
      kkSocket.close();
    } catch (IOException e) {
      System.err.println("not closing....");
    }
    s.close();

  }
}
