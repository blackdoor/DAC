package blackdoor.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CommandLineParser is a ... command line parser. CLP offers GNU-like help
 * menus and option syntax.
 * <p>
 * To help understand methods and variables in this class the following
 * definitions are given:
 * <p>
 * An argument is anything in the command line after the program name.
 * <p>
 * An parameter is an argument not specified by an option.
 * <p>
 * An option is a one letter or one word indicator (preceded by one or two
 * dashes, respectively.) that can be on it's own, or be followed by whitespace
 * and then a value.
 * <p>
 * A value is used to define some attribute in the command line, and is
 * typically preceded by and associated with an option.
 * <p>
 * e.g. in "wget --output index.html -t 5 google.com/file.txt" "output" and "t"
 * are options, "index.html" and "5" are the values of "output" and "t"
 * respectively, while "google.com/file.txt" is the value of a parameter.
 * Everything in the string is an argument, except "wget" which is the program
 * name.
 * 
 * @author nfischer3
 *
 */
public class CommandLineParser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6504238417414070778L;
	private List<List<String>> sortedArgs = null;
	private ArrayList<Argument> args = null;
	private List<Argument> params;
	private String usageHint = "";
	private String executableName = "";
	private String programDescription = "";

	/**
	 * Creates a new CommandLineParser. Different arguments and options can be
	 * defined for this parser, once configured the parser can handle multiple
	 * argument strings with the same configuration.
	 */
	public CommandLineParser() {
		sortedArgs = new ArrayList<List<String>>();
		args = new ArrayList<Argument>();
		params = new ArrayList<Argument>();
	}

	/**
	 * Get the parsed and checked command line arguments for this parser
	 * 
	 * @param args
	 *            - The command line arguments to add. These can be passed
	 *            straight from the parameter of main(String[])
	 *            <p>
	 * @return A list of strings, the first([0]) element in each list is the
	 *         command line option, if the second([1]) element exists it is the
	 *         parameter for that option.
	 *         <p>
	 *         Returns null if parseArgs(String[]) has not been called.
	 * @throws InvalidFormatException
	 */
	@Deprecated
	public List<List<String>> getParsedArgs(String[] args)
			throws InvalidFormatException {
		for (int i = 0; i < args.length; i++) {
			if (!args[i].startsWith("-")) {
				if (this.params.size() > 0) {
					List<String> option = new ArrayList<String>();
					option.add(this.params.get(0).longOption);
					this.params.remove(0);
					option.add(args[i]);
					sortedArgs.add(option);
				} else {
					throw new InvalidFormatException(
							"Expected command line option, found " + args[i]
									+ " instead.");
				}

			} else {

				for (Argument option : this.args) {
					if (option.matchesFlag(args[i])) {
						List<String> command = new ArrayList<String>();
						command.add(noDashes(args[i]));
						if (option.takesValue) {
							try {
								if (args[i + 1].startsWith("-")) {
									if (option.valueRequired)
										throw new InvalidFormatException(
												"Invalid command line format: -"
														+ option.option
														+ " or --"
														+ option.longOption
														+ " requires a parameter, found "
														+ args[i + 1]
														+ " instead.");
								} else {
									command.add(args[++i]);
								}
							} catch (ArrayIndexOutOfBoundsException e) {
							}
						}
						sortedArgs.add(command);
						break;
					}
				}
			}
		}
		return sortedArgs;
	}

	/**
	 * Parse arguments from the command line into a map of Argument objects.
	 * 
	 * @param args
	 *            An array of Strings passed to main from the command line.
	 * @return A map of Argument objects, if an argument had a long form then
	 *         that is the key, otherwise the short form is the key.
	 * @throws InvalidFormatException
	 *             Thrown if the command line is not in a valid format or if a
	 *             required argument is missing, or if a required value is
	 *             missing from an argument, etc...
	 */
	public Map<String, Argument> parseArgs(String[] args)
			throws InvalidFormatException {
		HashMap<String, Argument> parsedArgs = new HashMap<String, Argument>();
		for (int i = 0; i < args.length; i++) {
			// arg does not start with "-" and is therefore a parameter
			if (!args[i].startsWith("-")) {
				Argument param = null;
				// find parameter in this.args
				for (Argument arg : this.args) {
					if (arg.isParam) {
						param = arg;
						break;
					}
					throw new InvalidFormatException(
							args[i]
									+ " is a parameter, this parser does not have any parameter arguments defined.");
				}
				// check if parameter has already been parsed
				if (parsedArgs.containsKey(param.longOption)) {
					// check if parameter is allowed to be parsed more than once
					if (parsedArgs.get(param.longOption).multipleAllowed) {
						parsedArgs.get(param.longOption).addValue(args[i]);
					} else {
						throw new InvalidFormatException(
								args[i]
										+ " is a duplicate parameter, this parser is only configured to accept one value for parameter "
										+ param.longOption);
					}
					// if parameter has not been parsed, add it to map of parsed
					// args
				} else {
					parsedArgs.put(param.longOption,
							((Argument) param.clone()).addValue(args[i]));
				}
				// arg starts with -- and is therefore an option
			} else {
				// if option is in long form
				boolean l = false;
				if (args[i].startsWith("--"))
					l = true;
				String form = noDashes(args[i]);
				Argument opt = null;
				// find option in args
				for (Argument arg : this.args) {
					if (l ? form.equalsIgnoreCase(arg.longOption) : form
							.equals(arg.option)) {
						opt = arg;
						break;
					}
				}
				if (opt == null)
					throw new InvalidFormatException(args[i]
							+ " is not a valid option for this parser.");
				String key = opt.longOption == null ? opt.option
						: opt.longOption;
				if (parsedArgs.containsKey(key)) {
					if (parsedArgs.get(key).multipleAllowed) {
						if (parsedArgs.get(key).takesValue()) {
							if (!args[i + 1].startsWith("-")) {
								parsedArgs.get(key).addValue(args[++i]);
							} else {
								if (parsedArgs.get(key).isValueRequired()) {
									throw new InvalidFormatException(args[i]
											+ " requires a value, "
											+ args[i + 1]
											+ " is an option, not a value.");
								}
							}
						}
					} else {
						throw new InvalidFormatException(
								args[i]
										+ " is a duplicate option, this parser is only configured to accept one value for option "
										+ key);
					}
				} else {
					opt = (Argument) opt.clone();
					if (opt.takesValue()) {
						if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
							opt.addValue(args[++i]);
						} else {
							if (opt.isValueRequired()) {
								throw new InvalidFormatException(
										args[i]
												+ " requires a value, "
												+ (i + 1 < args.length ? args[i + 1]
														+ " is an option, not a value."
														: " there should be an value after it."));
							}
						}
					}
					parsedArgs.put(key, (Argument) opt.clone());
				}
			}
		}
		Set<String> set = parsedArgs.keySet();
		for (Argument arg : this.args) {
			if (arg.isRequiredArg()
					&& !set.contains(arg.getLongOption() == null ? arg
							.getOption() : arg.getLongOption())) {
				throw new InvalidFormatException(
						arg.getLongOption() == null ? arg.getOption()
								: arg.getLongOption()
										+ " is a required argument, but was not included in command line.");
			}
		}
		return parsedArgs;
	}

	/**
	 * Get a GNU-like help text output. This should be displayed when the
	 * options -h, --help are entered, or when an invalid command line is
	 * entered.
	 * 
	 * @return GNU-like help output.
	 */
	public String getHelpText() {
		@SuppressWarnings("unchecked")
		ArrayList<Argument> arguments = (ArrayList<Argument>) this.args.clone();
		String ret = programDescription + "\n";
		ret += "Usage: " + executableName + " ";
		for (Argument arg : this.args) {
			if (arg.isParam) {
				ret += arg.longOption + " ";
				arguments.remove(arg);
			}
		}
		for (Argument arg : this.args) {
			if (arg.isRequiredArg()) {
				ret += arg.getLongOption() == null ? "-" + arg.getOption()
						: "--" + arg.getLongOption();
				ret += " ";
			}
		}
		ret += "[OPTION]... ";
		ret += "\n" + usageHint + "\n";
		ArrayList<String> col1 = new ArrayList<String>();
		ArrayList<String> col2 = new ArrayList<String>();
		for (Argument option : arguments) {
			String line = option.getOption() != null ? String.format("  %-5s",
					"-" + option.option
							+ (option.getLongOption() != null ? "," : ""))
					: String.format("  %-5s", "");
			line += option.getLongOption() != null ? "--"
					+ option.longOption
					+ (option.getValueHint().length() > 0
							&& option.takesValue() ? "="
							+ option.getValueHint() : "") : "";
			col1.add(line);
			col2.add(option.helpText);
		}
		ret += theColumnator((String[]) col1.toArray(new String[] {}),
				(String[]) col2.toArray(new String[] {}), 2, 80);
		return ret;
	}

	private static String theColumnator(String[] col1, String[] col2,
			int dividerSize, int pageWidth) {
		String output = "";
		int maxCol1Len = 0;
		int col2Indent;
		for (String line : col1) {
			if (line.length() > maxCol1Len)
				maxCol1Len = line.length();
		}
		col2Indent = dividerSize + maxCol1Len;

		for (int i = 0; i < col1.length; i++) {
			output += theIndenter(String.format("%-" + col2Indent + "s%s\n",
					col1[i], col2[i]), col2Indent, pageWidth);
		}

		return output;
	}

	private static String theIndenter(String str, int indentSize, int pageWidth) {
		if (str.length() < pageWidth)
			return str;
		int columnSize = pageWidth - indentSize;
		boolean firstLine = true;
		boolean firstWord = true;
		ArrayList<String> lines = new ArrayList<String>();
		String[] input = str.split(" ");
		String line = "";
		for (int i = 0; i < input.length; i++) {
			if (line.length() + input[i].length() <= (firstLine ? pageWidth
					: columnSize)) {
				line += (firstWord ? "" : " ") + input[i];
				firstWord = false;
			} else {
				lines.add(line);
				firstLine = false;
				line = "";
				if (input[i].length() + line.length() > columnSize) {
					throw new RuntimeException("Dude, WTF is that word??");
				} else {
					line += input[i];
				}
			}
		}
		if (!line.equals(lines.get(lines.size() - 1)) && !line.equals(""))
			lines.add(line);

		firstLine = true;
		String out = "";
		for (String l : lines) {
			out += (firstLine ? "" : getLine(indentSize, ' ')) + l + '\n';
			firstLine = false;
		}
		return out;
	}

	private static String getLine(int width, char space) {
		String output = "";
		for (int i = 0; i < width; i++) {
			output += space;
		}
		return output;
	}

	/**
	 * @param usageHint
	 *            the usageHint to set for the program who's command line
	 *            arguments are being parsed
	 */
	public void setUsageHint(String usageHint) {
		this.usageHint = usageHint;
	}

	/**
	 * Set the program description which will be the first line of getHelpText()
	 * 
	 * @param programDescription
	 *            A brief, one-line description of the program.
	 */
	public void setProgramDescription(String programDescription) {
		this.programDescription = programDescription;
	}

	/**
	 * @return the name defined for this executable
	 */
	public String getExecutableName() {
		return executableName;
	}

	/**
	 * @param executableName
	 *            The name of the executable for which arguments will be parsed.
	 */
	public void setExecutableName(String executableName) {
		this.executableName = executableName;
	}

	/**
	 * adds options for this command line parser
	 * 
	 * @param argList
	 *            a list of Strings of options in a comma separated format
	 *            <p>
	 *            single char options should be prepended with a single "-"
	 *            <p>
	 *            string options should be prepended with "--"
	 *            <p>
	 *            non-option parameters should add a "?" to the string.
	 *            non-option parameters should define the string (long/--) form
	 *            option.
	 *            <p>
	 *            ie. parameters that would not have an explicit option before
	 *            them
	 *            <p>
	 *            eg. cp source.txt dest.txt
	 *            <p>
	 *            to add helptext for this option, add -h followed by the help
	 *            text
	 *            <p>
	 *            if there MUST be a parameter after this command line option
	 *            then add a "+" to the string. alternatively if there MAY be a
	 *            parameter after this command line option then add a "*" to the
	 *            string
	 *            <p>
	 *            eg. "-r,--readonly" or "--file,-f,+" or
	 *            "*, -f, --flag, -h this is helptext" or "--source, ?"
	 * @throws DuplicateOptionException
	 * @throws InvalidFormatException
	 */
	@Deprecated
	public void addArguments(String[] argList) throws DuplicateOptionException,
			InvalidFormatException {
		for (String arg : argList) {
			Argument f = new Argument();
			String[] breakdown = arg.split(",");
			for (String s : breakdown) {
				s = s.trim();
				if (s.startsWith("--")) {
					f.longOption = noDashes(s);
				} else if (s.startsWith("-h")) {
					f.helpText = s.substring(2);
				} else if (s.startsWith("-")) {
					f.option = noDashes(s);
				} else if (s.equals("+")) {
					f.takesValue = true;
					f.valueRequired = true;
				} else if (s.equals("?")) {
					f.isParam = true;
					f.takesValue = true;
					params.add(f);
				} else if (s.equals("*")) {
					f.takesValue = true;
				} else {
					throw new InvalidFormatException(s + " in " + arg
							+ " is not formatted correctly.");
				}
			}
			addArgument(f);
		}
	}

	/**
	 * Add a single option to this command line parser. Convenience method for
	 * calling addFlags with only one option
	 * 
	 * @param argumentString
	 *            - formatted string for this command line option, should be in
	 *            same format as strings in addOptions(String[])
	 * @throws DuplicateOptionException
	 * @throws InvalidFormatException
	 */
	@Deprecated
	public void addArgument(String argumentString)
			throws DuplicateOptionException, InvalidFormatException {
		addArguments(new String[] { argumentString });
	}

	/**
	 * Add an argument for this command line parser. Options should not be
	 * prepended by dashes.
	 * 
	 * @param shortForm
	 *            Single character command line option
	 * @param longForm
	 *            String command line option
	 * @param helpText
	 *            Help text to show after the short and long form in
	 *            getHelpText()
	 * @param isParameter
	 *            True if this argument is a parameter and will not be preceded
	 *            by an option
	 * @param valueRequired
	 *            True if the user MUST enter a value for this argument.
	 * @param takesValue
	 *            Should be true if this option may followed by a value when
	 *            called from the command line.
	 * @throws DuplicateOptionException
	 *             Thrown if this parser already has an option with the same
	 *             short or long form.
	 */
	@Deprecated
	public void addArgument(char shortForm, String longForm, String helpText,
			boolean isParameter, boolean takesValue, boolean valueRequired)
			throws DuplicateOptionException {
		Argument f = new Argument();
		f.option = "" + shortForm;
		f.longOption = longForm;
		f.takesValue = takesValue;
		f.valueRequired = valueRequired;
		f.helpText = helpText;
		f.isParam = isParameter;
		if (isParameter)
			params.add(f);
		addArgument(f);
	}

	/**
	 * Add an argument for this command line parser.
	 * 
	 * @param arg
	 *            The Argument object to add to this parser
	 * @throws DuplicateOptionException
	 *             Thrown if this parser already has an option with the same
	 *             short or long form.
	 */
	public void addArgument(Argument arg) throws DuplicateOptionException {
		duplicateOption(arg);
		args.add(arg);
	}

	private void duplicateOption(Argument opt) throws DuplicateOptionException {
		for (Argument op : args) {
			if (opt.duplicateOptions(op))
				throw new DuplicateOptionException(opt);
			if (opt.isParam() && op.isParam()) {
				throw DuplicateOptionException
						.customMessageBuilder("You can only have one parameter argument defined. You already defined "
								+ op.getLongOption()
								+ ". The existing parameter argument can have multiple values.");
			}
		}
	}

	/**
	 * Convenience method to add arguments from an array. Same as calling
	 * addArgument(Option) on every element in args.
	 * 
	 * @param args
	 *            Array of Arguments to add for this CommandLineParser.
	 * @throws DuplicateOptionException
	 *             Thrown if this parser already has an option with the same
	 *             short or long form as one of the objects you are trying to
	 *             add.
	 */
	public void addArguments(Argument[] args) throws DuplicateOptionException {
		for (Argument opt : args) {
			addArgument(opt);
		}
	}

	private String noDashes(String s) {
		return s.replaceFirst("(--|-)", "");
	}

	public static class Argument implements Cloneable, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 438781422325152886L;
		/**
		 * Short form of command line option.
		 * <p>
		 * e.g. -f as in file
		 */
		String option = null;
		/**
		 * Long form of command line option.
		 * <p>
		 * e.g. --file
		 */
		String longOption = null;
		/**
		 * Once an argument string from the command line is parsed, this list
		 * will be filled with any values attributable to this argument.
		 */
		protected ArrayList<String> values = new ArrayList<String>();
		/**
		 * The string of text to enter along side the options for this argument
		 * in the help menu.
		 */
		String helpText = "";
		/**
		 * The value hint that will appear in the help menu.
		 */
		String valueHint = "";
		/**
		 * True if this argument is a parameter or an option that takes values.
		 * False if this argument is an option that simply acts as a flag.
		 */
		boolean takesValue = true;
		/**
		 * True if this argument is a parameter or if there must be a value
		 * after this argument if it is an option.
		 */
		boolean valueRequired = false;
		/**
		 * True if this argument will never have an option in front of it.
		 */
		boolean isParam = false;
		/**
		 * True if this argument must be supplied. If requiredArg is true and
		 * this argument is not supplied in a command string then the help menu
		 * will be shown.
		 */
		boolean requiredArg = false;
		/**
		 * True if this argument can occur more than one time in the command
		 * line.
		 */
		boolean multipleAllowed = true;

		/**
		 * Returns a deep copy of this Argument object.
		 */
		public Object clone() {
			Argument out = new Argument().setHelpText(this.helpText)
					.setLongOption(this.longOption)
					.setMultipleAllowed(this.multipleAllowed)
					.setOption(this.option).setParam(this.isParam)
					.setRequiredArg(requiredArg).setTakesValue(takesValue)
					.setValueRequired(valueRequired);
			for (String value : values) {
				out.addValue(value);
			}
			return out;
		}

		public boolean duplicateOptions(Argument arg) {
			if (option != null && arg.getOption() != null
					&& arg.getOption().equals(option)) {
				return true;
			}
			if (longOption != null && arg.getLongOption() != null
					&& arg.getLongOption().equals(longOption))
				return true;
			return false;
		}

		public boolean matchesFlag(String option) {
			return !isParam
					&& (option.equalsIgnoreCase("--" + longOption) || option
							.equals("-" + this.option));
		}

		/**
		 * @return the valueHint
		 */
		public String getValueHint() {
			return valueHint;
		}

		/**
		 * @param valueHint
		 *            the valueHint to set
		 */
		public Argument setValueHint(String valueHint) {
			this.valueHint = valueHint;
			return this;
		}

		public String getOption() {
			return option;
		}

		public Argument setOption(String option) {
			this.option = option;
			return this;
		}

		public String getLongOption() {
			return longOption;
		}

		public Argument setLongOption(String longOption) {
			this.longOption = longOption == null ? null : longOption
					.toLowerCase();
			return this;
		}

		public ArrayList<String> getValues() {
			return values;
		}

		public Argument addValue(String value) {
			this.values.add(value);
			return this;
		}

		/**
		 * @return the helpText
		 */
		public String getHelpText() {
			return helpText;
		}

		/**
		 * @param helpText
		 *            the helpText to set
		 */
		public Argument setHelpText(String helpText) {
			this.helpText = helpText;
			return this;
		}

		public boolean takesValue() {
			return takesValue;
		}

		public Argument setTakesValue(boolean takesValue) {
			this.takesValue = takesValue;
			return this;
		}

		public boolean isValueRequired() {
			return valueRequired;
		}

		public Argument setValueRequired(boolean valueRequired) {
			this.valueRequired = valueRequired;
			return this;
		}

		public boolean isParam() {
			return isParam;
		}

		public Argument setParam(boolean isParam) {
			this.isParam = isParam;
			return this;
		}

		public boolean isRequiredArg() {
			return requiredArg;
		}

		public Argument setRequiredArg(boolean requiredArg) {
			this.requiredArg = requiredArg;
			return this;
		}

		public boolean isMultipleAllowed() {
			return multipleAllowed;
		}

		public Argument setMultipleAllowed(boolean multipleAllowed) {
			this.multipleAllowed = multipleAllowed;
			return this;
		}

		@Override
		public String toString() {
			return "Argument [option=" + option + ", longOption=" + longOption
					+ ", values=" + values + ", helpText=" + helpText
					+ ", takesValue=" + takesValue + ", valueRequired="
					+ valueRequired + ", isParam=" + isParam + ", requiredArg="
					+ requiredArg + ", multipleAllowed=" + multipleAllowed
					+ "]";
		}

	}

	public static class DuplicateOptionException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DuplicateOptionException(String option) {
			super(option.length() > 1 ? "--" : "-" + option
					+ " has already been added as an option");
		}

		public DuplicateOptionException(Argument arg) {
			super("-" + arg.option + " or " + "--" + arg.longOption
					+ " has already been added as an option");
		}

		private DuplicateOptionException(String e, boolean x) {
			super(e);
		}

		public static DuplicateOptionException customMessageBuilder(String e) {
			return new DuplicateOptionException(e, false);
		}
	}

	public class InvalidFormatException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4262637859491086870L;

		InvalidFormatException(String s) {
			super(s);
		}
	}

}
