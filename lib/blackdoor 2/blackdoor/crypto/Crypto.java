/**
 * 
 */
package blackdoor.crypto;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * @author kAG0
 * dead simple Hashing and Crypto
 * AES credit to erickson (stackoverflow.com/users/3474/erickson)
 */
public class Crypto {
	private byte[] secretPlain;
	private byte[] secretCipher = null;
	private KeySpec spec;
	private SecretKey secretKey;
	private Cipher cipher;
	private byte[] password;
	private byte[] salt = null;
	private byte[] iv;
	private boolean initAESDone;
	private int strength;
	
	public Crypto(){
		clearPlain();
		initAESDone = false;
		strength = 256;
	}
	
	public Crypto(byte[] input){
		clearPlain();
		initAESDone = false;
		this.secretPlain = input;
		strength = 256;
	}
	
	public void updateSecret(byte[] input){
		this.secretPlain = input;
	}
	
	/**
	 * @return the strength
	 */
	public int getStrength() {
		return strength;
	}

	/**
	 * @param strength the strength to set
	 * @throws InvalidKeyLengthException 
	 */
	public void setStrength(int strength) throws InvalidKeyLengthException {
		if(!(strength == 128 ||strength == 192||strength == 256)){
			throw new InvalidKeyLengthException(strength + " bit keys are not supported.");
		}
		this.strength = strength;
	}

	public byte[] getIV(){
		return iv;
	}
	
	public void setIV(byte[] iv){
		this.iv=iv;
	}
	public void setSalt(byte[] salt){
		this.salt=salt;
	}
	
	public byte[] getSalt(){
		return salt;
	}
	
	public byte[] getSecretCipher(){
		return secretCipher;
	}
	
