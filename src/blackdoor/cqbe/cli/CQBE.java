package blackdoor.cqbe.cli;

import java.util.Arrays;
import java.util.Map;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.json.*;

import blackdoor.util.CommandLineParser;
import blackdoor.util.CommandLineParser.Argument;
import blackdoor.util.CommandLineParser.DuplicateOptionException;
import blackdoor.util.CommandLineParser.InvalidFormatException;
import blackdoor.util.DBP;
import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.output_logic.Router;
import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.node.NodeBuilder;

public class CQBE {

  /**
   * This is the main function that is called whenever the user ineracts with dart via cli
   *
   * @param args
   *        command line args
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Proper Usage is: java cqbe args");
      System.exit(0);
    }
    if (args[0].equals("cert")) {
      String[] args2 = Arrays.copyOfRange(args, 1, args.length);
      Certificate.main(args2);
    } else if (args[0].equals("keys")) {
      String[] args2 = Arrays.copyOfRange(args, 1, args.length);
      Keys.main(args2);
    } else if (args[0].equals("insert")) {
      String[] args2 = Arrays.copyOfRange(args, 1, args.length);
      CQBE.insert(args2);
    } else if (args[0].equals("retrieve")) {
      String[] args2 = Arrays.copyOfRange(args, 1, args.length);
      CQBE.retrieve(args2);
    } else if (args[0].equals("leave")) {
      String[] args2 = Arrays.copyOfRange(args, 1, args.length);
      CQBE.shutdown(args2);
    } else if (args[0].equals("join")) {
      String[] args2 = Arrays.copyOfRange(args, 1, args.length);
      CQBE.join(args2);
    } else {
      CommandLineParser clp = new CommandLineParser();
      clp.setExecutableName("cqbe");
      clp.setUsageHint("\tmandatory options to long options are mandatory for short options too.\n"
          + "The subcommands for cqbe are:\n" + "\t certificate \n" + "\t keys \n" + "\t join \n"
          + "\t insert \n" + "\t retrieve \n" + "\t leave \n");
      try {
        clp.addArgument(new Argument().setLongOption("subcommand").setParam(true)
            .setMultipleAllowed(false).setRequiredArg(false).setTakesValue(false)
            .setHelpText("the subcommand of certificate to execute."));
        clp.addArgument(new Argument().setLongOption("version").setOption("v")
            .setMultipleAllowed(false).setTakesValue(false).setHelpText("Displays the version."));
        clp.addArgument(new Argument().setLongOption("help").setOption("h")
            .setMultipleAllowed(false).setTakesValue(false).setHelpText("Display help menue."));
        Map<String, Argument> out = clp.parseArgs(args);
        if (out.containsKey("version")) {
          System.out.println("cqbe 1.0");
        } else {
          System.out.println(clp.getHelpText());
        }
      } catch (DuplicateOptionException e) {
        System.out.println("dups exeption");
      } catch (InvalidFormatException e) {
        System.out.println("invalidException");
      }

    }
  }

  /**
   * join and setup the dart network
   *
   * @param args
   *        list of arguments
   */
  public static void join(String[] args) {
    CommandLineParser clp = new CommandLineParser();
    Router r = new Router();
    Address destination = null;
    AddressTable neighbors = r.resolveAddress(destination);
    NodeBuilder bob = new NodeBuilder(neighbors);
    clp.setExecutableName("cqbe join");
    try {
      clp.addArgument(new Argument().setLongOption("port").setOption("p").setMultipleAllowed(false)
          .setTakesValue(true));
      clp.addArgument(new Argument().setLongOption("adam").setOption("a").setMultipleAllowed(false));
      clp.addArgument(new Argument().setLongOption("dir").setOption("d").setMultipleAllowed(false)
          .setTakesValue(true));
      clp.addArgument(new Argument().setLongOption("revive").setOption("r")
          .setMultipleAllowed(false).setTakesValue(true));
      clp.addArgument(new Argument().setLongOption("daemon").setOption("dm")
          .setMultipleAllowed(false));
      Map<String, Argument> out = clp.parseArgs(args);
      if (out.containsKey("help")) {
        System.out.println(clp.getHelpText());
      } else {
        if (out.containsKey("port")) {
          int port = Integer.parseInt((out.get("port").getValues().get(0)));
          bob.setPort(port);
        }
        if (out.containsKey("adam")) {
          bob.setAdam(true);
        }
        if (out.containsKey("dir")) {
          String dir = out.get("dir").getValues().get(0);
          bob.setDirectory(dir);
        }
        if (out.containsKey("revive")) {
          String revive = out.get("revive").getValues().get(0);
          bob.setRevival(revive);
        }
        if (out.containsKey("daemon")) {
          bob.setDaemon(true);
        }
        bob.buildNode();
      }
    } catch (DuplicateOptionException e) {
      System.out.println("dups exeption");
    } catch (InvalidFormatException e) {
      System.out.println("invalidException");
    } catch (Exception e) {
      System.out.println("all other exeptions");
    }
  }

