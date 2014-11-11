/**
 * 
 */
package blackdoor.util;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Calendar;

/**
 * DeBugPrint. A class that allows output to be categorized before being
 * printed. Different categories of output can then be enabled, disabled or
 * redirected at runtime. The default output for most categories is System.out
 * 
 * @author nfischer3
 * 
 */
public class DBP {
	public static boolean DEBUG = false;
	public static boolean ERROR = false;
	public static boolean DEV = false;
	public static boolean DEMO = false;
	public static boolean WARNING = false;
	public static boolean LOG = true;
	/**
	 * The location of the file where calls to printlog and printlogln are
	 * written.
	 */
	private static String LOG_FILE = "log.log";
	/**
	 * When true, all calls to printerror and printerrorln are written to
	 * System.err rather than the default output.
	 */
	public static boolean ERROR_AS_SYSTEM_ERROR = false;
	/**
	 * When true, all output is written to the log file in addition to it's
	 * usual output.
	 */
	public static boolean LOG_ALL = false;
	/**
	 * When true, calls to any print* or print*ln method act as if their
	 * corresponding flag is true. In other words all calls will result data
	 * being written to output. The exception to this is log, which is
	 * unaffected.
	 */
	public static boolean VERBOSE = false;

	private static DBP singleton = null;
	private PrintStream out = System.out;
	private PrintStream log;

	private DBP() {
		this(LOG_FILE);
	}

	private DBP(String logFile) {
		LOG_FILE = logFile;
		try {
			this.log = new PrintStream(LOG_FILE);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(LOG_FILE + " not found.");
		}
	}

	private static void initSingleton() {
		if (singleton == null) {
			singleton = new DBP();
		}
	}

	/**
	 * Sets the default (all output except log and/or error when
	 * ERROR_AS_SYSTEM_ERROR) PrintStream for DBP to write out to. If this
	 * method is not called then output will go to System.out
	 * 
	 * @param out
	 *             the PrintStream to use for all output except log and/or
	 *            error when ERROR_AS_SYSTEM_ERROR = true.
	 */
	public static void setDefaultOutput(PrintStream out) {
		singleton.out = out;
	}

	/**
	 * Prints e to a log file. Use this method for any output to be saved to the
	 * log file (obviously).
	 * 
	 * @param e
	 */
	public static void printlog(Object e) {
		print(e, "LOG", LOG, singleton.log);
	}

	public static void printlogln(Object e) {
		printlog(e.toString() + '\n');
	}

	private static void print(Object e, String mode, boolean flag) {
		initSingleton();
		print(e, mode, flag, singleton.out);
	}

	private static void print(Object e, String mode, boolean flag, PrintStream o) {
		Calendar cal;
		String out;
		if (flag || VERBOSE) {
			cal = Calendar.getInstance();
			out = "[" + String.format("%1$tH:%1$tM:%1$tS", cal) + ']';
			out += String.format("[%-7s] ", mode);// "[" + mode + "]\t";
			out += "" + e;
			o.print(out);
			if (LOG_ALL && !mode.equals("LOG")) {
				singleton.log.print(out);
			}
		}
	}

	/**
	 * Use this method for any text that might be useful during debugging; such
	 * as values of variables or states of a FSM.
	 * 
	 * @param e
	 */
	public static void printdebug(Object e) {
		print(e, "DEBUG", DEBUG);
	}

	public static void printdebugln(Object e) {
		printdebug(e.toString() + '\n');
	}

	/**
	 * Use this method to print any errors. Try to make error output easily
	 * readable such that printing errors could be enabled in production code.
	 * 
	 * @param e
	 */
	public static void printerror(Object e) {
		print(e, "ERROR", ERROR, System.err);
	}

	public static void printerrorln(Object e) {
		printerror(e.toString() + '\n');
	}

	/**
	 * Use this method for output that should NEVER be seen outside of
	 * development; such as printing the output of a regex while tweaking it or
	 * a line to let you know a method call has been made.
	 * 
	 * @param e
	 */
	public static void printdev(Object e) {
		print(e, "DEV", DEV);
	}

	public static void printdevln(Object e) {
		printdev(e.toString() + '\n');
	}

	/**
	 * Use this method for output that would be used in a live demo; for example
	 * to show that some variable has changed or that some action has happened.
	 * 
	 * @param e
	 */
	public static void printdemo(Object e) {
		print(e, "DEMO", DEMO);
	}

	public static void printdemoln(Object e) {
		printdemo(e.toString() + '\n');
	}

	/**
	 * Use this to print warnings. Same rules as printerror(); warning output
	 * should be ok to enable in production.
	 * 
	 * @param e
	 */
	public static void printwarning(Object e) {
		print(e, "WARNING", WARNING);
	}

	public static void printwarningln(Object e) {
		printwarning(e.toString() + '\n');
	}

	public static boolean toggleDebug() {
		DEBUG = !DEBUG;
		return DEBUG;
	}

	public static boolean toggleDemo() {
		DEMO = !DEMO;
		return DEMO;
	}

	public static boolean toggleDev() {
		DEV = !DEV;
		return DEV;
	}

	public static boolean toggleError() {
		ERROR = !ERROR;
		return ERROR;
	}

	public static boolean toggleLog() {
		LOG = !LOG;
		return LOG;
	}

	public static boolean toggleWarning() {
		WARNING = !WARNING;
		return WARNING;
	}

	public static boolean toggleVerbose() {
		VERBOSE = !VERBOSE;
		return VERBOSE;
	}

	/**
	 * @return The location of the active log file.
	 */
	public static String getLogFileLocation() {
		return LOG_FILE;
	}

	/**
	 * Sets the location of the log file. Also initializes the DBP singleton,
	 * therefore it cannot be called twice.
	 * 
	 * @param logFile
	 *             The location of the file where calls to printlog and
	 *            printlogln are written.
	 * @throws SingletonAlreadyInitializedException
	 *              Thrown if the singleton has already been initialized.
	 */
	public static void setLogFileLocation(String logFile)
			throws SingletonAlreadyInitializedException {
		if (singleton == null) {
			singleton = new DBP(logFile);
		} else {
			throw new SingletonAlreadyInitializedException();
		}
	}

	@SuppressWarnings("serial")
	public static class SingletonAlreadyInitializedException extends Exception {
		public SingletonAlreadyInitializedException() {
			super(
					"This action is attempting to re-initialize an initialized singleton.");
		}

		public SingletonAlreadyInitializedException(String s) {
			super(s);
		}
	}
}
