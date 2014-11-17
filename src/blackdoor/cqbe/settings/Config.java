package blackdoor.cqbe.settings;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 5, 2014
 */
public class Config {

  // ////////////// Configurations ////////////////
  // Port that the node is running on
  public static int PORT = 1234;
  // Where the default keystore file is
  public static String KeyStoreDir = "";

  
  /*
   * GETTERS and SETTERS to EDIT configurations on the fly!
   */

  public static String getKeystoreDir() {
    return null;
  }

  public static void setPORT(int pORT) {
    PORT = pORT;
  }

  public static String getKeyStoreDir() {
    return KeyStoreDir;
  }

  public static void setKeyStoreDir(String keyStoreDir) {
    KeyStoreDir = keyStoreDir;
  }

  public static int getPORT() {
    return PORT;
  }

}