  /**
   * Insert certfile(s) into the network
   *
   * @param args
   *        list of arguments
   */
  public static void insert(String[] args) {
    CommandLineParser clp = new CommandLineParser();
    clp.setExecutableName("cqbe insert");
    try {
      clp.addArgument(new Argument().setLongOption("file").setParam(true).setMultipleAllowed(false)
          .setRequiredArg(true));
      clp.addArgument(new Argument().setLongOption("bootstrap").setOption("b")
          .setMultipleAllowed(false).setTakesValue(true).setHelpText("the bootstrap node."));
      clp.addArgument(new Argument().setLongOption("help").setOption("h").setMultipleAllowed(false)
          .setTakesValue(false).setHelpText("Display help menue."));
      Map<String, Argument> out = clp.parseArgs(args);
      if (out.containsKey("help")) {
        System.out.println(clp.getHelpText());
      } else {
        File file = new File(out.get("file").getValue());
        if (!existsAndReadable(file)) {
          System.out.println("specified file does not exist or we lack read permissions");
          return;
        }
        if (out.containsKey("bootstrap")) {
          Address a = new Address(out.get("bootstrap").getValues().get(0));
          RPCBuilder rpcObject = new RPCBuilder();
          JSONObject rpc = rpcObject.buildPUT(file);
          Router router = new Router(a);
          router.routeWithCalls(rpc);
        } else {
          RPCBuilder rpcObject = new RPCBuilder();
          JSONObject rpc = rpcObject.buildPUT(file);
          Router router = new Router();
          router.routeWithCalls(rpc);
        }
      }
    } catch (DuplicateOptionException e) {
      System.out.println("dups exeption");
    } catch (InvalidFormatException e) {
      System.out.println("invalidException");
    } catch (Exception e) {
      System.out.println("all other exeptions");
    }
  }

  private static boolean existsAndReadable(File f) {
    Path file = f.toPath();
    return Files.isRegularFile(file) & Files.isReadable(file);
  }

