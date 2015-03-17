package blackdoor.cqbe.test;

import java.util.*;

public class ProssessTest {

	public static void main(String[] args) throws Exception {
		List<String> commands = new ArrayList<String>();
		commands.add("java");
		commands.add("-jar");
		commands.add("dh256.jar");
		commands.add("join");
		commands.add("-a");
		ProcessBuilder pb = new ProcessBuilder(commands);
		pb.start();
	}
}