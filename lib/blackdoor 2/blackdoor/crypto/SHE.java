/**
 * 
 */
package blackdoor.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import blackdoor.struct.ByteQueue;
import blackdoor.util.Misc;

/**
 * @author nfischer3
 * Secure hash encryption, uses hash algorithms in CTR mode for mirrored, symmetric encryption.
 */
public class SHE {
	protected int blockSize;
	protected int keySize;
	private int blockNo = 0;
	private boolean cfg = false;
	private byte[] key;
	private ByteQueue buffer;
	private MessageDigest mD;
	private byte[] prehash;
	
	/**
	 * Gets a cipher with default algorithm configuration (SHA512, 64 byte block)
	 * @return An instance of an un-configured cipher
	 */
	public static SHE getInstance(){
		try {
			return new SHE("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets a cipher using the specified algorithm. The cipher will have a block size based on the chosen algorithm.
	 * 		Larger block sizes are typically faster when encrypting data larger than the block size.
	 * @param algorithm Any hash algorithm, see java's standard names document.
	 * @return An instance of an un-configured cipher
	 * @throws NoSuchAlgorithmException
	 */
	public static SHE getInstance(String algorithm) throws NoSuchAlgorithmException{
		return new SHE(algorithm);
	}
	
	protected SHE(String algorithm) throws NoSuchAlgorithmException{
		mD = MessageDigest.getInstance(algorithm);
		blockSize = mD.getDigestLength();
	}
	
	public boolean isConfigured() {
		return cfg;
	}

	/**
	 * Initializes the cipher with key, creates a random IV to use with the cipher.
	 * @param key A key to encrypt with. Key can be any length but a key longer than the block size will run more slowly. 
	 * @return An IV that has been created for this cipher to use. IV will be the same length as the key.
	 */
	public byte[] init(byte[] key){
		keySize = key.length;
		byte[] iv = new byte[keySize];
		new SecureRandom().nextBytes(iv);
		init(iv, key);
		return iv;
	}
	
	/**
	 * Initializes the cipher with key and IV
	 * @param IV An initialization vector to use for the cipher.
	 * @param key A key to encrypt with.
	 */
	public void init(byte[] IV, byte[] key) {
		if(IV.length != key.length)
			throw new Exceptions.InvalidKeyException("Key and IV size must be equal.");
		keySize = key.length;
		this.key = key;
		prehash = Misc.cleanXOR(IV, key);
		blockNo = 0;
		buffer = new ByteQueue(blockSize*2);
		buffer.setResizable(true);
		cfg = true;
	}
	
	/**
	 * Continues a multiple-part encryption or decryption operation (depending on how this cipher was initialized), processing another data part.
	 * The bytes in the input buffer are processed, and the result is stored in a new buffer.
	 *
	 * If input has a length of zero, this method returns null.
	 * @param input
	 * @return
	 */
	public byte[] update(byte[] input){
		if(!cfg)
			throw new Exceptions.CipherNotInitializedException();
		if(input.length == 0)
			return null;
		if(input.length > buffer.capacity())
			buffer.resize(input.length + blockSize);
		while(buffer.filled() < input.length){
			bufferKeystream();
		}
		return Misc.XORintoA(buffer.deQueue(input.length), input);
	}
	
	/**
	 * Zero's out memory where the key is stored.
	 * After calling this method init() needs to be called again.
	 */
	public void zeroKey(){
		for(int i = 0; i < key.length; i++){
			key[i] = 0x0;
		}
		for(int i = 0; i < prehash.length; i++){
			key[i] = 0x0;
		}
		cfg = false;
	}
	
	protected void bufferKeystream(){
		int i = blockNo % keySize;
		int inc = (blockNo/keySize) + 1;
		prehash[i] ^= key[i];					// expose IV[i] in prehash
		prehash[i] += inc;	// apply ctr
		prehash[i] ^= key[i];					// cover IV[i] in prehash with key[i]
		buffer.enQueue(mD.digest(prehash));		// buffer keystream
		prehash[i] ^= key[i];					// expose IV[i[ in prehash
		prehash[i] -= inc;	// remove ctr
		prehash[i] ^= key[i];					// cover IV[i[ in prehash with key[i]
		blockNo++;
	}

}