  /**
   * Finds a specific certificate in the network
   *
   * @param args
   *        list of arguments
   */
  public static void retrieve(String[] args) {
    CommandLineParser clp = new CommandLineParser();
    clp.setExecutableName("cqbe retrieve");
    try {
      clp.addArgument(new Argument().setLongOption("subjectUID").setParam(true)
          .setMultipleAllowed(false).setRequiredArg(true));
      clp.addArgument(new Argument().setLongOption("endorse").setOption("e")
          .setMultipleAllowed(false).setTakesValue(false)
          .setHelpText("item being retrieved is an endorsement."));
      clp.addArgument(new Argument().setLongOption("issuer").setOption("i")
          .setMultipleAllowed(false).setTakesValue(true).setHelpText("issuer unique identifier."));
      clp.addArgument(new Argument().setLongOption("list").setOption("l").setMultipleAllowed(false)
          .setTakesValue(true)
          .setHelpText("list of endorsements for this subject unique identifier."));
      clp.addArgument(new Argument().setLongOption("dir").setOption("d").setMultipleAllowed(false)
          .setTakesValue(false).setHelpText("location for the retrieved file to be stored."));
      clp.addArgument(new Argument().setLongOption("help").setOption("h").setMultipleAllowed(false)
          .setTakesValue(false).setHelpText("Display help menue."));
      Map<String, Argument> parsedArgs = clp.parseArgs(args);
      if (parsedArgs.containsKey("help")) {
        System.out.println(clp.getHelpText());
      } else if (parsedArgs.containsKey("endorse")) {
        if (parsedArgs.containsKey("issuer")) {

          RPCBuilder rpcObject = new RPCBuilder();
          JSONObject rpc =
              rpcObject.buildGETendorsement(parsedArgs.get("subjectUID").getValue(), parsedArgs
                  .get("issuer").getValue());
          Router router = new Router();
          JSONObject enorceJSON = router.routeWithCalls(rpc);
          File outputFile = new File("out.endrsmnt");
          if (parsedArgs.containsKey("dir")) {
            outputFile = new File(parsedArgs.get("dir").getValue());
          }
          Files.write(outputFile.toPath(), RPCBuilder.getBinary(enorceJSON),
              StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } else {
          System.out.println("a issuer unique identifier must be provided");
          System.out.println(clp.getHelpText());
        }
      } else if (parsedArgs.containsKey("list")) {
        RPCBuilder rpcObject = new RPCBuilder();
        JSONObject rpc = rpcObject.buildGETendorsementList(parsedArgs.get("subjectUID").getValue());
        Router router = new Router();
        JSONObject enorceListJSON = router.routeWithCalls(rpc);
        System.out.println(enorceListJSON.toString());
      } else {
        RPCBuilder rpcObject = new RPCBuilder();
        JSONObject rpc = rpcObject.buildGETcertificate(parsedArgs.get("subjectUID").getValue());
        Router router = new Router();
        JSONObject certificateJSON = router.routeWithCalls(rpc);
        File outputFile = new File("out.certificate");
        if (parsedArgs.containsKey("dir")) {
          outputFile = new File(parsedArgs.get("dir").getValue());
        }
        Files.write(outputFile.toPath(), RPCBuilder.getBinary(certificateJSON),
            StandardOpenOption.CREATE, StandardOpenOption.WRITE);
      }
    } catch (DuplicateOptionException e) {
      System.out.println("dups exeption");
    } catch (InvalidFormatException e) {
      System.out.println("invalidException");
    } catch (Exception e) {
      System.out.println("all other exeptions");
    }
  }

  /**
   * Used to exit the network
   *
   * @param args
   *        list of arguments
   */
  public static void shutdown(String[] args) {
    CommandLineParser clp = new CommandLineParser();
    clp.setExecutableName("cqbe shutdown");
    try {
      clp.addArgument(new Argument().setLongOption("port").setOption("p").setMultipleAllowed(false)
          .setRequiredArg(true).setTakesValue(true)
          .setHelpText("The port of the node to be shutdown"));
      Map<String, Argument> out = clp.parseArgs(args);
      if (out.containsKey("help")) {
        System.out.println(clp.getHelpText());
      } else {
        int port = Integer.parseInt(out.get("port").getValues().get(0));
        RPCBuilder r = new RPCBuilder();
        JSONObject rpc = r.buildSHUTDOWN(port);
        Router router = new Router();
        router.routeWithCalls(rpc);
      }
    } catch (DuplicateOptionException e) {
      System.out.println("dups exeption");
    } catch (InvalidFormatException e) {
      System.out.println("invalidException");
    } catch (Exception e) {
      System.out.println("all other exeptions");
    }
  }
}
