package blackdoor.cqbe.keystore;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 10, 2014
 */
public class KeyStoreException extends Exception {
  /**
   * 
   * @author Cj Buresch
   * @version v0.0.1 - Nov 10, 2014
   */
  public static class FailedDecryption extends KeyStoreException {

  }
  /**
   * 
   * @author Cj Buresch
   * @version v0.0.1 - Nov 10, 2014
   */
  public static class FailedEncryption extends KeyStoreException {

  }
}
