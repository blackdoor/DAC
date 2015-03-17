package blackdoor.cqbe.cli;

import java.util.Arrays;
import java.util.Map;
import java.io.File;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;

import blackdoor.util.CommandLineParser;
import blackdoor.util.CommandLineParser.Argument;
import blackdoor.util.CommandLineParser.DuplicateOptionException;
import blackdoor.util.CommandLineParser.InvalidFormatException;
import blackdoor.util.DBP;
import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.CASFileAddress;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.output_logic.Router;
import blackdoor.cqbe.rpc.GETResponse;
import blackdoor.cqbe.node.Node.NodeBuilder;

public class dh256 {

	/**
	 * This is the main function that is called whenever the user ineracts with
	 * dart via cli
	 *
	 * @param args
	 *            command line args
	 */
	public static void main(String[] args) {
		DBP.DEMO = true;
		try {
			CommandLineParser clp = new CommandLineParser();
			clp.setExecutableName("dh256");
			clp.setUsageHint("\tmandatory options to long options are mandatory for short options too.\n"
					+ "The subcommands for dh256 are:\n"
					+ "\t join \n"
					+ "\t insert \n" + "\t retrieve \n" + "\t leave \n");
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
				case "-version":
					System.out.println("dh256 1.0");
					break;
				default:
					System.out.println(clp.getHelpText());
					break;
				}
			} else {
				System.out.println(clp.getHelpText());
			}
		} catch (Exception e) {
			DBP.printException(e);
		}
	}

	/**
	 * join and setup the dart network
	 *
	 * @param args
	 *            list of arguments
	 */
	public static void join(String[] args) {
		CommandLineParser clp = new CommandLineParser();
		NodeBuilder node = new NodeBuilder();
		clp.setExecutableName("dh256 join");
		try {
			clp.addArgument(new Argument().setLongOption("bootstrap")
					.setParam(true).setMultipleAllowed(false));
			clp.addArgument(new Argument().setLongOption("port").setOption("p")
					.setMultipleAllowed(false).setTakesValue(true));
			clp.addArgument(new Argument().setLongOption("adam").setOption("a")
					.setMultipleAllowed(false));
			clp.addArgument(new Argument().setLongOption("dir").setOption("d")
					.setMultipleAllowed(false).setTakesValue(true));
			clp.addArgument(new Argument().setLongOption("log").setOption("l")
					.setMultipleAllowed(false).setTakesValue(true));
			clp.addArgument(new Argument().setLongOption("settings")
					.setOption("s").setMultipleAllowed(false)
					.setTakesValue(true));
			clp.addArgument(new Argument().setLongOption("revive")
					.setOption("r").setMultipleAllowed(false)
					.setTakesValue(true));
			clp.addArgument(new Argument().setLongOption("daemon")
					.setOption("dm").setMultipleAllowed(false));
			Map<String, Argument> out = clp.parseArgs(args);
			if (out.containsKey("help")) {
				System.out.println(clp.getHelpText());
			} else {
				if (out.containsKey("settings")) {
					String settings = out.get("settings").getValues().get(0);
					node.setSettings(settings);
				}
				if (out.containsKey("port")) {
					int port = Integer.parseInt(out.get("port").getValues()
							.get(0));
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
					String[] bootstrapSplit = bootstrap.split(":");
					node.setBootstrapNode(new L3Address(InetAddress
							.getByName(bootstrapSplit[0]), Integer
							.parseInt(bootstrapSplit[1])));
				}
				node.buildNode();
			}
		} catch (DuplicateOptionException e) {
			DBP.printException(e);
		} catch (InvalidFormatException e) {
			System.out.println(clp.getHelpText());
			DBP.printException(e);
		} catch (Exception e) {
			DBP.printException(e);
		}
	}

	/**
	 * Insert file(s) into the network
	 *
	 * @param args
	 *            list of arguments
	 */
	public static void insert(String[] args) {
		CommandLineParser clp = new CommandLineParser();
		clp.setExecutableName("dh256 insert");
		try {
			clp.addArgument(new Argument().setLongOption("file").setParam(true)
					.setMultipleAllowed(false).setRequiredArg(true));
			clp.addArgument(new Argument().setLongOption("bootstrap")
					.setOption("b").setMultipleAllowed(false)
					.setTakesValue(true).setHelpText("the bootstrap node."));
			clp.addArgument(new Argument().setLongOption("help").setOption("h")
					.setMultipleAllowed(false).setTakesValue(false)
					.setHelpText("Display help menue."));
			Map<String, Argument> out = clp.parseArgs(args);
			if (out.containsKey("help")) {
				System.out.println(clp.getHelpText());
			} else {
				File file = new File(out.get("file").getValues().get(0));
				if (!existsAndReadable(file)) {
					System.out
							.println("specified file does not exist or we lack read permissions");
					return;
				}
				Router router;
				CASFileAddress fileAddress = new CASFileAddress(file);
				byte[] fileBytes = Files.readAllBytes(file.toPath());
				if (out.containsKey("bootstrap")) {
					String bootstrap = out.get("bootstrap").getValues().get(0);
					String[] bootstrapSplit = bootstrap.split(":");
					L3Address a = new L3Address(
							InetAddress.getByName(bootstrapSplit[0]),
							Integer.parseInt(bootstrapSplit[1]));
					router = Router.fromBootstrapNode(a);
				} else {
					router = Router.fromDefaultLocalNode();
				}
				int numberStored = router.put(fileAddress, fileBytes);
				if(numberStored > 0)
					System.out.println("file added successfully by "+ numberStored +" nodes with address: " + fileAddress.overlayAddressToString());
				else
					System.out.println("the file was not added");
			}
		} catch (DuplicateOptionException e) {
			DBP.printException(e);
		} catch (InvalidFormatException e) {
			System.out.println(clp.getHelpText());
			DBP.printException(e);
		} catch (Exception e) {
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
	 *            list of arguments
	 */
	public static void retrieve(String[] args) {
		CommandLineParser clp = new CommandLineParser();
		clp.setExecutableName("dh256 retrieve");
		try {
			clp.addArgument(new Argument().setLongOption("fileOAddress")
					.setParam(true).setMultipleAllowed(false)
					.setRequiredArg(true)
					.setHelpText(
							"Address of the file you want to retrive."));
			clp.addArgument(new Argument()
					.setLongOption("dir")
					.setOption("d")
					.setMultipleAllowed(false)
					.setRequiredArg(true)
					.setHelpText(
							"location for the retrieved file to be stored."));
			clp.addArgument(new Argument().setLongOption("bootstrap")
					.setOption("b").setMultipleAllowed(false)
					.setTakesValue(true).setHelpText("the bootstrap node."));
			clp.addArgument(new Argument().setLongOption("help").setOption("h")
					.setMultipleAllowed(false).setTakesValue(false)
					.setHelpText("Display help menue."));
			Map<String, Argument> parsedArgs = clp.parseArgs(args);
			if (parsedArgs.containsKey("help")) {
				System.out.println(clp.getHelpText());
			}
			if (parsedArgs.containsKey("fileoaddress")){
				Router router;
				if (parsedArgs.containsKey("bootstrap")) {
					String bootstrap = parsedArgs.get("bootstrap").getValues().get(0);
					String[] bootstrapSplit = bootstrap.split(":");
					L3Address a = new L3Address(
							InetAddress.getByName(bootstrapSplit[0]),
							Integer.parseInt(bootstrapSplit[1]));
					router = Router.fromBootstrapNode(a);
				} else {
					router = Router.fromDefaultLocalNode();
				}
				Address a = new Address(parsedArgs.get("fileoaddress").getValues().get(0));
				byte[] response = router.get(a);
				File fileDir = new File(parsedArgs.get("dir").getValue());
				new CASFileAddress(fileDir, response);
				if(existsAndReadable(fileDir))
					System.out.println("the file was retrived to the specified directory successfully");
			}
			
		} catch (DuplicateOptionException e) {
			DBP.printException(e);
		} catch (InvalidFormatException e) {
			System.out.println(clp.getHelpText());
			DBP.printException(e);
		} catch (Exception e) {
			DBP.printException(e);
		}
	}

	/**
	 * Used to exit the network
	 *
	 * @param args
	 *            list of arguments
	 */
	public static void shutdown(String[] args) {
		CommandLineParser clp = new CommandLineParser();
		clp.setExecutableName("cqbe shutdown");
		try {
			clp.addArgument(new Argument().setLongOption("port").setOption("p")
					.setMultipleAllowed(false).setRequiredArg(true)
					.setTakesValue(true)
					.setHelpText("The port of the node to be shutdown"));
			clp.addArgument(new Argument().setLongOption("help").setOption("h")
					.setTakesValue(false).setRequiredArg(false)
					.setHelpText("display this help text"));
			Map<String, Argument> out = clp.parseArgs(args);
			if (out.containsKey("help")) {
				System.out.println(clp.getHelpText());
			} else {
				int port = Integer.parseInt(out.get("port").getValues().get(0));
				Router.shutDown(port);
			}
		} catch (DuplicateOptionException e) {
			DBP.printException(e);
		} catch (InvalidFormatException e) {
			System.out.println(clp.getHelpText());
			DBP.printException(e);
		} catch (Exception e) {
			DBP.printException(e);
		}
	}
}