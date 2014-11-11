package blackdoor.cqbe.cli;

import java.util.Arrays;
import java.util.Map;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import blackdoor.util.CommandLineParser;
import blackdoor.util.CommandLineParser.Argument;
import blackdoor.util.CommandLineParser.DuplicateOptionException;
import blackdoor.util.CommandLineParser.InvalidFormatException;
import blackdoor.util.DBP;

import org.json.*;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.node.NodeBuilder;
import blackdoor.cqbe.output_logic.Router;
import blackdoor.cqbe.rpc.RPCBuilder;

public class DART {

	/**
 	* This is the main function that is called whenever the user interacts with dart via cli
 	*
 	* @param  args  command line args
 	*/
    public static void main(String[] args) {
    	if(args.length == 0){
    	    System.out.println("Proper Usage is: java DART args");
    	    System.exit(0);
    	}
    	if(args[0].equals("cert")){
    		String[] args2 = Arrays.copyOfRange(args, 1, args.length);
    		//CertificateUILogic.main(args2);
    	}
    	else if(args[0].equals("keys")){
    		String[] args2 = Arrays.copyOfRange(args, 1, args.length);
    		//KeysUILogic.main(args2);
    	}
    	else if(args[0].equals("help")){
    		String[] args2 = Arrays.copyOfRange(args, 1, args.length);
    		DART.help(args2);
    	}
		else{
			DART.help(args);
		}
    }

    /**
 	* Used to print a menu of commands, their descriptions and uses. 
 	* Can be used injunction with a specific command to get just that command’s description.
 	*
 	* @param  args list of arguments
 	*/
	public static void help(String[] args) {
	}

	/**
 	* join and setup the dart network
 	*
 	* @param  args list of arguments
 	*/
	public void join(String[] args) {
		CommandLineParser clp = new CommandLineParser();
		Router r = new Router();
		Address destination = null;
		AddressTable neighbors = r.resolveAddress(destination);
		NodeBuilder bob = new NodeBuilder(neighbors);
		
		clp.setExecutableName("cqbe join");
		try{
			clp.addArgument(new Argument().setLongOption("port")
	            .setOption("p").setMultipleAllowed(false).setTakesValue(true));
	        clp.addArgument(new Argument().setLongOption("adam")
	        	.setOption("a").setMultipleAllowed(false));
	        clp.addArgument(new Argument().setLongOption("dir")
	        	.setOption("d").setMultipleAllowed(false).setTakesValue(true));
	        clp.addArgument(new Argument().setLongOption("revive")
	        	.setOption("r").setMultipleAllowed(false).setTakesValue(true));
	        clp.addArgument(new Argument().setLongOption("daemon")
	        	.setOption("dm").setMultipleAllowed(false));
	        Map<String, Argument> out = clp.parseArgs(args);
            if(out.containsKey("help")){
                System.out.println(clp.getHelpText());
            }
            else {
            	if(out.containsKey("port")){
            		int port = Integer.parseInt((out.get("port").getValues().get(0)));
            		bob.setPort(port);
            	}
            	if(out.containsKey("adam")){
            		bob.setAdam(true);
            	}
            	if(out.containsKey("dir")){
            		String dir = out.get("dir").getValues().get(0);
            		bob.setDirectory(dir);
            	}
            	if(out.containsKey("revive")){
            		String revive = out.get("revive").getValues().get(0);
            		bob.setRevival(revive);
            	}
            	if(out.containsKey("daemon")){
            		bob.setDaemon(true);
            	}
        		bob.buildNode();
            }
		}
        catch (DuplicateOptionException e) {
            System.out.println("dups exeption");
        }
        catch (InvalidFormatException e) {
            System.out.println("invalidException");
        }
        catch (Exception e) {
            System.out.println("all other exeptions");
        }

	}

	/**
 	* Insert certfile(s) into the network
 	*
 	* @param  args list of arguments
 	*/
	public void insert(String[] args) {
	}

	/**
 	* Finds a specific certificate in the network
 	*
 	* @param  args list of arguments
 	*/
	public void retrieve(String[] args) {
	}

	/**
 	* Used to exit the network
 	*
 	* @param  args list of arguments
 	*/
	public void shutdown(String[] args) {
		CommandLineParser clp = new CommandLineParser();
		clp.setExecutableName("cqbe shutdown");
		try{
			clp.addArgument(new Argument().setLongOption("port")
				.setOption("p").setMultipleAllowed(false).setRequiredArg(true)
				.setTakesValue(true).setHelpText("The port of the node to be shutdown"));
			Map<String,Argument> out = clp.parseArgs(args);
            if(out.containsKey("help")){
                System.out.println(clp.getHelpText());
            }
            else {

            	int port = Integer.parseInt(out.get("port").getValues().get(0));
        		RPCBuilder r = new RPCBuilder();
        		JSONObject rpc = r.buildSHUTDOWN(port);
        		Router router = new Router();
        		router.routeWithCalls(rpc);
            }
		}
		 catch (DuplicateOptionException e) {
	         System.out.println("dups exeption");
	     }
	     catch (InvalidFormatException e) {
			 System.out.println("invalidException");
	     }
	     catch (Exception e) {
	    	 System.out.println("all other exeptions");
	     }
		

	}
}