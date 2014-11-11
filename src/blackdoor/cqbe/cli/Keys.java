/**
 * 
 */
package blackdoor.cqbe.cli;

import java.io.Console;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import blackdoor.cqbe.keystore.KeyStore;
import blackdoor.cqbe.settings.Config;
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
    if (args.length > 0) {
      try {
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
          default:
            parsedArgs = parser.parseArgs(args);
            DBP.printdebugln(parsedArgs);
            System.out.println(parser.getHelpText());
            break;
        }
      } catch (InvalidFormatException e) {
        System.out.println(parser.getHelpText());
        DBP.printException(e);
      }
    } else {
      System.out.println(parser.getHelpText());
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
            + "The subcommands for cqbe keys are:\n" + "\t create \n" + "\t retrieve \n"
            + "\t remove \n");
    Argument subcommand =
        new Argument().setLongOption("subcommand").setParam(true).setMultipleAllowed(false)
            .setRequiredArg(false).setTakesValue(false)
            .setHelpText("the subcommand of keys to execute.");
    Argument helpArgument =
        new Argument().setLongOption("help").setOption("h").setMultipleAllowed(false)
            .setTakesValue(false).setHelpText("print this help.");
    try {
      parser.addArguments(new Argument[] {subcommand, helpArgument});
    } catch (DuplicateOptionException e) {
      DBP.printException(e);
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
    Argument alias =
        new Argument().setLongOption("alias").setParam(true).setMultipleAllowed(false)
            .setRequiredArg(true);
    Argument store =
        new Argument().setLongOption("store").setOption("s").setMultipleAllowed(false)
            .setValueRequired(true).setHelpText("a keystore that is different than the default.")
            .setValueHint("FILE");
    Argument output =
        new Argument().setLongOption("output").setOption("o").setMultipleAllowed(false)
            .setValueRequired(true)
            .setHelpText("location of public key cryptography standards(PKCS) output.")
            .setValueHint("DIRECTORY");
    try {
      parser.addArguments(new Argument[] {alias, store, output});
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
    Argument alias =
        new Argument().setLongOption("alias").setParam(true).setMultipleAllowed(false)
            .setRequiredArg(true);
    Argument store =
        new Argument().setLongOption("store").setOption("s").setMultipleAllowed(false)
            .setValueRequired(true).setHelpText("retrieve a key of ALIAS from FILE.")
            .setValueHint("FILE");
    Argument output =
        new Argument().setLongOption("output").setOption("o").setMultipleAllowed(false)
            .setValueRequired(true).setHelpText("location that output should to be stored.")
            .setValueHint("DIRECTORY");
    Argument display =
        new Argument().setLongOption("display").setOption("d").setMultipleAllowed(false)
            .setValueRequired(true).setHelpText("password displayed in terminal.");
    try {
      parser.addArguments(new Argument[] {alias, store, output});
    } catch (DuplicateOptionException e) {
      DBP.printException(e);
    }
    return parser;
  }

  private static CommandLineParser getRemoveParser() {
    CommandLineParser parser = new CommandLineParser();
    parser.setUsageHint("");
    parser.setExecutableName("cqbe keys remove");
    Argument alias =
        new Argument().setLongOption("alias").setParam(true).setMultipleAllowed(false)
            .setRequiredArg(true);
    Argument store =
        new Argument().setLongOption("store").setOption("s").setMultipleAllowed(false)
            .setValueRequired(true).setHelpText("removes key of ALIAS from FILE.")
            .setValueHint("FILE");
    try {
      parser.addArguments(new Argument[] {alias, store});
    } catch (DuplicateOptionException e) {
      DBP.printException(e);
    }
    return parser;
  }

  /**
   * Creates a keypair and stores it encrypted in a keystore.
   * <p>
   *
   * @param args
   *        List of Arguments.
   * @throws InvalidFormatException
   */
  public static void create(String[] args) throws InvalidFormatException {
    CommandLineParser parser;
    File in;
    File out;
    String password;
    KeyStore ks;

    Map<String, Argument> parsedArgs;
    parser = getCreateParser();
    parsedArgs = parser.parseArgs(args);

    //
    if (!parsedArgs.containsKey("alias")) {
      System.out.println("Must enter a value alias." + parser.getHelpText());
      return;
    }
    // Get KeyStore!
    ks = grabKeyStore(parsedArgs);

    if (ks.containsAlias(parsedArgs.get("alias").getValue())) {
      System.out.println("alias already exists in specified keystore file.");
      return;
    } else {
      ks.newEntry(parsedArgs.get("alias").getValue()); // TODO Needs more stuff....
    }

    ks.store(); // throw stuff
  }

  /**
   * Retrieves a key from a keystore.
   * <p>
   *
   * @param args
   *        List of Arguments.
   */
  public static void retrive(String[] args) throws InvalidFormatException {
    CommandLineParser parser;
    File in;
    File out;
    String password;
    KeyStore ks;

    Map<String, Argument> parsedArgs;
    parser = getRetrieveParser();
    parsedArgs = parser.parseArgs(args);

    //
    if (!parsedArgs.containsKey("alias")) {
      System.out.println("Must enter a value alias." + parser.getHelpText());
      return;
    }
    // Get KeyStore!
    ks = grabKeyStore(parsedArgs);

    if (!ks.containsAlias(parsedArgs.get("alias").getValue())) {
      System.out.println("alias already exists in specified keystore file.");
      return;
    } else {
      ks.newEntry(parsedArgs.get("alias").getValue()); // TODO Needs more stuff....
    }

    ks.store(); // throw stuff
  }

  /**
   * Removes a key from a keystore.
   * <p>
   *
   * @param args
   *        List of Arguments.
   */
  public static void remove(String[] args) throws InvalidFormatException {
   //TODO soon.......
  }


  /*
   * //////////////////////////// PRIVATE HELPERS ////////////////////////////
   *//**
   * @param valueHint
   *        the valueHint to set
   */

  private static KeyStore grabKeyStore(Map<String, Argument> parsedArgs) {
    File in;
    File out;
    String password;
    boolean isNew = false;

    // IN
    if (parsedArgs.containsKey("store")) {
      // use the entered file to add the alias
      in = new File(parsedArgs.get("store").getValue());
      if (!existsAndReadable(in)) {
        System.out
            .println("Specified keystore file does not exist or does not have read permissions.");
        return null; // TODO throw something
      }
      password = scannerPrompt(); // for IDE TESTING ONLY
    } else {
      // Use default
      in = new File(Config.getKeystoreDir());
      if (!existsAndReadable(in)) {
        // Initialize keystore!
        System.out.println("Keystore does not exist. Creating new KeyStore...");
        isNew = true;
        password = newpassword_scannerPrompt();
      } else {
        // get password to decrypt existing keystore.
        password = scannerPrompt(); // for IDE TESTING ONLY
      }
    }
    // OUT
    if (parsedArgs.containsKey("output")) {
      // use entered
      out = new File(parsedArgs.get("output").getValue());
    } else {
      // default
      out = new File(Config.getKeystoreDir());
    }

    KeyStore ks = new KeyStore().setIn(in).setOut(out).setPassword(password).isnewKeyStore(isNew);
    ks.load();

    return ks;
  }

  /**
   * 
   * @param f
   * @return
   */
  private static boolean existsAndReadable(File f) {
    Path file = f.toPath();
    return Files.isRegularFile(file) & Files.isReadable(file);
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

  private static String newpassword_consolePrompt() {
    Console cons = null;
    char[] passwd = null;
    if ((cons = System.console()) != null
        && (passwd = cons.readPassword("[%s]", "enter password to encrypt keystore:")) != null) {
      java.util.Arrays.fill(passwd, ' ');
    } else {
      // Handle a null console, or no password entry!
    }
    return passwd.toString();
  }

  private static String newpassword_scannerPrompt() {
    System.out.println("enter password to encrypt keystore:");
    Scanner s = new Scanner(System.in);
    String password = s.next();
    // Check password format

    return password;
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
