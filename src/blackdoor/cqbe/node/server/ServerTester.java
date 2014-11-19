package blackdoor.cqbe.node.server;

public class ServerTester {
  public static void main(String[] args) {
    Server server = new Server(1776);
    // START
    new Thread(server).start();

    // TODO WORKKKKKKKKK

    // STOP
    server.stop();
  }
}
