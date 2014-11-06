package blackdoor.cqbe.cli;

import java.util.Arrays;

import blackdoor.util.CommandLineParser;
import blackdoor.util.CommandLineParser.Argument;
import blackdoor.util.CommandLineParser.DuplicateOptionException;

public class Certificate {
	
	public static void main(String[] args) {
		CommandLineParser parser = getParser();
		System.out.println(parser.getHelpText());
		
		if(args.length > 0){
			switch(args[0]){
				case "create":	create(Arrays.copyOfRange(args, 1, args.length-1));	
								break;
				case "endorse":	endorse(Arrays.copyOfRange(args, 1, args.length-1));	
								break;
				case "check":	check(Arrays.copyOfRange(args, 1, args.length-1));	
								break;
				case "sign":	sign(Arrays.copyOfRange(args, 1, args.length-1));	
								break;
			}
		}
	}
	
	private static CommandLineParser getParser(){
		CommandLineParser parser = new CommandLineParser();
		parser.setExecutableName("cqbe certificate");
		parser.setUsageHint("mandatory options to long options are mandatory for short options too.\n"
				+ "");
		Argument subcommand = new Argument().setLongOption("<subcommand>")
				.setParam(true).setMultipleAllowed(false).setRequiredArg(true)
				.setTakesValue(false)
				.setHelpText("the subcommand of certificate to execute.")
				.addValue("create");
		Argument helpArgument = new Argument().setLongOption("help")
				.setOption("h").setMultipleAllowed(false).setTakesValue(false)
				.setHelpText("print this help.");
		try {
			parser.addArgument(subcommand);
			parser.addArgument(helpArgument);
		} catch (DuplicateOptionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parser;
	}

	private static void check(String[] copyOfRange) {
		// TODO Auto-generated method stub
		
	}

	public Certificate(String[] args) {
	}

	/**
 	* create a certificate
 	*
 	* @param  args list of arguments
 	*/
	public static void create(String[] args) {
	}

	/**
 	* endorses a certificate
 	*
 	* @param  args list of arguments
 	*/
	public static void endorse(String[] args) {
	}

	/**
 	* sign a file
 	*
 	* @param  args list of arguments
 	*/
	public static void sign(String[] args) {
	}

	/**
 	* verify a certificate based on endocment
 	*
 	* @param  args list of arguments
 	*/
	public void verify(String[] args) {
	}
}
