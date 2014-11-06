package blackdoor.cqbe.cli;

import java.util.Arrays;
import java.util.Map;

import org.json.simple.JSONObject;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.output_logic.Router;
import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.util.CommandLineParser;

public class DART {

	/**
	 * This is the main function that is called whenever the user ineracts with
	 * dart via cli
	 *
	 * @param args
	 *            command line args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Proper Usage is: java DART args");
			System.exit(0);
		}
		if (args[0].equals("cert")) {
			String[] args2 = Arrays.copyOfRange(args, 1, args.length);
			CertificateUILogic.main(args2);
		} else if (args[0].equals("keys")) {
			String[] args2 = Arrays.copyOfRange(args, 1, args.length);
			Keys.main(args2);
		} else if (args[0].equals("join")) {
			String[] args2 = Arrays.copyOfRange(args, 1, args.length);
			DART.join(args2);
		} else {
			// CommandLineParser clp = new CommandLineParser();
			// clp.addArgument('h', "Help", "Display help menue", true, false,
			// false);
			// clp.addArgument('v', "version", "Displays the version", true,
			// false, false);
			// try {
			// Map out = clp.parseArgs(test1);
			// if (out.containsKey("help")) {
			// System.out.println(clp.getHelpText());
			// } else if (out.containsKey("version")) {
			// System.out.println("DART 1.0");
			// }
			// } catch (Exception e) {
			// System.out.println(clp.getHelpText());
			// }
		}
	}

	/**
	 * join and setup the dart network
	 *
	 * @param args
	 *            list of arguments
	 */
	public static void join(String[] args) {
	}

	/**
	 * Insert certfile(s) into the network
	 *
	 * @param args
	 *            list of arguments
	 */
	public static void insert(String[] args) {
		// CommandLineParser clp = new CommandLineParser();
		//
		// try {
		// clp.addArgument('h', "Help", "Display help menue", true, false,
		// false);
		// clp.addArgument('b', "bootstrap", "the bootstrap node Address:Port",
		// true, false, false);
		// Map out = clp.parseArgs(test1);
		// if (out.containsKey("help")) {
		// System.out.println(clp.getHelpText());
		// } else if (out.containsKey("bootstrap")) {
		// Address a = new Address(args[2]);
		// RPCBuilder rpcObject = new RPCBuilder();
		// JSONObject rpc = rpcObject.buildPUT(args[0]);
		// Router router = new Router(a);
		// router.routeWithCalls(rpc);
		// } else {
		// RPCBuilder rpcObject = new RPCBuilder();
		// JSONObject rpc = rpcObject.buildPUT(args[0]);
		// Router router = new Router();
		// router.routeWithCalls(rpc);
		// }
		// } catch (Exception e) {
		// System.out.println(clp.getHelpText());
		// }

	}

	/**
	 * Finds a specific certificate in the network
	 *
	 * @param args
	 *            list of arguments
	 */
	public static void retrieve(String[] args) {
	}

	/**
	 * Used to exit the network
	 *
	 * @param args
	 *            list of arguments
	 */
	public static void leave(String[] args) {
	}
}
