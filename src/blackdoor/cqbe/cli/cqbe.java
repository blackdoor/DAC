package blackdoor.cqbe.cli;

import java.util.Arrays;
import java.util.Map;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

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

public class cqbe {

	/**
 	* This is the main function that is called whenever the user ineracts with dart via cli
 	*
 	* @param  args  command line args
 	*/
    public static void main(String[] args) {
    	if(args.length == 0){
    	    System.out.println("Proper Usage is: java cqbe args");
    	    System.exit(0);
    	}
    	if(args[0].equals("cert")){
    		String[] args2 = Arrays.copyOfRange(args, 1, args.length);
    		Certificate.main(args2);
    	}
    	else if(args[0].equals("keys")){
    		String[] args2 = Arrays.copyOfRange(args, 1, args.length);
    		Keys.main(args2);
    	}
    	else if(args[0].equals("insert")){
    		String[] args2 = Arrays.copyOfRange(args, 1, args.length);
    		cqbe.insert(args2);
    	}
        else if(args[0].equals("retrieve")){
            String[] args2 = Arrays.copyOfRange(args, 1, args.length);
            cqbe.retrieve(args2);
        }else if(args[0].equals("leave")){
            String[] args2 = Arrays.copyOfRange(args, 1, args.length);
            cqbe.leave(args2);
        }
        else if(args[0].equals("join")){
            String[] args2 = Arrays.copyOfRange(args, 1, args.length);
            cqbe.join(args2);
        }
		else{
            CommandLineParser clp = new CommandLineParser();
            clp.setExecutableName("cqbe");
            clp.setUsageHint("\tmandatory options to long options are mandatory for short options too.\n"
                + "The subcommands for cqbe are:\n"
                + "\t certificate \n"
                + "\t keys \n"
                + "\t join \n"
                + "\t insert \n"
                + "\t retrieve \n"
                + "\t leave \n");
            try {
                clp.addArgument(new Argument().setLongOption("subcommand")
                    .setParam(true).setMultipleAllowed(false).setRequiredArg(false)
                    .setTakesValue(false)
                    .setHelpText("the subcommand of certificate to execute."));
                clp.addArgument(new Argument().setLongOption("version")
                    .setOption("v").setMultipleAllowed(false).setTakesValue(false)
                    .setHelpText("Displays the version."));
                clp.addArgument(new Argument().setLongOption("help")
                    .setOption("h").setMultipleAllowed(false).setTakesValue(false)
                    .setHelpText("Display help menue."));
                Map out = clp.parseArgs(args);
                if(out.containsKey("version")){
                    System.out.println("cqbe 1.0");
                }
                else{
                    System.out.println(clp.getHelpText());
                }
            } 
            catch (DuplicateOptionException e) {
                System.out.println("dups exeption");
            }
            catch (InvalidFormatException e) {
                System.out.println("invalidException");
            }
        }
    }

	/**
	 * join and setup the dart network
	 *
	 * @param args
	 *            list of arguments
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
 	* @param  args list of arguments
 	*/
	public static void insert(String[] args) {
        CommandLineParser clp = new CommandLineParser();
        clp.setExecutableName("cqbe insert");
        try {
            clp.addArgument(new Argument().setLongOption("file")
                .setParam(true).setMultipleAllowed(false).setRequiredArg(true));
            clp.addArgument(new Argument().setLongOption("bootstrap")
                .setOption("b").setMultipleAllowed(false).setTakesValue(true)
                .setHelpText("the bootstrap node."));
            clp.addArgument(new Argument().setLongOption("help")
                .setOption("h").setMultipleAllowed(false).setTakesValue(false)
                .setHelpText("Display help menue."));
            Map<String, Argument> out = clp.parseArgs(args);
            if(out.containsKey("help")){
                System.out.println(clp.getHelpText());
            }
            else {
                File file = new File(out.get("file").getValue());
                if(!existsAndReadable(file)){
                    System.out.println("specified file does not exist or we lack read permissions");
                    return;
                }
                if(out.containsKey("bootstrap")){
                    Address a = new Address(out.get("bootstrap").getValues().get(0));
                    RPCBuilder rpcObject = new RPCBuilder();
                    JSONObject rpc = rpcObject.buildPUT(file);
                    Router router = new Router(a);
                    router.routeWithCalls(rpc);
                }
                else{
                    RPCBuilder rpcObject = new RPCBuilder();
                    JSONObject rpc = rpcObject.buildPUT(file);
                    Router router = new Router();
                    router.routeWithCalls(rpc);
                }
            }
        } 
        catch (DuplicateOptionException e) {
            System.out.println("dups exeption");
        }
        catch (InvalidFormatException e) {
            System.out.println("invalidException");
        }
        catch (Exception e){
            System.out.println("all other exeptions");
        }
	}

    private static boolean existsAndReadable(File f){
        Path file = f.toPath();
        return Files.isRegularFile(file) &
                 Files.isReadable(file);
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
		// CrazyIvan
		// RPCBuilder r = new RPCBuilder();
		// r.buildSHUTDOWN();

	}
}
