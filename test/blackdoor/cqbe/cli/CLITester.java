package blackdoor.cqbe.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

import blackdoor.cqbe.cli.dh256;
import blackdoor.util.DBP;

public class CLITester {

	public static void main(String[] args) throws Exception, IOException {
		InetAddress address;

			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					whatismyip.openStream()));
			address = InetAddress.getByName(in.readLine());
		
		//DBP.enableChannel("DEMO");
		//DBP.DEMO= true;
		//DBP.DEBUG = false;
		//DBP.DEV = true;
		//DBP.ERROR = false;
		//DBP.enableChannel("ERROR");
		//DBP.enableChannel("WARNING");//DBP.WARNING = true;
		//DBP.LOG_ALL = true;
		//DBP.VERBOSE = true;
		addSettings();
		//String[] args2 = {"retrieve", "AC:B6:6E:9D:13:FE:13:D0:BE:AA:E2:D8:1F:BA:95:02:4E:B9:BE:72:2D:6F:F2:A0:67:FF:88:BC:1C:0A:C6:2C", "-d", "yay.txt", "-b","localhost:1778"};
		//String[] args2 = {"insert","savefile.txt","-b","localhost:1784"};
		//String[] args2 = {"join","-a", "-p", "1778", "--log", "log/adam.log"};
		
		//String[] args2 = {"join", address.getHostAddress() + ":1778","-p","1779"};
		//String[] args2 = {"join", address.getHostAddress() + ":1778","-p","1780" };
		//String[] args2 = {"join", address.getHostAddress() + ":1778","-p","1781"};
		//String[] args2 = {"join", address.getHostAddress() + ":1778","-p","1782"};
		//String[] args2 = {"join", address.getHostAddress() + ":1778","-p","1783"};
		//String[] args2 = {"shutdown", "-p", "1779"};
		
		String[] args2 = {"join", "clusterfuck.tk:57005","-p","1784"};
		dh256.main(args2);

	}
	
	public static void addSettings() {
		File theDir = new File("NodeStorage");
		if (!theDir.exists()) {
			try {
				theDir.mkdir();
			} catch (SecurityException se) {
			}
		}
		for(int i = 2; i <= 4; i++) {
			theDir = new File("NodeStorage" + i);
			if (!theDir.exists()) {
				try {
					theDir.mkdir();
				} catch (SecurityException se) {
				}
			}
		}
	}

}
