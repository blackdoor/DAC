package blackdoor.cqbe.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;

import blackdoor.cqbe.cli.dh256;
import blackdoor.util.DBP;

public class CLITester {

	public static void main(String[] args) {
		DBP.DEBUG = true;
		DBP.DEV = true;
		//addSettings();
		//String[] args2 = {"insert","NodeStorage/lol_file.txt","-b","localhost:1779"};
		//String[] args2 = {"join","-p","1778","-a"};
		String[] args2 = {"join","147.222.45.95:1778","-p","3112"};
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