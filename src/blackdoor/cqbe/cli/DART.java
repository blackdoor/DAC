package blackdoor.cqbe.cli;

import java.util.Arrays;
import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.output_logic.Router;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.node.NodeBuilder;

public class DART {

  /**
   * This is the main function that is called whenever the user ineracts with dart via cli
   *
   * @param args command line args
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Proper Usage is: java DART args");
      System.exit(0);
    }
    if (args[0].equals("cert")) {
      String[] args2 = Arrays.copyOfRange(args, 1, args.length);
      new Certificate(args2);
    } else if (args[0].equals("keys")) {
      String[] args2 = Arrays.copyOfRange(args, 1, args.length);
      new Keys(args2);
    } else if (args[0].equals("join")) {
      String[] args2 = Arrays.copyOfRange(args, 1, args.length);
      DART.join(args2);
    } else if (args[0].equals("help")) {
      String[] args2 = Arrays.copyOfRange(args, 1, args.length);
      DART.help(args2);
    } else {
      DART.help(args);
    }
  }

  /**
   * Used to print a menu of commands, their descriptions and uses. Can be used injunction with a
   * specific command to get just that command’s description.
   *
   * @param args list of arguments
   */
  public static void help(String[] args) {}

  /**
   * join and setup the dart network
   *
   * @param args list of arguments
   */
  public static void join(String[] args) {
    // Parse things into
    Router r = new Router();
    Address destination = null;
    int port = -1;
    Boolean adam = false;
    String dir = "";
    String revive = "";
    Boolean daemon = false;
    AddressTable neighbors = r.resolveAddress(destination);

    NodeBuilder bob = new NodeBuilder(neighbors);
    bob.setAdam(adam);
    bob.setDaemon(daemon);
    if (dir != "")
      bob.setDirectory(dir);
    if (revive != "")
      bob.setRevival(revive);
    if (port != -1)
      bob.setPort(port);

    bob.buildNode();
  }

  /**
   * Insert certfile(s) into the network
   *
   * @param args list of arguments
   */
  public static void insert(String[] args) {

  }

  /**
   * Finds a specific certificate in the network
   *
   * @param args list of arguments
   */
  public static void retrieve(String[] args) {}

  /**
   * Used to exit the network
   *
   * @param args list of arguments
   */
  public static void shutdown(String[] args) {
    // CrazyIvan
    // RPCBuilder r = new RPCBuilder();
    // r.buildSHUTDOWN();

  }
}