	public void initAES() throws Exception{
		if(password == null)
			throw new Exception("password is null");
		SecretKeyFactory factory = null;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(salt == null)
			salt = SecureRandom.getSeed(8);
		String pass=null;
		try {
			pass = new String(password, "UTF-16");
		} catch (UnsupportedEncodingException e) {
			System.err.println("for some reason UTF-16 is not a valid encoding");
			e.printStackTrace();
		}
		try {
			int maxBits = Cipher.getMaxAllowedKeyLength("AES");
			if(maxBits < strength){
				System.out.println("This system only supports up to " + maxBits + " bit encryption, using that strength.");
				strength = maxBits;
			}
			spec = new PBEKeySpec(pass.toCharArray(), salt, 65536, strength);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		SecretKey tmp = null;
		try {
			tmp = factory.generateSecret(spec);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
		initAESDone = true;
	}
	
	public void initAES(byte[] password){
		this.password = password;
		SecretKeyFactory factory = null;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(salt == null)
			salt = SecureRandom.getSeed(8);
		String pass=null;
		try {
			pass = new String(password, "UTF-16");
		} catch (UnsupportedEncodingException e) {
			System.err.println("for some reason UTF-16 is not a valid encoding");
			e.printStackTrace();
		}
		try {
			int maxBits = Cipher.getMaxAllowedKeyLength("AES");
			if(maxBits < strength){
				System.out.println("This system only supports up to " + maxBits + " bit encryption, using that strength.");
				strength = maxBits;
			}
			spec = new PBEKeySpec(pass.toCharArray(), salt, 65536, strength);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		SecretKey tmp = null;
		try {
			tmp = factory.generateSecret(spec);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
		initAESDone = true;
	}
	/**
	 * clears all plaintext passwords and secrets. password, secret and initAES must all be set before re-using
	 * @throws Exception
	 */
	public void doAESEncryption() throws Exception{
		if(!initAESDone)
			initAES();
		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		//System.out.println(secretKey.getEncoded());
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		AlgorithmParameters params = cipher.getParameters();
		iv = params.getParameterSpec(IvParameterSpec.class).getIV();
		secretCipher = cipher.doFinal(secretPlain);
		clearPlain();
	}
	
	public void doAESDecryption(byte[] salt, byte[] iv) throws Exception{
		setSalt(salt);
		if(!initAESDone)
			initAES();
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
		secretPlain = cipher.doFinal(secretCipher);
	}
	
	/**
	 * @return the secretPlain
	 */
	public byte[] getSecretPlain() {
		return secretPlain;
	}

	/**
	 * @param secretPlain the secretPlain to set
	 */
	public void setSecretPlain(byte[] secretPlain) {
		this.secretPlain = secretPlain;
	}

	/**
	 * @param secretCipher the secretCipher to set
	 */
	public void setSecretCipher(byte[] secretCipher) {
		this.secretCipher = secretCipher;
	}

	public static EncryptionResult getAESEncryption(byte[] secret, byte[] password, int strength) throws InvalidKeyLengthException{
		Crypto agent = new Crypto(secret);
		agent.setStrength(strength);
		agent.initAES(password);
		try {
			agent.doAESEncryption();
		} catch (Exception e) {
			e.printStackTrace();
		}
		EncryptionResult result = new EncryptionResult(agent.getSecretCipher(), agent.getIV(), agent.getSalt());
		return result;
	}
	
	public static byte[] getAESDecryption(byte[] secret, byte[] password, byte[] salt, byte[] iv, int strength) throws InvalidKeyLengthException{
		Crypto agent = new Crypto();
		agent.setStrength(strength);
		agent.setSalt(salt);
		agent.setSecretCipher(secret);
		agent.initAES(password);
		try {
			agent.doAESDecryption(salt, iv);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] secretResult = agent.getSecretPlain();
		agent.clearPlain();
		return secretResult;		
	}
	
	public void clearPlain(){
		secretPlain = null;
		password = null;
		spec = null;
		secretKey = null;
		cipher = null;
	}
	
	public static class EncryptionResult implements Serializable{
		private byte[] output;
		private byte[] iv;
		private byte[] salt;
		/**
		 * @param output
		 * @param iv
		 */
		public EncryptionResult(byte[] output, byte[] iv, byte[] salt) {
			super();
			this.output = output;
			this.iv = iv;
			this.salt = salt;
		}
		
		public EncryptionResult(byte[] simpleSerial){
			int ivLength = simpleSerial[0];
			int outputLength = simpleSerial.length - ivLength;
			iv = new byte[ivLength];
			output = new byte[outputLength];
			System.arraycopy(simpleSerial, 1, iv, 0, ivLength);
			System.arraycopy(simpleSerial, ivLength, output, 0, outputLength);
		}
		
		/**
		 * needs testing
		 * @return the encryption result as a byte array in the form (ivLength|iv|ciphertext) 
		 */
		public byte[] simpleSerial(){
			byte[] out = new byte[output.length + iv.length];
			out[0] = (byte) iv.length;
			System.arraycopy(iv, 0, out, 1, iv.length);
			System.arraycopy(output, 0, out, iv.length, output.length);
			return out;
		}
		
		
		/**
		 * @return the cipherText
		 */
		public byte[] getOutput() {
			return output;
		}
		
		/**
		 * @return the iv
		 */
		public byte[] getIv() {
			return iv;
		}
		
		public byte[] getSalt(){
			return salt;
		}
	}
	public class InvalidKeyLengthException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * 
		 */
		public InvalidKeyLengthException() {
			super();
			// TODO Auto-generated constructor stub
		}

		/**
		 * @param message
		 * @param cause
		 * @param enableSuppression
		 * @param writableStackTrace
		 */
		public InvalidKeyLengthException(String message, Throwable cause,
				boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
			// TODO Auto-generated constructor stub
		}

		/**
		 * @param message
		 * @param cause
		 */
		public InvalidKeyLengthException(String message, Throwable cause) {
			super(message, cause);
			// TODO Auto-generated constructor stub
		}

		/**
		 * @param message
		 */
		public InvalidKeyLengthException(String message) {
			super(message);
			// TODO Auto-generated constructor stub
		}

		/**
		 * @param cause
		 */
		public InvalidKeyLengthException(Throwable cause) {
			super(cause);
			// TODO Auto-generated constructor stub
		}
		
	}
}
