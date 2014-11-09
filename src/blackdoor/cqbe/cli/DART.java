package blackdoor.cqbe.cli;

import java.util.Arrays;
import java.util.Map;

import blackdoor.util.CommandLineParser;
import blackdoor.util.CommandLineParser.Argument;
import blackdoor.util.CommandLineParser.DuplicateOptionException;
import blackdoor.util.CommandLineParser.InvalidFormatException;
import blackdoor.util.DBP;

public class DART {

	/**
 	* This is the main function that is called whenever the user ineracts with dart via cli
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
    		CertificateUILogic.main(args2);
    		main.Certificate(args2);
    	}
    	else if(args[0].equals("keys")){
    		String[] args2 = Arrays.copyOfRange(args, 1, args.length);
    		KeysUILogic.main(args2);
    	}
    	else if(args[0].equals("insert")){
    		String[] args2 = Arrays.copyOfRange(args, 1, args.length);
    		DART.join(args2);
    	}
        else if(args[0].equals("retrieve")){
            String[] args2 = Arrays.copyOfRange(args, 1, args.length);
            DART.join(args2);
        }else if(args[0].equals("leave")){
            String[] args2 = Arrays.copyOfRange(args, 1, args.length);
            DART.join(args2);
        }
        else if(args[0].equals("join")){
            String[] args2 = Arrays.copyOfRange(args, 1, args.length);
            DART.join(args2);
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
                if(out.containsKey("help")){
                    System.out.println(clp.getHelpText());
                }
                else if(out.containsKey("version")){
                    System.out.println("DART 1.0");
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
 	* @param  args list of arguments
 	*/
	public static void join(String[] args) {
	}

	/**
 	* Insert certfile(s) into the network
 	*
 	* @param  args list of arguments
 	*/
	public static void insert(String[] args) {/*
        CommandLineParser clp = new CommandLineParser();
        clp.addArgument('h', "Help", "Display help menue", True, False, False);
        clp.addArgument('b', "bootstrap", "the bootstrap node Address:Port", True, True, False);
        try {
            Map out = clp.parseArgs(test1);
            if(out.containsKey("help")){
                System.out.println(clp.getHelpText());
            }
            else if(out.containsKey("bootstrap")){
                Address a = new Address(args[2]);
                RPCBuilder rpcObject = new RPCBuilder();
                JSONObject rpc = rpcObject.buildPUT(args[0]);
                Router router = new Router(a);
                router.routeWithCalls(rpc);
            }
            else{
                RPCBuilder rpcObject = new RPCBuilder();
                JSONObject rpc = rpcObject.buildPUT(args[0]);
                Router router = new Router();
                router.routeWithCalls(rpc);
            }
        } 
        catch (Exception e){
            System.out.println(clp.getHelpText());
        }*/

	}

	/**
 	* Finds a specific certificate in the network
 	*
 	* @param  args list of arguments
 	*/
	public static void retrieve(String[] args) {
	}

	/**
 	* Used to exit the network
 	*
 	* @param  args list of arguments
 	*/
	public static void leave(String[] args) {
	}
}
