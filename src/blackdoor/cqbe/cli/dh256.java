package blackdoor.cqbe.cli;

import java.util.Arrays;
import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONException;
import org.json.JSONObject;

import blackdoor.util.CommandLineParser;
import blackdoor.util.CommandLineParser.Argument;
import blackdoor.util.CommandLineParser.DuplicateOptionException;
import blackdoor.util.CommandLineParser.InvalidFormatException;
import blackdoor.util.DBP;
import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.addressing.CASFileAddress;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.filestor.FileTable;
import blackdoor.cqbe.output_logic.Router;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.cqbe.node.Node.NodeBuilder;
import blackdoor.cqbe.node.NodeException;
import blackdoor.cqbe.node.server.ServerException;
import blackdoor.util.DBP.Channel;

public class dh256 {
	
	public static void configureLogs(String logsettings) throws IOException{
		JSONObject settings = new JSONObject(new String(Files.readAllBytes(new File(logsettings).toPath())));
		for(String channel : settings.keySet()){
			try{
			DBP.getChannel(channel).setEnabled(settings.getJSONObject(channel).getBoolean("enabled"));
			}catch(Exception e){
				
			}
		}
		if(settings.has("logAll")){
			if(settings.getBoolean("logAll"))
				DBP.LOG_ALL = true;
			else DBP.LOG_ALL = false;
		}
	}

	/**
	 * This is the main function that is called whenever the user ineracts with
	 * dart via cli
	 *
	 * @param args
	 *        command line args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		configureLogs("logConfig.js");
		//DBP.DEMO = false;
		//DBP.enableChannel(DBP.DefaultChannelNames.DEV.name());//DBP.DEV = true;
		//DBP.enableChannel("LOG");
		//DBP.LOG_ALL = true;
			

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
				case "DHFS":
					dh256.DHFS(args2);
					break;
				default:
					System.out.println(clp.getHelpText());
					break;
			}
		} else {
			System.out.println(clp.getHelpText());
		}

	}

	public static void DHFS(String[] args) {
		
		CommandLineParser clp = new CommandLineParser();
		clp.setExecutableName("dh256 DHFS");
		clp.setUsageHint("The subcommands for DHFS are: \n"
						+ "mv - Move File \n"
						+ "rm - Remove File \n"
						+ "ls - List Files \n"
						+ "For more information on mv or rm type dh256 DHFS mv or dh256 DHFS rm\n"
						+ "See FS readme on GitHub for more help.");
		if (args.length > 0) {
			String[] args3 = Arrays.copyOfRange(args, 1, args.length);
			switch (args[0]) {
			case "mv":
				if(args3.length!=2){
					System.out.println("\nDHFS mv usage: dh256 DHFS mv baseFile targetFile.\n"
							+ "ex: dh256 DHFS mv ~/Documents/myLocalFile.txt /DHFS/newfile.txt\n"
							+ "See FS readme on GitHub for more info.\n");
				} else {
				try {
					dh256.dhfsMV(args3);
				} catch (IOException | RPCException | AddressException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				break;
			case "rm":
				if(args3.length!=1){
					System.out.println("\nDHFS rm usage: dh256 DHFS rm /DHFS/targetFile \n"
							+ "ex: dh256 DHFS rm /DHFS/newfile.txt\n"
							+ "See FS readme on GitHub for more info.\n");
				} else {
				try {
					dh256.dhfsRM(args3);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				break;
			case "ls":
				if(args3.length>1){
					System.out.println("\nDHFS ls usage: dh256 DHFS ls baseDirectory or dh256 DHFS ls \n"
							+ "ex: dh256 DHFS ls DHFS/importantFiles\n"
							+ "See FS readme on GitHub for more info.\n");
				}
				try {
					dh256.dhfsLS(args3);
				} catch (JSONException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				System.out.println(clp.getHelpText());
				break;
			}
		} else {
			System.out.println(clp.getHelpText());
		}
	}
	
	public static void dhfsMV(String[] args) throws IOException, RPCException, AddressException {
		Path baseFile = Paths.get(args[0]);
		Path targetFile = Paths.get(args[1]);
		File fStore = new File("default.filestore");
		if( baseFile.toString().length()<5 || targetFile.toString().length()<5){
			System.out.println("Invalid file paths");
			return;
		}
		Boolean baseDHFS = baseFile.toString().substring(0, 5).equals("/DHFS");
		Boolean targetDHFS = targetFile.toString().substring(0,5).equals("/DHFS");
		byte[] response = null;
		Router router = null;
		Address get = null;
		router = Router.fromDefaultLocalNode();
		if(baseDHFS && targetDHFS){
			//DHFS Reorganization
			
			File lockFile = new File("default.filestore.lock");
			while(!lockFile.renameTo(lockFile)){
				
			}
			
			RandomAccessFile rafLock = new RandomAccessFile(lockFile,"rw");
			FileChannel fc = rafLock.getChannel();
			FileLock lock = fc.lock();
			
			
			File temp = new File("temp.txt");
			BufferedReader reader = new BufferedReader(new FileReader(fStore));
			BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
			
			JSONObject ft = new JSONObject(reader.readLine());
			String value = ft.getString(baseFile.toString());
			ft.remove(baseFile.toString());
			ft.put(targetFile.toString(), value);
			writer.write(ft.toString() + System.getProperty("line.seperator"));
			writer.close();
			reader.close();
			Boolean success = temp.renameTo(fStore);
			
			reader.close();
			writer.close();
			lock.release();
			fc.close();
			rafLock.close();
		}
		else if(baseDHFS && !targetDHFS){
			//DHFS to Disk
			get = FileTable.getEntry(fStore, baseFile.toString());
			if(get.equals(null)){
				System.out.println("File path not found in filestore!");
				return;
			} else {
				response = router.get(get);
			}
			if(response.equals(null)){
				System.out.println("Something here is fuckey.");
			}
			File f = new File(targetFile.toString());
			Files.write(f.toPath(), response, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
			FileTable.removeEntry(fStore, baseFile.toString());

		}
		else if(!baseDHFS && targetDHFS){
			//Disk to DHFS
			File f = new File(baseFile.toString());
			CASFileAddress fa = new CASFileAddress(f);
			if(!f.exists()){
				System.out.println("File path not found!");
				return;
			} else {
				FileTable.setEntry(fStore, targetFile.toString(), fa);
				router.put(fa, fa.getBinary());
			}
		}
		else{
			//You prick
			System.out.println("DH256 is not meant to be used as a basic mv command.");
		}
	}
	
	public static void dhfsRM(String[] args) throws IOException {
		Path toRemove = Paths.get(args[0]);
		File fStore = new File("default.filestore");
		if(!toRemove.toString().substring(0,5).equals("/DHFS") || toRemove.toString().length()<5){
			System.out.println("DH256 is not meant to be used as a basic rm command.");
		} else {
			FileTable.removeEntry(fStore, toRemove.toString());
		}
	}
	
	public static void dhfsLS(String[] args) throws JSONException, IOException {
		File fStore = new File("default.filestore");
		if(args.length!=0){
			FileTable.listEntries(fStore,args[0]);
		} else {
			FileTable.listEntries(fStore);
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
