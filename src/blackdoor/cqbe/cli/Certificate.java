package blackdoor.cqbe.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.KeyException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Map;

import blackdoor.cqbe.certificate.BuilderException;
import blackdoor.cqbe.certificate.CertificateBuilder;
import blackdoor.cqbe.certificate.CertificateProtos;
import blackdoor.cqbe.certificate.CertificateValidator;
import blackdoor.cqbe.certificate.EndorsementValidator;
import blackdoor.cqbe.certificate.Validator;
import blackdoor.util.CommandLineParser;
import blackdoor.util.CommandLineParser.Argument;
import blackdoor.util.CommandLineParser.DuplicateOptionException;
import blackdoor.util.CommandLineParser.InvalidFormatException;
import blackdoor.util.DBP;

public class Certificate {
	
	
	public static void main(String[] args) {
		DBP.DEBUG = true;
		DBP.ERROR = true;
		CommandLineParser parser = getParser();

		// DBP.printdebugln(parser.getHelpText());

		Map<String, Argument> parsedArgs;
		if (args.length > 0) {
			try {
				switch (args[0]) {
				case "create":
					create(Arrays.copyOfRange(args, 1, args.length - 1));
					break;
				case "endorse":
					endorse(Arrays.copyOfRange(args, 1, args.length - 1));
					break;
				case "check":
					check(Arrays.copyOfRange(args, 1, args.length));
					break;
				case "sign":
					sign(Arrays.copyOfRange(args, 1, args.length - 1));
					break;
				default:
					parsedArgs = parser.parseArgs(args);
					DBP.printdebugln(parsedArgs);
					System.out.println(parser.getHelpText());
					break;
				}
			} catch (InvalidFormatException e) {
				System.out.println(parser.getHelpText());
				DBP.printException(e);
			}
		} else {
			System.out.println(parser.getHelpText());
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
			DBP.printException(e);
		}
		return parser;
	}
	
	private static CommandLineParser getCheckParser(){
		CommandLineParser parser = new CommandLineParser();
		parser.setUsageHint("");
		parser.setExecutableName("cqbe certificate check");
		Argument subject = new Argument().setLongOption("subject")
				.setParam(true).setMultipleAllowed(false).setRequiredArg(true);
		Argument issuer = new Argument()
				.setLongOption("issuer")
				.setOption("i")
				.setMultipleAllowed(false)
				.setValueRequired(true)
				.setHelpText(
						"the certificate of the issuer of an endorsement to check, endorsement must also be set")
				.setValueHint("FILE");
		Argument endorsement = new Argument()
		.setLongOption("endorsement")
		.setOption("e")
		.setMultipleAllowed(false)
		.setValueRequired(true)
		.setHelpText(
				"the endorsement file to check, issuer must also be set")
		.setValueHint("FILE");
		try {
			parser.addArguments(new Argument[]{subject, issuer, endorsement});
		} catch (DuplicateOptionException e) {
			DBP.printException(e);
		}
		return parser;
	}
	
	/**
	 * 
	 * @param args
	 * @throws InvalidFormatException 
	 */
	private static void check(String[] args) throws InvalidFormatException {
		//ugh, so much boilerplate file checking in here.
		CommandLineParser parser;
		File issuer;
		File endorsement;
		File subject;
		Validator validator;
		CertificateProtos.Certificate issuerCert;
		CertificateProtos.Certificate subjectCert;
		CertificateProtos.Endorsement endorsementObj;
		Map<String, Argument> parsedArgs;
		parser = getCheckParser();
		parsedArgs = parser.parseArgs(args);
		subject = new File(parsedArgs.get("issuer").getValue());
		
		if((parsedArgs.containsKey("issuer") && !parsedArgs.containsKey("endorsement")) || (!parsedArgs.containsKey("issuer") && parsedArgs.containsKey("endorsement"))){
			DBP.printdebugln("issuer present: " + parsedArgs.containsKey("issuer") + "\nendorsement present: " + parsedArgs.containsKey("endorsement"));
			System.out.println("issuer and endorsement must both be present." + parser.getHelpText());
			return;
		}
		
		//get cert object from subject file
		if(!existsAndReadable(subject)){
			System.out.println("specified subject certificate file does not exist or we lack read permissions");
			return;
		}
		try (FileInputStream subjectStream = new FileInputStream(subject)){
			subjectCert = CertificateProtos.Certificate.parseFrom(subjectStream);;
		} catch (FileNotFoundException e) {
			DBP.printException(e);
			System.out.println("could not find subject file");
			return;
		} catch (IOException e) {
			DBP.printException(e);
			System.out.println("unable to build objects from subject cert file");
			return;
		}
		
		if(parsedArgs.containsKey("issuer")){
			endorsement = new File(parsedArgs.get("endorsement").getValue());
			issuer = new File(parsedArgs.get("issuer").getValue());
			if(!existsAndReadable(issuer)){
				System.out.println("specified issuer certificate file does not exist or we lack read permissions");
				return;
			}
			if(!existsAndReadable(endorsement)){
				System.out.println("specified endorsement file does not exist or we lack read permissions");
				return;
			}
			try (
				FileInputStream issuerStream = new FileInputStream(issuer);
				FileInputStream endorsementStream = new FileInputStream(endorsement);
				){
				issuerCert = CertificateProtos.Certificate.parseFrom(issuerStream);
				endorsementObj = CertificateProtos.Endorsement.parseFrom(endorsementStream);
			} catch (FileNotFoundException e) {
				DBP.printException(e);
				System.out.println("could not find one of the files");
				return;
			} catch (IOException e) {
				DBP.printException(e);
				System.out.println("unable to build objects from files");
				return;
			}
			EndorsementValidator ev = new EndorsementValidator();
			ev.setEndorsement(endorsementObj);
			ev.setIssuer(issuerCert);
			ev.setSubject(subjectCert);
			validator = ev;
		}else{
			CertificateValidator cv = new CertificateValidator();
			cv.setCertificate(subjectCert);
			validator = cv;
		}
		if(validator.isValid()){//TODO print info about which fields were invalid
			System.out.println("VALID\n\tAll signatures and fields are valid");
		}else{
			System.out.println("INVALID\n\tNot all signatures and fields are valid");
		}
	}
	
