package blackdoor.crypto;

public class Exceptions {
	public static class InvalidKeyException extends RuntimeException{
		public InvalidKeyException(String string) {
			super(string);
		}
	}
	public static class CipherNotInitializedException extends RuntimeException{

		public CipherNotInitializedException(String string) {
			super(string);
		}
		
		public CipherNotInitializedException(){
			super("Cipher not initialized.");
		}
		
	}
	
}
