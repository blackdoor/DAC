package np1.rsa;

import java.security.*;

public class Crsa {
	
	private KeyPair kp = null;
	
	public void init(){
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			kp = kpg.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}
	
	public byte[] getPublicKey(){
		return null;
	}
}
