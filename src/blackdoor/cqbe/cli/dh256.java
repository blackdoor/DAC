package blackdoor.cqbe.cli;

import java.util.Arrays;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;

import blackdoor.util.CommandLineParser;
import blackdoor.util.CommandLineParser.Argument;
import blackdoor.util.CommandLineParser.DuplicateOptionException;
import blackdoor.util.CommandLineParser.InvalidFormatException;
import blackdoor.util.DBP;
import blackdoor.util.DBP.SingletonAlreadyInitializedException;
import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.addressing.CASFileAddress;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.output_logic.Router;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.cqbe.node.Node.NodeBuilder;
import blackdoor.cqbe.node.NodeException;
import blackdoor.cqbe.node.server.ServerException;

public class dh256 {

	/**
	 * This is the main function that is called whenever the user ineracts with
	 * dart via cli
	 *
	 * @param args
	 *        command line args
	 */
	public static void main(String[] args) {
		DBP.DEMO = false;
		DBP.DEV = true;
		DBP.LOG_ALL = true;

		CommandLineParser clp = new CommandLineParser();
		clp.setExecutableName("dh256");
		clp.setUsageHint("\tmandatory options for long options are also"
				+ "mandatory for short option variants.\n" + "The subcommands for dh256 are:\n"
				+ "\t join \n" + " \t insert \n " + "\t retrieve \n" + "\t shutdown \n");
		if (args.length > 0) {
			String[] args2 = Arrays.copyOfRange(args, 1, args.length);
			switch (args[0]) {
				case "insert":
					dh256.insert(args2);
					break;
				case "retrieve":
					dh256.retrieve(args2);
					break;
				case "shutdown":
					dh256.shutdown(args2);
					break;
				case "join":
					dh256.join(args2);
					break;
				case "version":
					System.out.println("dh256 v0.1");
					break;
				default:
					System.out.println(clp.getHelpText());
					break;
			}
		} else {
			System.out.println(clp.getHelpText());
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
		NodeBuilder node = new NodeBuilder();
		clp.setExecutableName("dh256 join");
		try {
			clp.addArgument(new Argument()
					.setLongOption("bootstrap")
					.setParam(true)
					.setMultipleAllowed(false)
					.setHelpText(
							"Joins an already running node at a specific address and port."
									+ "Format: <IP Address>:<Port>"));
			clp.addArgument(new Argument()
					.setLongOption("port")
					.setOption("p")
					.setMultipleAllowed(false)
					.setTakesValue(true)
					.setHelpText(
							"Specifies the port that you would like your "
									+ "node to use when joining the network."));
			clp.addArgument(new Argument().setLongOption("adam").setOption("a")
					.setMultipleAllowed(false)
					.setHelpText("Joins the network as the first node in the network."));
			clp.addArgument(new Argument().setLongOption("dir").setOption("d")
					.setMultipleAllowed(false).setTakesValue(true)
					.setHelpText("Sets a location for a running node's storage."));
			clp.addArgument(new Argument().setLongOption("log").setOption("l")
					.setMultipleAllowed(false).setTakesValue(true)
					.setHelpText("Sets a location to save a running node's log."));
			clp.addArgument(new Argument()
					.setLongOption("settings")
					.setOption("s")
					.setMultipleAllowed(false)
					.setTakesValue(true)
					.setHelpText(
							"Joins the network using user-specfied options."
									+ " Defaults are used for missing values."));
			clp.addArgument(new Argument()
					.setLongOption("revive")
					.setOption("r")
					.setMultipleAllowed(false)
					.setTakesValue(true)
					.setHelpText(
							"Starts a node using the saved data " + "and settings of a previous."));
			clp.addArgument(new Argument().setLongOption("daemon").setOption("dm")
					.setMultipleAllowed(false)
					.setHelpText("Forks the node's operation to the backround."));
			clp.addArgument(new Argument().setLongOption("check").setOption("c")
					.setMultipleAllowed(false)
					.setHelpText("Checks the input without running the action."));
			Map<String, Argument> out = clp.parseArgs(args);

			if (out.containsKey("help")) {
				System.out.println(clp.getHelpText());
			} else {
				if (out.containsKey("settings")) {
					String settings = out.get("settings").getValues().get(0);
					File file = new File(settings);
					if (!existsAndReadable(file)) {
						System.out
								.println("specified settings file does not exist or we lack read permissions");
						return;
					} else
						node.setSettings(settings);
				}
				if (out.containsKey("port")) {
					int port = Integer.parseInt(out.get("port").getValues().get(0));
					node.setPort(port);
				}
				if (out.containsKey("adam")) {
					node.setAdam(true);
				}
				if (out.containsKey("dir")) {
					String dir = out.get("dir").getValues().get(0);
					node.setStorageDir(dir);
				}
				if (out.containsKey("log")) {
					String log = out.get("log").getValues().get(0);
					node.setLogDir(log);
				}
				if (out.containsKey("daemon")) {
					node.setDaemon(true);
				}
				if (out.containsKey("bootstrap")) {

					String bootstrap = out.get("bootstrap").getValues().get(0);
					if (bootstrap.contains(":")) {
						String[] bootstrapSplit = bootstrap.split(":");
						node.setBootstrapNode(new L3Address(InetAddress
								.getByName(bootstrapSplit[0]), Integer.parseInt(bootstrapSplit[1])));
					} else
						throw new InvalidFormatException("Bootstrap ' " + bootstrap
								+ " ' is formatted incorrectly. ");

				}
				if (!out.containsKey("check"))
					node.buildNode();

			}
		} catch (DuplicateOptionException e) {
			DBP.printException(e);
		} catch (InvalidFormatException e) {
			System.out.println(clp.getHelpText());
			DBP.printException(e);
		} catch (NumberFormatException e) {
			DBP.printException(e);
		} catch (UnknownHostException e) {
			DBP.printException(e);
		} catch (NodeException e) {
			DBP.printException(e);
		} catch (ServerException e) {
			DBP.printException(e);
		} catch (SingletonAlreadyInitializedException e) {
			DBP.printException(e);
		} catch (IOException e) {
			DBP.printException(e);
		}
	}

	/**
	 * Insert file(s) into the network
	 *
	 * @param args
	 *        list of arguments
	 */
	public static void insert(String[] args) {
		CommandLineParser clp = new CommandLineParser();
		clp.setExecutableName("dh256 insert");
		try {
			clp.addArgument(new Argument().setLongOption("file").setParam(true)
					.setMultipleAllowed(false).setRequiredArg(true));
			clp.addArgument(new Argument().setLongOption("bootstrap").setOption("b")
					.setMultipleAllowed(false).setTakesValue(true)
					.setHelpText("the bootstrap node."));
			clp.addArgument(new Argument().setLongOption("help").setOption("h")
					.setMultipleAllowed(false).setTakesValue(false)
					.setHelpText("Display help menue."));
			clp.addArgument(new Argument().setLongOption("check").setOption("c")
					.setMultipleAllowed(false)
					.setHelpText("Checks the input without running the action."));
			Map<String, Argument> out = clp.parseArgs(args);
			if (out.containsKey("help")) {
				System.out.println(clp.getHelpText());
			} else {
				File file = new File(out.get("file").getValues().get(0));
				if (!existsAndReadable(file)) {
					System.out.println("specified file does not exist or we lack read permissions");
					return;
				}
				if (!out.containsKey("check")) {

					Router router;
					CASFileAddress fileAddress = new CASFileAddress(file);
					byte[] fileBytes = Files.readAllBytes(file.toPath());
					if (out.containsKey("bootstrap")) {
						String bootstrap = out.get("bootstrap").getValues().get(0);
						router = Router.fromBootstrapNode(getAddressfromArgument(bootstrap));
					} else {
						router = Router.fromDefaultLocalNode();
					}
					int numberStored = router.put(fileAddress, fileBytes);
					if (numberStored > 0)
						System.out.println("file with address <"
								+ fileAddress.overlayAddressToString() + "> stored by"
								+ numberStored + " nodes");
					else
						System.out.println("the file was not added");
				}
			}
		} catch (DuplicateOptionException e) {
			DBP.printException(e);
		} catch (InvalidFormatException e) {
			System.out.println(clp.getHelpText());
			DBP.printException(e);
		} catch (IOException e) {
			DBP.printException(e);
		} catch (RPCException e) {
			DBP.printException(e);
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
		clp.setExecutableName("dh256 retrieve");
		try {
			clp.addArgument(new Argument().setLongOption("fileOAddress").setParam(true)
					.setMultipleAllowed(false).setRequiredArg(true)
					.setHelpText("Address of the file you want to retrive."));
			clp.addArgument(new Argument().setLongOption("dir").setOption("d")
					.setMultipleAllowed(false).setRequiredArg(true)
					.setHelpText("location for the retrieved file to be stored."));
			clp.addArgument(new Argument().setLongOption("bootstrap").setOption("b")
					.setMultipleAllowed(false).setTakesValue(true)
					.setHelpText("the bootstrap node. Format: <IP Address>:<Port>"));
			clp.addArgument(new Argument().setLongOption("help").setOption("h")
					.setMultipleAllowed(false).setTakesValue(false)
					.setHelpText("Display help menu."));
			clp.addArgument(new Argument().setLongOption("check").setOption("c")
					.setMultipleAllowed(false)
					.setHelpText("Checks the input without running the action."));
			Map<String, Argument> parsedArgs = clp.parseArgs(args);
			if (parsedArgs.containsKey("help")) {
				System.out.println(clp.getHelpText());
			}
			if (parsedArgs.containsKey("fileoaddress")) {
				Router router;
				if (parsedArgs.containsKey("bootstrap")) {

					String bootstrap = parsedArgs.get("bootstrap").getValues().get(0);
					router = Router.fromBootstrapNode(getAddressfromArgument(bootstrap));
				} else {
					router = Router.fromDefaultLocalNode();
				}
				Address a = new Address(parsedArgs.get("fileoaddress").getValues().get(0));
				if (!parsedArgs.containsKey("check")) {
					byte[] response = router.get(a);
					File fileDir = new File(parsedArgs.get("dir").getValues().get(0));
					new CASFileAddress(fileDir, response);
					if (existsAndReadable(fileDir))
						System.out.println("the file was retrived to the "
								+ "specified directory successfully");
				}
			} else {

				throw new InvalidFormatException("Missing required fileOAddress argument.");
			}

		} catch (DuplicateOptionException e) {
			DBP.printException(e);
		} catch (InvalidFormatException e) {
			System.out.println(clp.getHelpText());
			DBP.printException(e);
		} catch (NumberFormatException e) {
			DBP.printException(e);
		} catch (UnknownHostException e) {
			DBP.printException(e);
		} catch (IOException e) {
			DBP.printException(e);
		} catch (RPCException e) {
			DBP.printException(e);
		} catch (AddressException e) {
			DBP.printException(e);
		}
	}

	private static L3Address getAddressfromArgument(String bootstrap) throws NumberFormatException,
			UnknownHostException, InvalidFormatException {
		if (!bootstrap.contains(":")) {
			throw new InvalidFormatException("Bootstrap ' " + bootstrap
					+ " ' is formatted incorrectly. ");
		}
		String[] bootstrapSplit = bootstrap.split(":");
		L3Address a =
				new L3Address(InetAddress.getByName(bootstrapSplit[0]),
						Integer.parseInt(bootstrapSplit[1]));
		return a;
	}

	/**
	 * Used to exit the network
	 *
	 * @param args
	 *        list of arguments
	 */
	public static void shutdown(String[] args) {
		CommandLineParser clp = new CommandLineParser();
		clp.setExecutableName("dh256 shutdown");
		try {
			clp.addArgument(new Argument().setLongOption("port").setOption("p")
					.setMultipleAllowed(false).setRequiredArg(true).setTakesValue(true)
					.setHelpText("The port of the node to be shutdown"));
			clp.addArgument(new Argument().setLongOption("help").setOption("h")
					.setTakesValue(false).setRequiredArg(false)
					.setHelpText("display this help text"));
			clp.addArgument(new Argument().setLongOption("check").setOption("c")
					.setMultipleAllowed(false)
					.setHelpText("Checks the format of input without running the action."));
			Map<String, Argument> out = clp.parseArgs(args);
			if (out.containsKey("help")) {
				System.out.println(clp.getHelpText());
			} else {
				int port = Integer.parseInt(out.get("port").getValues().get(0));
				if (!out.containsKey("check"))
					Router.shutDown(port);
			}
		} catch (DuplicateOptionException e) {
			DBP.printException(e);
		} catch (InvalidFormatException e) {
			System.out.println(clp.getHelpText());
			DBP.printException(e);
		} catch (IOException e) {
			DBP.printException(e);
		}
	}
}
