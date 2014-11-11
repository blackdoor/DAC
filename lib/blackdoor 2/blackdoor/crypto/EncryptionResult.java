package blackdoor.crypto;

import java.io.Serializable;

import blackdoor.util.Misc;

public class EncryptionResult implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6451163680434801851L;
	private byte[] text;
	private byte[] iv;
	/**
	 * @param text
	 * @param iv
	 */
	public EncryptionResult(byte[] iv, byte[] text) {
		//super();
		this.text = text;
		this.iv = iv;
	}
	
	public EncryptionResult(byte[] simpleSerial){
		int ivLength = simpleSerial[0];
		int outputLength = simpleSerial.length - ivLength -1;
		iv = new byte[ivLength];
		text = new byte[outputLength];
		System.arraycopy(simpleSerial, 1, iv, 0, ivLength);
		System.arraycopy(simpleSerial, ivLength + 1, text, 0, outputLength);
	}
	
	/**
	 * needs testing
	 * @return the encryption result as a byte array in the form (ivLength|iv|ciphertext) 
	 */
	public byte[] simpleSerial(){
		byte[] out = new byte[text.length + iv.length + 1];
		out[0] = (byte) iv.length;
		System.arraycopy(iv, 0, out, 1, iv.length);
		System.arraycopy(text, 0, out, iv.length + 1, text.length);
		return out;
	}
	
	/**
	 * @return the cipherText
	 */
	public byte[] getText() {
		return text;
	}
	
	/**
	 * @return the iv
	 */
	public byte[] getIv() {
		return iv;
	}
	
	@Override
	public String toString() {
		return "EncryptionResult [iv="
				+ Misc.bytesToHex(iv) + "[text=" + Misc.bytesToHex(text)+ "]\n" + Misc.bytesToHex(simpleSerial());
	}
}