	private static boolean existsAndReadable(File f){
		Path file = f.toPath();
		return Files.isRegularFile(file) &
		         Files.isReadable(file);
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
			DBP.printException(e);
		}
		return parser;
	}
	
	/**
 	* create a certificate
 	*
 	* @param  args list of arguments
	 * @throws InvalidFormatException 
 	*/
	public static void create(String[] args) throws InvalidFormatException {
		DBP.printdevln("in create");
		CommandLineParser parser = getCreateParser();
		Map<String, Argument> parsedArgs;
		File descriptorFile = null;
		File keyFile = null;
		File outputFile;
		CertificateBuilder builder;
		PrivateKey key;
		byte[] cert = null;
		parsedArgs = parser.parseArgs(args);
		descriptorFile = new File(parsedArgs.get("descriptor").getValues().get(0));
		keyFile = new File(parsedArgs.get("key-file").getValues().get(0));
		outputFile = new File(parsedArgs.get("output").getValues().get(0));

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
			DBP.printException(e);
		} catch (BuilderException e) {
			// TODO Auto-generated catch block
			DBP.printException(e);
		}
		try {
			Files.write(outputFile.toPath(), cert, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			DBP.printException(e);
		}
	}

	
	private static CommandLineParser getEndorseParser(){
		CommandLineParser parser = new CommandLineParser();
		parser.setUsageHint("eg. \"cqbe certificate endorse mycerts/bob.cert friendcerts/alice.cert -d end.desc -k mykeys/key.pkcs -o myendorsements/bob-alice.endr\"");
		parser.setExecutableName("cqbe certificate endorse");
		Argument subject = new Argument().setLongOption("certificates")
				.setParam(true).setMultipleAllowed(true).setRequiredArg(true)
				.setValueRequired(true);
		Argument descriptor = new Argument().setLongOption("descriptor")
				.setOption("d").setRequiredArg(true).setMultipleAllowed(false)
				.setHelpText("descriptor FILE to populate endorsement")
				.setTakesValue(true).setValueHint("FILE")
				.setValueRequired(true);
		Argument keyFile = new Argument().setLongOption("key-file")
				.setOption("k")
				.setHelpText("use PKCS#8 key file when creating an endorsement")
				.setValueHint("FILE").setMultipleAllowed(false)
				.setRequiredArg(true).setValueRequired(true);
		Argument output = new Argument().setLongOption("output").setOption("o")
				.setHelpText("location for endorsement to be saved")
				.setMultipleAllowed(false).setTakesValue(true)
				.setValueRequired(false);
		try {
			parser.addArguments(new Argument[]{subject, descriptor, keyFile, output});
		} catch (DuplicateOptionException e) {
			DBP.printException(e);
		}
		return parser;
	}
	
	/**
 	* endorses a certificate
 	*
 	* @param  args list of arguments
	 * @throws InvalidFormatException 
 	*/
	public static void endorse(String[] args) throws InvalidFormatException {
		CommandLineParser parser = getEndorseParser();
		Map<String, Argument> parsedArgs;
		parsedArgs = parser.parseArgs(args);
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
	
	public Certificate(String[] args) {
	}
}
