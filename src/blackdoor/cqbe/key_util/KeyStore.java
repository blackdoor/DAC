package blackdoor.cqbe.key_util;

/**
 * 
 * This Class handles the file io, encryption and retrieval of all keys from the
 * default/configured store location for a user's private keys.
 * <p>
 * 
 * @author Cj Buresch
 * @version 0.0.1, 2014 Oct 31, 2014
 */
public class KeyStore {
	/**
	 * 
	 */
	public KeyStore() {
	}

	/**
	 * Prepares Keystore for operation.
	 * 
	 * <p> Called perhaps in the constructor. If a keystore does not already exist,
	 * it will initialize with. The main function of this is to prepare an
	 * algorithm to be used to either decrypt or encrypt. This might also be
	 * done in the constructor???
	 */
	public void init() {
	}

	/**
	 * Encrypts Keystore and saves it to disk at the default or configured
	 * location.
	 * 
	 * <p>Takes a list of keys and alias held in this class and formats them into a
	 * readable file for later. Encrypts it with a user entered password,
	 * 
	 * TODO need to figure out where and how the user password comes from
	 * 
	 */
	public void load() { // maybe rename to "open"
	}

	/**
	 * Using a user entered password, grab the keystore and unecrypt it. Load it
	 * into this this classes map of keys and private keys
	 */
	public void store() { // maybe rename to "close"
	}

	/**
	 * Returns a Private Key from the KeyStore.
	 * 
	 * @param Alias
	 *            Is the name of the user for the private key. Or the subject
	 *            name on the certificate.
	 * @return PrivateKey Returns the private key of a user. probably need to
	 *         figure out how I'd like that to be formated. IF it needs to be a
	 *         string, byte array or something else.
	 */
	public void getPrivateKey(String alias) {
	}

	/**
	 * Returns a Private Key from the KeyStore.
	 * 
	 * @param Alias
	 *            The subject name or alias of a certificate that was created.
	 * @return Boolean Returns true or false depending on whether or not the
	 *         alias exists in the keystore
	 * 
	 */
	public void containsAlias() {
	}

	/**
	 * Creates a new entry into the keystore for the use
	 * 
	 * @param Alias
	 *            The subject name or alias of a certificate that was created.
	 * @param PrivateKey
	 *            Is the private key of the alias, generated at the creation of
	 *            a certificate.
	 * @param DATE
	 *            TODO Might need to format this? or perhaps just figure out how
	 *            to parse a string into a date/calendar object so taht it can
	 *            be compared or something
	 */
	public void newEntry() {
	}

	/**
	 * 
	 */
	public void deleteEntry() {
	}

	/**
	 * 
	 */
	public void updateEntry() {
	}

	/**
	 * 
	 */
	public void getEntryDate() {
	}

	/**
	 */
	public void size() {
	}

}
