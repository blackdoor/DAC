package blackdoor.cqbe.cli;

import java.util.Arrays;
import java.util.Map;

import blackdoor.util.CommandLineParser;
import blackdoor.util.CommandLineParser.Argument;
import blackdoor.util.CommandLineParser.DuplicateOptionException;
import blackdoor.util.CommandLineParser.InvalidFormatException;
import blackdoor.util.DBP;

public class Certificate {
	
	public static void main(String[] args){
		DBP.DEBUG = true;
		DBP.ERROR = true;
		args = new String[]{"check"};
		CommandLineParser parser = getParser();
		//DBP.printdebugln(parser.getHelpText());
		Map<String, Argument> parsedArgs;
		try {
			parsedArgs = parser.parseArgs(args);
			if(parsedArgs.containsKey("subcommand")){
				switch(args[0]){
					case "create":	create(Arrays.copyOfRange(args, 1, args.length-1));	
									break;
					case "endorse":	endorse(Arrays.copyOfRange(args, 1, args.length-1));	
									break;
					case "check":	check(Arrays.copyOfRange(args, 1, args.length));	
									break;
					case "sign":	sign(Arrays.copyOfRange(args, 1, args.length-1));	
									break;
				}
			}else{
				System.out.println(parser.getHelpText());
			}
		} catch (InvalidFormatException e) {
			System.out.println(parser.getHelpText());
			DBP.printerrorln(e.getMessage());
			for(StackTraceElement elem : e.getStackTrace()){
				DBP.printerrorln(elem);
			}
		}
	}
	
	private static CommandLineParser getParser(){
		CommandLineParser parser = new CommandLineParser();
		parser.setExecutableName("cqbe certificate");
		parser.setUsageHint("\tmandatory options to long options are mandatory for short options too.\n"
				+ "The subcommands for cqbe certificate are:\n"
				+ "\t create \n"
				+ "\t endorse \n"
				+ "\t sign \n"
				+ "\t check \n");
		Argument subcommand = new Argument().setLongOption("subcommand")
				.setParam(true).setMultipleAllowed(false).setRequiredArg(false)
				.setTakesValue(false)
				.setHelpText("the subcommand of certificate to execute.");
		//DBP.printdebugln(subcommand);
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

	private static void check(String[] args) {
		CommandLineParser parser = new CommandLineParser();
		parser.setExecutableName("cqbe certificate check");
		Argument descriptor = new Argument().setLongOption("descriptor")
				.setOption("d").setRequiredArg(true).setMultipleAllowed(false)
				.setHelpText("descriptor FILE to populate certificate")
				.setTakesValue(true).setValueHint("FILE")
				.setValueRequired(true);
		Argument keyFile = new Argument().setLongOption("key-file")
				.setOption("k")
				.setHelpText("use PKCS#8 key file when creating a certificate")
				.setValueHint("FILE").setMultipleAllowed(false)
				.setRequiredArg(true).setValueRequired(true);
		Argument output = new Argument().setLongOption("output").setOption("o")
				.setHelpText("location for certificate to be saved")
				.setMultipleAllowed(false).setTakesValue(true)
				.setValueRequired(false);
		try {
			parser.addArguments(new Argument[]{descriptor, keyFile, output});
		} catch (DuplicateOptionException e) {
			DBP.printerrorln(e.getMessage());
			for(StackTraceElement elem : e.getStackTrace()){
				DBP.printerrorln(elem);
			}
		}
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
	//public void verify(String[] args) {
	//}
}
