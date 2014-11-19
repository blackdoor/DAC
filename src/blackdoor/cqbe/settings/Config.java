package blackdoor.cqbe.settings;

import org.json.JSONObject;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 5, 2014
 */
public class Config {

	private static String FILE_LOCATION;

	/**
	 * 
	 * @param FILE_LOCATION
	 */
	public Config(String FILE_LOCATION) {
		this.FILE_LOCATION = FILE_LOCATION;
	}

	/**
	 * 
	 * @return
	 */
	public static int Port() {
		JSONObject obj = load();
		return obj.getInt("port");
	}

	/**
	 * 
	 * @return
	 */
	public static String Address() {
		JSONObject obj = load();
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public static String OverlayAddress() {
		JSONObject obj = load();
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public static String NodeDirectory() {
		JSONObject obj = load();
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public static String StorageDirectory() {
		JSONObject obj = load();
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public static String KeyStoreDirectory() {
		JSONObject obj = load();
		return null;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	private static JSONObject load() {
		return null;
	}
}
