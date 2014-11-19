package blackdoor.cqbe.settings;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.2 - Nov 5, 2014
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
   * Returns a integer of the port.
   * <p>
   * 
   * @return
   */
  public static int Port() {
    JSONObject obj = load();
    return obj.getInt("port");
  }

  /**
   * Returns a string of the Inet6Address.
   * <p>
   * 
   * @return
   */
  public static String Address() {
    JSONObject obj = load();
    return obj.getString("ip");
  }

  /**
   * Returns a string of the overlayAddress.
   * <p>
   * 
   * @return
   */
  public static String OverlayAddress() {
    JSONObject obj = load();
    // do work?? or just get it from the thing
    return null;
  }

  /**
   * Returns a string of the path of the Node Directory.
   * <p>
   * 
   * @return
   */
  public static String NodeDirectory() {
    JSONObject obj = load();
    return obj.getString("node");
  }

  /**
   * Returns a string of the path of the Storage Directory.
   * <p>
   * 
   * @return
   */
  public static String StorageDirectory() {
    JSONObject obj = load();
    return obj.getString("storage");
  }

  /**
   * Returns a string of the path of the KeyStore Directory.
   * <p>
   * 
   * @return
   */
  public static String KeyStoreDirectory() {
    JSONObject obj = load();
    return obj.getString("keystore");
  }

  /**
   * Load JSONObject from Config File.
   * <p>
   * 
   * @return
   */
  private static JSONObject load() {
    String result = "";
    try (BufferedReader br = new BufferedReader(new FileReader(FILE_LOCATION))) {
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append(System.lineSeparator());
        line = br.readLine();
      }
      result = sb.toString();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return new JSONObject(result);
  }
}
