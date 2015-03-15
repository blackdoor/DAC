package blackdoor.cqbe.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import org.json.JSONObject;

import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.cli.dh256;
import blackdoor.cqbe.node.NodeException.CantGetAddress;
import blackdoor.util.DBP;

public class CLITester {

	public static void main(String[] args) throws Exception, IOException {
		InetAddress address;

			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					whatismyip.openStream()));
			address = InetAddress.getByName(in.readLine());
		
		DBP.DEMO= true;
		DBP.DEV = true;
		DBP.ERROR = false;
		DBP.WARNING = true;
		//addSettings();
		//String[] args2 = {"retrieve", "AC:B6:6E:9D:13:FE:13:D0:BE:AA:E2:D8:1F:BA:95:02:4E:B9:BE:72:2D:6F:F2:A0:67:FF:88:BC:1C:0A:C6:2C", "-d", "yay.txt", "-b","localhost:1778"};
		//String[] args2 = {"insert","lol_file.txt","-b","localhost:1778"};
		//String[] args2 = {"join","-a"};
		//String[] args2 = {"join", address.getHostAddress() + ":1778","-p","1779"};
		//String[] args2 = {"join", address.getHostAddress() + ":1778","-p","1780"};
		//String[] args2 = {"join", address.getHostAddress() + ":1778","-p","1781"};
		//String[] args2 = {"join", address.getHostAddress() + ":1778","-p","1782"};
		//String[] args2 = {"join", address.getHostAddress() + ":1778","-p","1783"};
		String[] args2 = {"shutdown", "-p", "1776"};
		dh256.main(args2);

	}
	
	public static void addSettings() {
		File theDir = new File("NodeStorage");
		if (!theDir.exists()) {
		    try{
		        theDir.mkdir();
		     } catch(SecurityException se){}        
		  }
		
		String defaultfile = "dflt.txt";
		writeJSON(defaultfile, getDefultSettings());	
	}
	
	public static JSONObject getDefultSettings() {
		JSONObject rpc = new JSONObject();
		rpc.put("port", 1778);
		rpc.put("storageDir", "NodeStorage");
		rpc.put("savefile", "session.txt");
		return rpc;
	}
	
	public static void writeJSON(String outfile, JSONObject obj) {
		try {
			File file = new File(outfile);
			file.createNewFile();
			FileWriter out = new FileWriter(file);
			out.write(obj.toString());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
