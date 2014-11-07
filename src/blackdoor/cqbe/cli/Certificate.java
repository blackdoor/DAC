package blackdoor.cqbe.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.KeyException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Map;

import blackdoor.cqbe.certificate.BuilderException;
import blackdoor.cqbe.certificate.CertificateBuilder;
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
	
	private static CommandLineParser getCreateParser(){
		CommandLineParser parser = new CommandLineParser();
		parser.setUsageHint("");
		parser.setExecutableName("cqbe certificate create");
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
		return parser;
	}

	private static void check(String[] args) {
		
		
	}

	public Certificate(String[] args) {
	}

	/**
 	* create a certificate
 	*
 	* @param  args list of arguments
 	*/
	public static void create(String[] args) {
		CommandLineParser parser = getCreateParser();
		Map<String, Argument> parsedArgs;
		File descriptorFile = null;
		File keyFile = null;
		File outputFile;
		CertificateBuilder builder;
		PrivateKey key;
		byte[] cert = null;
		try {
			parsedArgs = parser.parseArgs(args);
			descriptorFile = new File(parsedArgs.get("descriptor").getValues().get(0));
			keyFile = new File(parsedArgs.get("key-file").getValues().get(0));
			outputFile = new File(parsedArgs.get("output").getValues().get(0));
		} catch (InvalidFormatException e) {
			DBP.printerrorln(e.getMessage());
			for(StackTraceElement elem : e.getStackTrace()){
				DBP.printerrorln(elem);
			}
			System.out.println(parser.getHelpText());
			return;
		}
		try{
			if(!descriptorFile.isFile() || !descriptorFile.exists()){
				System.out.println("Invalid descriptor file.");
				return;
			}if (!keyFile.exists() || !keyFile.isFile()){
				System.out.println("Invalid key file.");
				return;
			}
		}catch (NullPointerException e){
			DBP.printerrorln(e.getMessage());
			for(StackTraceElement elem : e.getStackTrace()){
				DBP.printerrorln(elem);
			}
		}
		// all input is checked and good after this
		key = null;//TODO need a way to get a key file to a key object
		builder = new CertificateBuilder(descriptorFile);
		try {
			cert = builder.build(key);
		} catch (KeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Files.write(outputFile.toPath(), cert, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
