package blackdoor.cqbe.settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.nio.file.Files;

import blackdoor.cqbe.settings.ConfigurationException.*;

import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Cj Buresch
 * @version v0.1.1 - Jan 22, 2015
 */
public class Config extends ConcurrentHashMap<String, Object> {

	private Object writeLock = new Object();
	private final String savefile_keyname = "savefile";

	/**
	 * Makes session settings available to the node classes.
	 * <p>
	 * Initialize Configuration with the default settings file. Use
	 * loadSettings() to load other settings, that will overwrite with the
	 * user's custom session settings.
	 * 
	 * @param defaultsettings
	 * @throws ConfigurationException
	 */
	public Config(File defaultsettings) throws ConfigurationException {
		super();
		loadSettings(defaultsettings);
	}

	/**
	 * Load other settings from file.
	 * <p>
	 * Duplicate settings will be overwritten, if there are any. Otherwise,
	 * settings will be loaded and made available from the file, if they are
	 * called from Config.
	 * 
	 * @param usersettings
	 * @throws ConfigurationException
	 */
	public void loadSettings(File settings) throws ConfigurationException {
		JSONObject jo = read(settings);
		Iterator<String> keys = jo.keys();
		String tmp = "";
		while (keys.hasNext()) {
			tmp = keys.next();
			put(tmp, jo.get(tmp));
		}
	}

	@Override
	public Object put(String key, Object value) {
		// TODO do we want it to save to file here?
		return super.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		// TODO do we want it to save to file here?
		super.putAll(m);
	}

	/**
	 * Save the session settings to file.
	 * <p>
	 * Session settings will be saved, provided that the file location to be
	 * saved is in the settings and is not null. Otherwise, will throw an
	 * exception. Ideally this should be used when the node is shutdown, or
	 * crashes for whatever reason.
	 * 
	 * @throws ConfigurationException
	 */
	public void saveSessionToFile() throws ConfigurationException {
		synchronized (writeLock) {
			// TODO check to see if they are any different than default??
			JSONObject tmp = new JSONObject();
			for (Map.Entry<String, Object> e : entrySet()) {
				tmp.put(e.getKey(), e.getValue());
			}
			try {
				write(new File((String) this.get(savefile_keyname)), tmp);
			} catch (NullPointerException e) {
				throw new SettingSaveException();
			}
		}
	}

	private JSONObject read(File in) throws ConfigurationException {
		String content = null;
		JSONObject jo = null;
		try {
			byte[] chars;
			chars = Files.readAllBytes(in.toPath());
			content = new String(chars);
			jo = new JSONObject(content);
		} catch (IOException e) {
			throw new SettingNotFoundException();
		} catch (JSONException e) {
			throw new SettingFormatException();
		}
		return jo;
	}

	private void write(File out, JSONObject jo) throws ConfigurationException {

		try {
			FileWriter fw = new FileWriter(out);
			fw.write(jo.toString());
			fw.flush();
			fw.close();
		} catch (IOException e) {
			throw new SettingSaveException();
		}

	}
}
