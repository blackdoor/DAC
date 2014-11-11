/**
 * 
 */
package blackdoor.crypto;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import blackdoor.crypto.Crypto.EncryptionResult;
import blackdoor.util.Misc;

/**
 * @author kAG0
 * a class containing simple static implementations of secure but non-standardized encryption algorithms
 */
public class ExperimentalCrypto {
	
	/**
	 * Calculate a single block with SimpleHashEncryption (see doSHE for details)
	 * @param block The block number, text and initialization vector for the block that is to be calculated
	 * @param key a 128 bit or stronger key
	 * @return an array containing the calculated block
	 */
	public static byte[] doSHEBlock(Block block, byte[] key){
		//make sure key is at least 128 bits.
		if(key.length < 16)
			throw new RuntimeException("Key too short");
		//make sure iv is at least 256 bits.
		if(block.getIV().length < 32)
			throw new RuntimeException("IV too short.");
		//make sure text block is exactly 256 bits.
		if(block.getText().length != 32)
			throw new RuntimeException("Block is not 256 bits.");
		
		//create a copy of the iv so we can increment it
		byte[] iv = new byte [32];
		System.arraycopy(block.getIV(), 0, iv, 0, iv.length);
		
		//increment the iv based on the block number
		iv[block.getBlockNo()%32] += block.getBlockNo()+1;
		
		return Misc.XOR(block.getText(), //xor the below with the text
				Hash.getSHA256( //hash the key salted iv
						Misc.XOR(key, iv))); // salt the iv with the key
	}
	
	/**
	 * Calculate a single block with SimpleHashEncryption (see doSHE for details). 
	 * Use doSHEBlock if the parameters need to be checked for size, null, etc.
	 * @param block The block number, text and initialization vector for the block that is to be calculated
	 * @param key a 128 bit or stronger key
	 * @return an array containing the calculated block
	 */
	private static byte[] getSHEBlock(Block block, byte[] key){
		//create a copy of the iv so we can increment it
		byte[] iv = new byte [32];
		System.arraycopy(block.getIV(), 0, iv, 0, iv.length);
		
		//increment the iv based on the block number
		iv[block.getBlockNo()%32] += block.getBlockNo()+1;
		
		return Misc.XOR(block.getText(), //xor the below with the text
				Hash.getSHA256( //hash the key salted iv
						Misc.XOR(key, iv))); // salt the iv with the key
	}
	
	/**
	 * Simple/Secure Hash Encryption encryption/decryption method which uses the 
	 * identical nature of the encryption and decryption algorithms in the 
	 * counter (CTR) mode of operation for block ciphers to use a one way hash 
	 * function instead of a typical encryption algorithm. SHE uses SHA256 with 
	 * a 256bit block size, and a key of at least 126 bits and an IV of at least 
	 * 256 bits. This method operates as a block cipher, 
	 * @param input The text to calculate, must be less that 2GB or 2^32 bytes, due to array restrictions in java.
	 * @param key The key to use for encryption/decryption, must be at least 128 bits.
	 * @param IV The initializaiton vector to use, must be at least 256 bits.
	 * @return An EncryptionResult containing the iv and calculated text.
	 */
	public static  EncryptionResult doSHE(byte[] input, byte[] key, byte[] IV){
		//List inputList;
		//pad input length to a multiple of 32
		if(input.length%32 != 0){
			int length = input.length;
			//Misc.PrintMemInfo(Runtime.getRuntime());
			input = Arrays.copyOf(input, (input.length/32+1)*32);
			input[length] = 69;
			//Misc.PrintMemInfo(Runtime.getRuntime());//System.gc();
		}
		int numBlocks = input.length/32;
		
		//make sure key is at least 128 bits
		if(key.length < 16)
			throw new RuntimeException("Key too short");
		
		// if initialization vecor is null, get a new CSRN for it
		if(IV == null){
			SecureRandom random = new SecureRandom();
			IV = new byte[32];
			random.nextBytes(IV);
		}else{ //if iv is not null make sure it is long enough
			if(IV.length < 32)
				throw new RuntimeException("IV too short."); //throw an error
		}
		byte[] tmp = new byte[32];
		Block block = new Block(0, null, IV);
		//loop through each block
		for(int i = 0; i < numBlocks; i++){
			//copy block into temp array
			System.arraycopy(input, i*32, tmp, 0, 32);
			//copy encrypted block back into input
			block.setBlockNo(i);
			block.setText(tmp);
			System.arraycopy(getSHEBlock(block, key), 0, input, i*32, 32);
			
		}
		tmp = null;
		block = null;
		System.gc();
		//trim any null bytes from end of array
		int endIndex = input.length -1 ;
		while(input[endIndex] == 0){
			endIndex --;
			if(input[endIndex] == 69){
				endIndex--;
				break;
			}
				
		}
		//byte out[] = new byte[endIndex+1];
		//System.arraycopy(input, 0, out, 0, endIndex+1);		
		return new EncryptionResult(Arrays.copyOf(input, endIndex+1), IV, null);
	}
	
	
	public static class Block{
		private int blockNo;
		private byte[] text;
		private byte[] IV;
		/**
		 * @param blockNo
		 * @param text
		 * @param iV
		 */
		public Block(int blockNo, byte[] text, byte[] iV) {
			super();
			this.blockNo = blockNo;
			this.text = text;
			IV = iV;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "SHEBlock [blockNo=" + blockNo + ", text="
					+ Arrays.toString(text) + ", IV=" + Arrays.toString(IV)
					+ "]";
		}
		/**
		 * @return the text
		 */
		public byte[] getText() {
			return text;
		}
		/**
		 * @param text the text to set
		 */
		public void setText(byte[] text) {
			this.text = text;
		}
		/**
		 * @return the iV
		 */
		public byte[] getIV() {
			return IV;
		}
		/**
		 * @param iV the iV to set
		 */
		public void setIV(byte[] iV) {
			IV = iV;
		}
		/**
		 * @return the blockNo
		 */
		public int getBlockNo() {
			return blockNo;
		}
		/**
		 * @param blockNo the blockNo to set
		 */
		public void setBlockNo(int blockNo) {
			this.blockNo = blockNo;
		}
		
	}
}
