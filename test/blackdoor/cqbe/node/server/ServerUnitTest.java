package blackdoor.cqbe.node.server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.node.server.Server;
import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.net.SocketIOWrapper;
import blackdoor.util.DBP;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Jan 27, 2015
 */
public class ServerUnitTest {

  private final static int SERVER_PORT = 1776;
  private static Server server = null;
  private static boolean continuetests = true;

  @BeforeClass
  public static void executedBeforeAll() throws InterruptedException, ServerException {
    server = new Server(SERVER_PORT);
    new Thread(server).start();
    Thread.sleep(100);
    if (!server.isRunning()) {
      continuetests = false;
    }
  }

  @AfterClass
  public static void executedAferAll() throws InterruptedException {
    server.stop();
    Thread.sleep(100);
    if (server.isRunning()) {
      DBP.printerrorln("Server did not shut down....");
    }
    server = null;
  }

  @Test
  public void testBasicInteraction() throws InterruptedException, ServerException {
    if (continuetests) {
      int port = 1010;
      TestInteraction ti = null;
      String response;
      try {
        ti = new TestInteraction(port);
        ti.write(ti.getFakeRPC().toString());
        response = ti.read();
      } catch (IOException e) {
        e.printStackTrace();
        fail("RPC Error: " + e.getMessage());
      }
    } else {
      fail("Did not run 'basic interaction' test... Server could not be started.");
    }

  }

  @Test
  public void testThreadPoolMax() throws InterruptedException, ServerException {
    if (continuetests) {
      int MaxPoolNum = 150;
      ArrayList<TestInteraction> tilist = new ArrayList<>();
      String response;
      try {
        for (int i = 0; i < MaxPoolNum; i++) {
          tilist.add(new TestInteraction(i + 8000));
        }

        for (TestInteraction elem : tilist) {
          new Thread(elem).start();
        }

      } catch (IOException e) {
        e.printStackTrace();
        fail("Test Error: " + e.getMessage());
      }
    } else {
      fail("Did not run 'basic interaction' test... Server could not be started.");
    }
  }

  /**
   * 
   * @author Cj Buresch
   * @version v0.0.1 - Jan 29, 2015
   */
  private class TestInteraction implements Runnable {

    private int port;
    private String desthost;
    private int destport;
    private SocketIOWrapper io;
    private final int BUFFER_SIZE = 64 * 1024;
    private boolean hold = false;

    public TestInteraction(int port) throws IOException {
      this.port = port;
      io = new SocketIOWrapper(new Socket(InetAddress.getLoopbackAddress(), SERVER_PORT));
    }

    public void write(String content) throws IOException {
      io.write(content);
    }

    public String read() throws IOException {
      return io.read();
    }

    public void closeOut() throws IOException {
      io.close();
    }

    @Override
    public void run() {
      try {
        write(getFakeRPC().toString());
        String response = read();
      } catch (IOException e) {
        fail("Problems with Interaction");
        e.printStackTrace();
      } finally {
        try {
          closeOut();
        } catch (IOException e) {
          fail("Problems closing Interaction");
          e.printStackTrace();
        }
      }
    }

    public JSONObject getActualRPC() {
      RPCBuilder builder = new RPCBuilder();
      JSONObject obj = null;
      try {

        builder.setDestinationO(new L3Address(InetAddress.getByName(desthost), destport));
        builder.setSource(new L3Address(InetAddress.getLoopbackAddress(), port));
        obj = builder.buildLOOKUP();
      } catch (Exception e) {
        return null;
      }
      return obj;
    }

    public JSONObject getFakeRPC() throws IOException {
      JSONObject rpc = new JSONObject();
      rpc.put("jsonrpc", "2.0");
      rpc.put("method", "TESTER");
      rpc.put("id", 99999999);
      return rpc;
    }

  }

}
