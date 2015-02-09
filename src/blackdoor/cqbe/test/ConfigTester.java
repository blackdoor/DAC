package blackdoor.cqbe.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import blackdoor.cqbe.settings.Config;
import blackdoor.cqbe.settings.ConfigurationException;

public class ConfigTester {

	public static void main(String[] args) {
		List<String> li = new ArrayList<String>();
		// where to write stubs
		String defaultfile = "dflt.txt";
		String settingfile = "session.txt";
		//String broken = "athis.txt";
		// write stubs to file
		li.add(defaultfile);
		li.add(settingfile);

		writeJSON(defaultfile, getDefultSettings());
		writeJSON(settingfile, getSettingsStub());

		Config config = null;
		try {
			config = new Config(new File(defaultfile));
			config.loadSettings(new File(settingfile));
			Set<String> ks = config.keySet();
			for (String k : ks) {
				System.out.println(k + ": " + config.get(k));
			}
		} catch (ConfigurationException e1) {
			System.err.println("problems getting config going");
			System.out.println(e1);
		}

		//CleanUpDoYourShare(li);
	}

	public static void CleanUpDoYourShare(List<String> files) {
		for (String path : files) {
			boolean success = (new File(path)).delete();
			if (success)
				System.out.println("deleted");
			else
				System.out.println("go pick up your toy: " + path);
		}
	}

	public static JSONObject getSettingsStub() {
		JSONObject rpc = new JSONObject();
		rpc.put("port", 1778);
		rpc.put("storageDir", "NodeStorage");
		rpc.put("savefile", "session.txt");
		return rpc;
	}

	public static JSONObject getDefultSettings() {
		JSONObject rpc = new JSONObject();
		rpc.put("id", "default");
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