/**
 * 
 */
package blackdoor.cqbe.cli;

import java.util.Arrays;
import java.util.Scanner;
import java.io.Console;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 5, 2014
 */
public class Keys {

	public static void main(String[] args) {
		Keys kl = new Keys();
		if (args[0].equals("create")) {
			kl.create(Arrays.copyOfRange(args, 1, args.length));
		} else if (args[0].equals("retrieve")) {
			kl.retrive(Arrays.copyOfRange(args, 1, args.length));
		} else if (args[0].equals("remove")) {
			kl.remove(Arrays.copyOfRange(args, 1, args.length));
		} else {
			// Something went wrong. Handle this.
		}
	}

	/**
	 * Creates a keypair and stores it encrypted in a keystore.
	 * <p>
	 *
	 * @param args
	 *            List of Arguments.
	 */
	public void create(String[] args) {
		String password = scannerPrompt(); // for IDE TESTING ONLY
	}

	/**
	 * Retrieves a key from a keystore.
	 * <p>
	 *
	 * @param args
	 *            List of Arguments.
	 */
	public void retrive(String[] args) {
		String password = scannerPrompt(); // for IDE TESTING ONLY
	}

	/**
	 * Removes a key from a keystore.
	 * <p>
	 *
	 * @param args
	 *            List of Arguments.
	 */
	public void remove(String[] args) {
		String password = scannerPrompt(); // for IDE TESTING ONLY
	}

	/**
	 * Gets password from user input.
	 * <p>
	 * Characters are hidden from console, and console history.
	 * 
	 * @return password input.
	 */
	private String consolePrompt() {
		Console cons = null;
		char[] passwd = null;
		if ((cons = System.console()) != null && (passwd = cons.readPassword("[%s]", "enter password:")) != null) {
			java.util.Arrays.fill(passwd, ' ');
		} else {
			// Handle a null console, or no password entry!
		}
		return passwd.toString();
	}

	/**
	 * Gets password from user input.
	 * <p>
	 * USED ONLY FOR IDE TESTING. Will expose password to console history, which
	 * is a security flaw. Do not do that.
	 * 
	 * @return password input.
	 */
	private String scannerPrompt() {
		System.out.println("enter password:");
		Scanner s = new Scanner(System.in);
		String password = s.next();
		// Check password format

		return password;
	}
}