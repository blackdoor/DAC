package blackdoor.cqbe.keystore;

import java.io.File;
import java.util.ArrayList;

import blackdoor.util.CommandLineParser.Argument;

/**
 * 
 * This Class handles the file io, encryption and retrieval of all keys from the default/configured
 * store location for a user's private keys.
 * <p>
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 5, 2014
 */
public class KeyStore {
  /**
	 * 
	 */
  private File in;
  private File out;
  private String password;
  private boolean init_flag = false;

  public KeyStore() {}

  /**
   * Prepares Keystore for operation.
   * 
   * <p>
   * Called perhaps in the constructor. If a keystore does not already exist, it will initialize
   * with. The main function of this is to prepare an algorithm to be used to either decrypt or
   * encrypt. This might also be done in the constructor???
   */
  public void init() {}

  /**
   * Reads from configured location, and decrypts the keystore with the given password.
   * 
   * <p>
   * Takes a list of keys and alias held in this class and formats them into a readable file for
   * later. Encrypts it with a user entered password,
   * 
   * @param Password
   *        Likely is a byte array. So that will need to be handled
   * 
   */
  public void load() {}

  /**
   * Encrypts the keystore, and writes it to disk in the configured location.
   * 
   * <p>
   * Takes a list of keys and alias held in this class and formats them into a readable file for
   * later. Encrypts it with a user entered password,
   * 
   * @param password
   * @param out
   * 
   * @param Password
   *        Likely is a byte array. So that will need to be handled s
   */
  public void store() {}

  /**
   * Returns a Private Key from the KeyStore.
   * <p>
   * 
   * @param Alias
   *        Is the name of the user for the private key. Or the subject name on the
   *        certificate.
   * @return PrivateKey Returns the private key of a user. probably need to figure out how I'd like
   *         that to be formated. IF it needs to be a string, byte array or something else.
   */
  public void getPrivateKey(String alias) {}


  /**
   * Returns a Private Key from the KeyStore.
   * <p>
   * 
   * @param string
   * 
   * @param Alias
   *        The subject name or alias of a certificate that was created.
   * @return Boolean Returns true or false depending on whether or not the alias exists in the
   *         keystore
   */
  public boolean containsAlias(String string) {
    return false;
  }

  /**
   * Creates a new entry into the keystore for the use
   * <p>
   * 
   * @param string
   * 
   * @param Alias
   *        The subject name or alias of a certificate that was created.
   * @param PrivateKey
   *        Is the private key of the alias, generated at the creation of a certificate.
   * @param DATE
   *        TODO Might need to format this? or perhaps just figure out how to parse a stkrring
   *        into a date/calendar object so taht it can be compared or something
   */
  public void newEntry(String string) {}

  /**
   * Deletes an entry from the keystore.
   * <p>
   * 
   * @param Alias
   *        The subject name or alias of a certificate that was created.
   */
  public void deleteEntry() {}

  /**
   * Deletes an entry from the keystore.
   * <p>
   * Might need to overload this for different kind of updating.
   * 
   * @param Alias
   *        The subject name or alias of a certificate that was created.
   * @param PrivateKey
   * @param DATE
   */
  public void updateEntry() {}

  /**
   * Returns the entry date for an alias.
   * <p>
   * 
   * @param Alias
   *        The subject name or alias of a certificate that was created.
   * @return The date an entry was added/date on the certificate.
   */
  public void getEntryDate() {}

  /**
   * Returns the number of entries in the keystore.
   * <p>
   * 
   * @return integer as the number of certificates in the keystore.
   */
  public void size() {}

  /*
   * GETTERS and SETTERS
   */
  /**
   * 
   * @return
   */
  public File getIn() {
    return in;
  }

  /**
   * 
   * @param in
   * @return
   */
  public KeyStore setIn(File in) {
    this.in = in;
    return this;
  }

  /**
   * 
   * @return
   */
  public File getOut() {
    return out;
  }

  /**
   * 
   * @param out
   * @return
   */
  public KeyStore setOut(File out) {
    this.out = out;
    return this;
  }

  /**
   * 
   * @return
   */
  public String getPassword() {
    return password;
  }

  /**
   * 
   * @param password
   * @return
   */
  public KeyStore setPassword(String password) {
    this.password = password;
    return this;
  }

  /**
   * 
   * @return
   */
  public boolean isInitialized() {
    return init_flag;
  }

  /**
   * 
   * @param init_flag
   */
  public KeyStore isnewKeyStore(boolean flag) {
    this.init_flag = flag;
    return this;
  }

}
