package np1;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

public class CjRSA {
	public static void main(String[] args) throws Exception {
		// Security.addProvider(new
		// org.bouncycastle.jce.provider.BouncyCastleProvider());

		byte[] input = new byte[] { (byte) 0xbe, (byte) 0xef };
		Cipher cipher = Cipher.getInstance("RSA");

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(
				"12345678", 16), new BigInteger("11", 16));
		RSAPrivateKeySpec privKeySpec = new RSAPrivateKeySpec(new BigInteger(
				"12345678", 16), new BigInteger("12345678", 16));

		RSAPublicKey pubKey = (RSAPublicKey) keyFactory
				.generatePublic(pubKeySpec);
		RSAPrivateKey privKey = (RSAPrivateKey) keyFactory
				.generatePrivate(privKeySpec);

		cipher.init(Cipher.ENCRYPT_MODE, pubKey);

		byte[] cipherText = cipher.doFinal(input);
		System.out.println("cipher: " + new String(cipherText));

		cipher.init(Cipher.DECRYPT_MODE, privKey);
		byte[] plainText = cipher.doFinal(cipherText);
		System.out.println("plain : " + new String(plainText));
	}
}
