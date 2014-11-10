/**
 * 
 */
package blackdoor.cqbe.cli;

import java.io.Console;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import blackdoor.util.CommandLineParser;
import blackdoor.util.CommandLineParser.Argument;
import blackdoor.util.CommandLineParser.DuplicateOptionException;
import blackdoor.util.CommandLineParser.InvalidFormatException;
import blackdoor.util.DBP;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 5, 2014
 */
public class Keys {

  public static void main(String[] args) {
    DBP.DEBUG = true;
    DBP.ERROR = true;

    CommandLineParser parser = getParser();
    Map<String, Argument> parsedArgs;
    try {
      parsedArgs = parser.parseArgs(args);
      if (parsedArgs.containsKey("subcommand")) {
        switch (args[0]) {
          case "create":
            create(Arrays.copyOfRange(args, 1, args.length));
            break;
          case "retrieve":
            Keys.retrive(Arrays.copyOfRange(args, 1, args.length));
            break;
          case "remove":
            Keys.remove(Arrays.copyOfRange(args, 1, args.length));
            break;
        }
      } else {
        System.out.println(parser.getHelpText());

      }
    } catch (InvalidFormatException e) {
      System.out.println(parser.getHelpText());
      DBP.printerrorln(e.getMessage());
      for (StackTraceElement elem : e.getStackTrace()) {
        DBP.printerrorln(elem);
      }
    }
  }
  /**
   * 
   * @return
   */
  private static CommandLineParser getParser() {
    CommandLineParser parser = new CommandLineParser();
    parser.setExecutableName("cqbe keys");
    parser
        .setUsageHint("\tmandatory options to long options are mandatory for short options too.\n"
            + "The subcommands for cqbe keys are:\n" 
            + "\t create \n" 
            + "\t retrieve \n"
            + "\t remove \n");
    Argument subcommand =
        new Argument().setLongOption("subcommand").setParam(true).setMultipleAllowed(false)
            .setRequiredArg(false).setTakesValue(false)
            .setHelpText("the subcommand of keys to execute.");
    // DBP.printdebugln(subcommand);
    Argument helpArgument =
        new Argument().setLongOption("help").setOption("h").setMultipleAllowed(false)
            .setTakesValue(false).setHelpText("print this help.");
    try {
      parser.addArgument(subcommand);
      parser.addArgument(helpArgument);
    } catch (DuplicateOptionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return parser;
  }
  
  /**
   * 
   * @return
   */
  private static CommandLineParser getCreateParser() {
    CommandLineParser parser = new CommandLineParser();
    parser.setUsageHint("");
    parser.setExecutableName("cqbe keys create");
    Argument alias = new Argument().setLongOption("ALIAS")
            .setParam(true).setMultipleAllowed(false).setRequiredArg(true);
    Argument store = new Argument()
            .setLongOption("store")
            .setOption("s")
            .setMultipleAllowed(false)
            .setValueRequired(true)
            .setHelpText(
                    "a keystore that is different than the default")
            .setValueHint("FILE");
    Argument output = new Argument()
    .setLongOption("output")
    .setOption("o")
    .setMultipleAllowed(false)
    .setValueRequired(true)
    .setHelpText(
            "location of public key cryptography standards(PKCS) output")
    .setValueHint("DIRECTORY");
    try {
        parser.addArguments(new Argument[]{alias, store, output});
    } catch (DuplicateOptionException e) {
        DBP.printException(e);
    }
    return parser;
  }
  /**
   * 
   * @return
   */
  private static CommandLineParser getRetrieveParser() {
    CommandLineParser parser = new CommandLineParser();
    parser.setUsageHint("");
    parser.setExecutableName("cqbe keys retrieve");
    Argument alias = new Argument().setLongOption("ALIAS")
            .setParam(true).setMultipleAllowed(false).setRequiredArg(true);
    Argument store = new Argument()
            .setLongOption("store")
            .setOption("s")
            .setMultipleAllowed(false)
            .setValueRequired(true)
            .setHelpText(
                    "retrieve a key of ALIAS from FILE")
            .setValueHint("FILE");
    Argument output = new Argument()
    .setLongOption("output")
    .setOption("o")
    .setMultipleAllowed(false)
    .setValueRequired(true)
    .setHelpText(
            "location that output should to be stored.")
    .setValueHint("DIRECTORY");
    try {
        parser.addArguments(new Argument[]{alias, store, output});
    } catch (DuplicateOptionException e) {
        DBP.printException(e);
    }
    return parser;
  }
  
  private static CommandLineParser getRemoveParser() {
    CommandLineParser parser = new CommandLineParser();
    parser.setUsageHint("");
    parser.setExecutableName("cqbe keys remove");
    Argument alias = new Argument().setLongOption("ALIAS")
            .setParam(true).setMultipleAllowed(false).setRequiredArg(true);
    Argument store = new Argument()
            .setLongOption("store")
            .setOption("s")
            .setMultipleAllowed(false)
            .setValueRequired(true)
            .setHelpText(
                    "removes key of ALIAS from FILE.")
            .setValueHint("FILE");
    try {
        parser.addArguments(new Argument[]{alias, store});
    } catch (DuplicateOptionException e) {
        DBP.printException(e);
    }
    return parser;
  }

  /**
   * Creates a keypair and stores it encrypted in a keystore.
   * <p>
   *
   * @param args List of Arguments.
   */
  public static void create(String[] args) {
    String password = scannerPrompt(); // for IDE TESTING ONLY
  }

  /**
   * Retrieves a key from a keystore.
   * <p>
   *
   * @param args List of Arguments.
   */
  public static void retrive(String[] args) {
    String password = scannerPrompt(); // for IDE TESTING ONLY
  }

  /**
   * Removes a key from a keystore.
   * <p>
   *
   * @param args List of Arguments.
   */
  public static void remove(String[] args) {
    String password = scannerPrompt(); // for IDE TESTING ONLY
  }

  /**
   * Gets password from user input.
   * <p>
   * Characters are hidden from console, and console history.
   * 
   * @return password input.
   */
  private static String consolePrompt() {
    Console cons = null;
    char[] passwd = null;
    if ((cons = System.console()) != null
        && (passwd = cons.readPassword("[%s]", "enter password:")) != null) {
      java.util.Arrays.fill(passwd, ' ');
    } else {
      // Handle a null console, or no password entry!
    }
    return passwd.toString();
  }

  /**
   * Gets password from user input.
   * <p>
   * USED ONLY FOR IDE TESTING. Will expose password to console history, which is a security flaw.
   * Do not do that.
   * 
   * @return password input.
   */
  private static String scannerPrompt() {
    System.out.println("enter password:");
    Scanner s = new Scanner(System.in);
    String password = s.next();
    // Check password format

    return password;
  }
}
