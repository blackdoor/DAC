package blackdoor.cqbe.cli;

import java.util.Arrays;

public class DART {

	/**
	 * This is the main function that is called whenever the user ineracts with
	 * dart via cli
	 *
	 * @param args
	 *            command line args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Proper Usage is: java DART args");
			System.exit(0);
		}
		if (args[0].equals("cert")) {
			String[] args2 = Arrays.copyOfRange(args, 1, args.length);
			new CertificateUILogic(args2);
		} else if (args[0].equals("keys")) {
			String[] args2 = Arrays.copyOfRange(args, 1, args.length);
			Keys.main(args2);
		} else if (args[0].equals("help")) {
			String[] args2 = Arrays.copyOfRange(args, 1, args.length);
			new DART().help(args2);
		} else {
			new DART().help(args);
		}
	}

	/**
	 * Used to print a menu of commands, their descriptions and uses. Can be
	 * used injunction with a specific command to get just that command’s
	 * description.
	 *
	 * @param args
	 *            list of arguments
	 */
	public void help(String[] args) {
	}

	/**
	 * join and setup the dart network
	 *
	 * @param args
	 *            list of arguments
	 */
	public void join(String[] args) {
	}

	/**
	 * Insert certfile(s) into the network
	 *
	 * @param args
	 *            list of arguments
	 */
	public void insert(String[] args) {
	}

	/**
	 * Finds a specific certificate in the network
	 *
	 * @param args
	 *            list of arguments
	 */
	public void retrieve(String[] args) {
	}

	/**
	 * Used to exit the network
	 *
	 * @param args
	 *            list of arguments
	 */
	public void leave(String[] args) {

	}
}