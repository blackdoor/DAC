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
 * @version v0.1.2 - Feb 22, 2015
 */
public class Config extends ConcurrentHashMap<String, Object> {

	private Object writeLock = new Object();

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
		JSONObject jo = ConfigReadOnly.read(settings);
		Iterator<String> keys = jo.keys();
		String tmp;
		while (keys.hasNext()) {
			tmp = keys.next();
			super.put(standardizeKey(tmp), jo.get(tmp));
		}
	}

	/**
	 * Returns value for the key in the settings.
	 * <p>
	 * Also ensures that keys follow the standard config file conventions.
	 */
	@Override
	public Object get(Object key) {
		return super.get(standardizeKey((String) key));
	}

	/**
	 * Places a value for the key in the settings.
	 * <p>
	 * Also ensures that things follows the config file conventions.
	 */
	@Override
	public Object put(String key, Object value) {
		Object tmp = super.put(standardizeKey(key), value);
		saveSessionToFile();
		return tmp;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		super.putAll(m);
		saveSessionToFile();
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
			JSONObject tmp = new JSONObject();
			for (Map.Entry<String, Object> e : entrySet()) {
				tmp.put(e.getKey(), e.getValue());
			}
			try {
				//TODO timestamp savefile or something
				write(new File((String) this.get("save_file")), tmp);
			} catch (NullPointerException e) {
				throw new SettingSaveException();
			}
		}
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

	private static String standardizeKey(String key) {
		return key.toLowerCase().replace(' ', '_');
	}

	/**
	 * Allows for settings to be grabbed from a config file without
	 * instantiating a Configuration object.
	 * <p>
	 * 
	 * @author Cj Buresch
	 * @version 0.0.1 -- Feb 21, 2015
	 */
	public static class ConfigReadOnly {

		/**
		 * Returns a single setting from a setting file.
		 * <p>
		 * Primarily for people who are feeling super lazy. Does a couple
		 * checks, but in the event that things go wrong, it will throw a
		 * configuration exception.
		 * 
		 * @param filename
		 * @param setting
		 * @return
		 * @throws ConfigurationException
		 */
		public static Object get(String filename, String setting)
				throws ConfigurationException {
			File file = new File(filename);
			return get(file, setting);
		}

		/**
		 * Returns a single setting from a setting file.
		 * <p>
		 * For people who are feeling less lazy and already have a file.
		 * 
		 * @param file
		 * @param setting
		 * @return
		 * @throws ConfigurationException
		 */
		public static Object get(File file, String setting)
				throws ConfigurationException {
			JSONObject jo = read(file);
			if (jo.has(standardizeKey(setting)))
				return jo.get(standardizeKey(setting));
			else
				throw new SettingNotFoundException();
		}

		private static JSONObject read(File file) {
			String content = null;
			JSONObject jo = null;
			try {
				byte[] chars;
				chars = Files.readAllBytes(file.toPath());
				content = new String(chars);
				jo = new JSONObject(content);
			} catch (IOException e) {
				throw new ConfigFileNotFoundException();
			} catch (JSONException e) {
				throw new ConfigFileFormatException();
			}
			return jo;
		}
	}
}
