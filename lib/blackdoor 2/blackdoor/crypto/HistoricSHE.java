/**
 * 
 */
package blackdoor.crypto;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.BufferUnderflowException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import blackdoor.struct.ByteQueue;
import blackdoor.util.Misc;

/**
 * @author nfischer3
 * Secure Hash Encryption. SHA256 in CTR mode implemented with methods similar to the standard Crypto.java library.
 */
public class HistoricSHE {
	
	public int blockSize;// = 32;
	private int blockNo;
	private byte[] IV;
	private byte[] key;
	private byte[] prehash;
	private boolean cfg;
	private byte[] buffer = new byte[blockSize];
	private int bufferIndex; //index at which to place next byte in buffer
	private MessageDigest mD;
	
	/**
	 * Creates a Cipher object with specified algorithm.
	 * @param algorithm the algorithm to use for this cipher.
	 */
	public HistoricSHE(String algorithm) throws NoSuchAlgorithmException{
		blockNo = 0;
		cfg = false;
		mD = MessageDigest.getInstance(algorithm);
		blockSize = mD.getDigestLength();
	}
	
	/**
	 * Creates a Cipher object.
	 */
	public HistoricSHE(Algorithm algorithm){
		blockNo = 0;
		cfg = false;
		try {
			mD = MessageDigest.getInstance(algorithm.getAlgorithm());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		blockSize = mD.getDigestLength();
	}
	/**
	 * Creates a Cipher object.
	 */
	public HistoricSHE(){
		blockSize = 32;
		blockNo = 0;
		cfg = false;
		try {
			mD = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes the cipher with key, creates a random IV to use with the cipher.
	 * @param key A 256 bit key to encrypt with.
	 * @return A 256 bit IV that has been created for this cipher to use.
	 */
	public byte[] init(byte[] key){
		byte[] iv = new byte[blockSize];
		new SecureRandom().nextBytes(iv);
		init(iv, key);
		return iv;
	}
	
	/**
	 * Initializes the cipher with key and IV
	 * @param IV A 256 bit initialization vector to use for the cipher.
	 * @param key A 256 bit key to encrypt with.
	 */
	public void init(byte[] IV, byte[] key){
		if(IV.length != blockSize || key.length != blockSize)
			throw new RuntimeException("key and IV need to be same as block size (" + blockSize + ")."); //TODO subclass exception
		this.key = key;
		this.IV = IV;
		prehash = Misc.cleanXOR(IV, key);
		cfg = true;
		blockNo = 0;
		buffer = new byte[blockSize];
		bufferIndex = 0;
	}
	
	private byte[] cryptBlock(){
		//byte[] iv = Arrays.copyOf(IV, IV.length);// + BLOCKSIZE);
		//System.arraycopy(IV, 0, iv, 0, BLOCKSIZE);
		//iv[blockNo % blockSize] += blockNo + 1;
		
		byte[] ret;
		int i = blockNo % blockSize;
		int inc = (blockNo/blockSize) + 1;
		prehash[i] ^= key[i];					// expose IV[i] in prehash
		prehash[i] += inc;	// apply ctr
		prehash[i] ^= key[i];					// cover IV[i] in prehash with key[i]
		ret = Misc.XORintoA(mD.digest(prehash), buffer);
		prehash[i] ^= key[i];					// expose IV[i[ in prehash
		prehash[i] -= inc;	// remove ctr
		prehash[i] ^= key[i];					// cover IV[i[ in prehash with key[i]
		
		
		//iv[blockNo % blockSize] += (blockNo/blockSize) + 1; // this way allows more blocks
		//System.out.println(Misc.bytesToHex(iv));
		//iv = Misc.cleanXOR(iv, key); //this line runs much faster than the following two lines because the following make a larger IV which takes longer to digest
		//iv = Arrays.copyOf(iv, blockSize + iv.length);
		//System.arraycopy(key, 0, iv, blockSize, blockSize);
		return ret;//Misc.cleanXOR(buffer, mD.digest(iv));
	}
	
	public boolean isConfigured(){
		return cfg;
	}
	
//	public byte[] updateWithInterrupts(byte[] input){
//		if(!cfg){
//			throw new RuntimeException("Cipher not configured.");
//		}
//		int numBlocks = (int) Math.floor((input.length + bufferIndex)/BLOCKSIZE);
//		byte[] out = new byte[numBlocks*BLOCKSIZE];
//		
//		for(int i=0; i < input.length; i++){
//			try{
//				buffer[bufferIndex++] = input[i];
//			}catch(IndexOutOfBoundsException e){
//				bufferIndex = 0;
//				i--;
//				//System.out.println(Misc.bytesToHex(buffer));
//				System.arraycopy(cryptBlock(), 0, out, blockNo*BLOCKSIZE, BLOCKSIZE);
//				blockNo++;
//				buffer = new byte[BLOCKSIZE];
//			}
//		}
//		if(bufferIndex == 32){
//			bufferIndex = 0;
//			System.arraycopy(cryptBlock(), 0, out, blockNo*BLOCKSIZE, BLOCKSIZE);
//			buffer = new byte[BLOCKSIZE];
//		}
//		//System.out.println(bufferIndex);
//		//System.out.println(Misc.bytesToHex(out));
//		return out;
//	}
	
	/**
	 * Continues a multiple-part encryption or decryption operation (depending on how this cipher was initialized), processing another data part.
	 * The bytes in the input buffer are processed, and the result is stored in a new buffer.
	 *
	 * If input has a length of zero, this method returns null.
	 * @param input
	 * @return
	 */
	public byte[] update(byte[] input){
		if(!cfg){
			throw new RuntimeException("Cipher not configured.");//TODO
		}
		if(input.length == 0)
			return new byte[]{};//null;
		if(bufferIndex != 0){
			byte[] in2 = Arrays.copyOf(buffer, input.length + bufferIndex);//new byte[input.length + bufferIndex];
			//System.out.println(Misc.bytesToHex(in2));
			//System.arraycopy(buffer, 0, in2, 0, bufferIndex);
			System.arraycopy(input, 0, in2, bufferIndex, input.length);
			input = in2;
		}
		
		int numBlocks = (int) Math.floor(input.length/blockSize);
		//System.out.println(numBlocks);
		byte[] out = new byte[blockSize * numBlocks];
		for(int i = 0; i < numBlocks; i++){
			//System.out.println("i:"+i+" block:" + blockNo);
			System.arraycopy(input, blockSize*i, buffer, 0, blockSize);
			System.arraycopy(cryptBlock(), 0, out, i * blockSize, blockSize);
			blockNo++;
		}
		buffer = new byte[blockSize];
		if(input.length % blockSize == 0){
			
			bufferIndex = 0;
		}else{
			//buffer = new byte[BLOCKSIZE];
			System.arraycopy(input, numBlocks*blockSize, buffer, 0, input.length - numBlocks*blockSize);
			bufferIndex = input.length - numBlocks*blockSize;
		}
		//System.out.println(Misc.bytesToHex(out));
		return out;
	}
//	public byte[] doFinalWithInterrupts(byte[] input){
//		byte[] main = updateWithInterrupts(input);
//		byte[] out;
//		//if buffer isn't empty add a padding indicator to the end of data
//		if(bufferIndex != 0){
//			
//			buffer[bufferIndex] = 0x69;
//			bufferIndex++;
//			//System.out.println(Misc.bytesToHex(buffer));
//			buffer = cryptBlock();
//			//add buffer to end of main
//			out = new byte[main.length + buffer.length];
//			System.arraycopy(main, 0, out, 0, main.length);
//			System.arraycopy(buffer, 0, out, main.length, buffer.length);
//		}else{
//			//remove padding
//			int endIndex = main.length-1 ;
//			while(main[endIndex] == 0 || main[endIndex] == 0x69){
//				endIndex --;
//				if(main[endIndex] == 0x69){
//					endIndex--;
//					break;
//				}
//			}
//			//System.out.println("endindex " + endIndex);
//			out = new byte[endIndex + 1];
//			System.arraycopy(main, 0, out, 0, endIndex+1);
//		}
//				
//		blockNo = 0;
//		IV = null;
//		key = null;
//		cfg = false;
//		bufferIndex = 0;
//		
//		return out;
//	}
	/**
	 * Encrypts or decrypts data in a single-part operation, or finishes a multiple-part operation.
	 * The bytes in the input buffer, and any input bytes that may have been buffered during a previous update operation, are processed, with padding (if requested) being applied. 
	 *
	 * Upon finishing, this method resets this cipher object to the state it was in before initialized via a call to init. That is, the object is reset and needs to be re-initialized before it is available to encrypt or decrypt more data.
	 * @return the new buffer with the result
	 */
	public byte[] doFinal(){
		return doFinal(new byte[]{});
	}
	
	/**
	 * Encrypts or decrypts data in a single-part operation, or finishes a multiple-part operation.
	 * The bytes in the input buffer, and any input bytes that may have been buffered during a previous update operation, are processed, with padding (if requested) being applied. 
	 *
	 * Upon finishing, this method resets this cipher object to the state it was in before initialized via a call to init. That is, the object is reset and needs to be re-initialized before it is available to encrypt or decrypt more data.
	 * @param input the input buffer
	 * @return the new buffer with the result
	 */
	public byte[] doFinal(byte[] input){
		byte[] main = update(input);
		byte[] out;
		//if buffer isn't empty add a padding indicator to the end of data
		if(bufferIndex != 0){
			
			buffer[bufferIndex] = 0x69;
			bufferIndex++;
			//System.out.println(Misc.bytesToHex(buffer));
			buffer = cryptBlock();
			//add buffer to end of main
			out = new byte[main.length + buffer.length];
			System.arraycopy(main, 0, out, 0, main.length);
			System.arraycopy(buffer, 0, out, main.length, buffer.length);
		}else{
			//remove padding
			int endIndex = main.length-1 ;
			while(main[endIndex] == 0 || main[endIndex] == 0x69){
				endIndex --;
				if(endIndex != 0 && main[endIndex] == 0x69){ //TODO removing padding needs fixing
					endIndex--;
					break;
				}
			}
			//System.out.println("endindex " + endIndex);
			out = new byte[endIndex + 1];
			System.arraycopy(main, 0, out, 0, endIndex+1);
		}
				
		blockNo = 0;
		IV = null;
		key = null;
		cfg = false;
		bufferIndex = 0;
		
		return out;
	}
	
	public static class EncryptedOutputStream extends FilterOutputStream{
		private HistoricSHE cipher;
		/**
		 * If true, when this stream is closed using its close() method, any 
		 * unprocessed data will be padded and then encrypted.
		 * If false, when this stream is closed any unprocessed data will be discarded.
		 */
		public boolean padOnClose;
		
		/**
		 * Constructs an EncryptedOutputStream from an OutputStream and a Cipher. 
		 * Note: if the specified output stream or cipher is null, a NullPointerException may be thrown later when they are used.
		 * @param out the OutputStream object
		 * @param cipher an initialized Cipher object
		 */
		public EncryptedOutputStream(OutputStream out, HistoricSHE cipher) {
			super(out);
			if(!cipher.isConfigured())
				throw new RuntimeException("Cipher not configured.");
			padOnClose = false;
			this.cipher = cipher;
		}
		/**
		 * Writes the specified byte to this output stream.
		 */
		@Override
		public void write(int b) throws IOException{
			out.write(cipher.update(new byte[]{(byte) b}));
		}
		
		
		@Override
		public void write(byte[] b) throws IOException{
			//byte[] debug;// = new byte[b.length];
			//debug = ;
			//System.out.print(Misc.bytesToHex(debug));
			out.write(cipher.update(b));
			//return debug;
		}
		
		public void write(byte[] b, int off, int len) throws IOException{
			byte[] todo = new byte[len];
			System.arraycopy(b, off, todo, 0, len);
			write(todo);
		}
		
		//@Override
	//	public void flush() throws IOException{
			//write(cipher.doFinal());
			//out.flush();
		//}
		@Override
		public void close() throws IOException{
			if(padOnClose)
				out.write(cipher.doFinal());
			out.close();
		}
		/**
		 * 
		 * @return the underlying SHE cipher for this Stream
		 */
		public HistoricSHE getCipher(){
			return cipher;
		}
		
	}
	
	public static class EncryptedInputStream extends FilterInputStream{
		private HistoricSHE cipher;
		private ByteQueue buffer;
		
		public EncryptedInputStream(InputStream in, HistoricSHE cipher) {
			super(in);
			if(!cipher.isConfigured())
				throw new RuntimeException("Cipher not configured.");
			this.cipher = cipher;
			buffer = new ByteQueue(cipher.blockSize*2);
			buffer.setResizable(true);
		}
		
		private void bufferBlock() throws IOException{
			byte[] plainText = new byte[cipher.blockSize];
			in.read(plainText);
			buffer.enQueue(cipher.update(plainText));
		}
		
		private void bufferBytes(int size) throws IOException{
			while(buffer.filled() < size){
				bufferBlock();
				}
		}
		
		public int read() throws IOException{
			try{
				return buffer.deQueue(1)[0];
			}catch(BufferUnderflowException e){
				bufferBlock();
				return read();
			}
		}
		
		public int read(byte[] b) throws IOException{
			return read(b, 0, b.length);
		}
		
		public int read(byte[]b, int off, int len) throws IOException{
			bufferBytes(len-off);
			buffer.deQueue(b, off, len);
			return len-off;
		}
		
	}
	
	public static class EncryptionResult implements Serializable{
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
	public class Algorithm{
		public static final String SHA1 = "SHA-1";
		public static final String SHA256 = "SHA-256";
		public static final String SHA384 = "SHA-384";
		public static final String SHA512 = "SHA-512";
		
		private String algo;
		public Algorithm(String algorithm){
			if(!algorithm.equals(SHA1) || !algorithm.equals(SHA256) || 
					!algorithm.equals(SHA384) || !algorithm.equals(SHA512))
				throw new RuntimeException("Invalid algorithm " + algorithm);
			algo = algorithm;
		}
		public String getAlgorithm() {
			return algo;
		}
		public void setAlgorithm(String algo) {
			if(!algo.equals(SHA1) || !algo.equals(SHA256) || 
					!algo.equals(SHA384) || !algo.equals(SHA512))
				throw new RuntimeException("Invalid algorithm " + algo);
			this.algo = algo;
		}
		
	}
}
