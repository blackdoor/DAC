/**
 * 
 */
package blackdoor.auth;

import java.io.Serializable;
import java.util.Arrays;

//import org.apache.commons.codec.binary.Hex;


import blackdoor.crypto.Hash;

/**
 * @author kAG0
 *
 */
@Deprecated //See the Portunes project for more features and an SQL user database
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5359665275772545627L;
	public enum UserRight{
		ADD, REMOVE, CHANGE
	}
	private boolean[] userRights = {false, false, false};
	private String userName;
	private byte[] passwordHash;
	//private EnumSet<UserRight> userRights;
	/**
	 * create a new user with given name and hashed password
	 * @param userName
	 * @param passwordHash
	 */
	public User(String userName, byte[] passwordHash) {
		this.userName = userName;
		this.passwordHash = passwordHash;
	}
	/**
	 * create a new user with given name and null password
	 * @param userName
	 */
	public User(String userName){
		this.userName = userName;
		passwordHash = null;
	}
	/**
	 * check if this user has given right
	 * @param right
	 * @return true if user has right, else false
	 */
	public boolean hasRight(UserRight right){
		return userRights[right.ordinal()];//userRights.contains(right);
	}
	
	/**
	 * add right to this user
	 * @param right
	 */
	public void addUserRight(UserRight right){
		userRights[right.ordinal()] = true;
	}
	
	/**
	 * remove right from this user
	 * @param right
	 */
	public void removeUserRight(UserRight right){
		userRights[right.ordinal()] = false;
	}
	
	/**
	 * sets the password for the first time.
	 * setPassword(byte[] currentPassword, byte[] newPassword) should be used to change the password
	 * @param password
	 * @throws Exception 
	 */
	public void setPassword(byte[] password) throws Exception{
		if (passwordHash == null){
			passwordHash = password;
		}
		else throw new Exception("password is already set");
	}
	
	/**
	 * get the saved password hash salted with given salt for use with CHAP
	 * @param salt
	 * @return the saved password hashed and salted with salt
	 */
	public byte[] getSaltyHash(byte[] salt){
		byte [] saltedHash = new byte[salt.length + passwordHash.length];
		System.arraycopy(salt, 0, saltedHash, 0, salt.length);
		System.arraycopy(passwordHash, 0, saltedHash, salt.length, passwordHash.length);
		return Hash.getSHA1(saltedHash);
	}
	
	/**
	 * changes the stored password
	 * @deprecated
	 * @param currentPassword (hashed)
	 * @param newPassword (hashed)
	 * @return true if currentPassword is correct and password has been replaced, else false
	 */
	@Deprecated public boolean setPassword(byte[] currentPassword, byte[] newPassword){
		if(checkPassword(currentPassword)){
			this.passwordHash = newPassword;
			return true;
		}
		else return false;
	}
	
	/**
	 * changes the stored password
	 * @param currentPassword (hashed)
	 * @param newPassword (hashed)
	 * @return true if currentPassword is correct and password has been replaced, else false
	 */
	public boolean setPassword(byte[] currentPassword, byte[] salt, byte[] newPassword){
		if(checkPassword(currentPassword, salt)){
			this.passwordHash = newPassword;
			return true;
		}
		else return false;
	}
	
	/**
	 * changes the stored password
	 * @deprecated
	 * @param currentPassword
	 * @param newPassword
	 * @return true if currentPassword is correct and password has been replaced, else false
	 */
	@Deprecated public boolean setPassword(String currentPassword, String newPassword){
		if(checkPassword(Hash.getSHA1(currentPassword.getBytes()))){
			this.passwordHash = Hash.getSHA1(newPassword.getBytes());
			return true;
		}
		else return false;
	}
	/**
	 * 
	 * @param passwordHash
	 * @return returns true if passwordHash is the same as the stored password hash
	 */
	public boolean checkPassword(byte[] passwordHash){
		return Arrays.equals(passwordHash, this.passwordHash);
	}
	
	/**
	 * checks password against a salted password hash
	 * @param passwordHash
	 * @return returns true if passwordHash is the same as the stored password hash
	 */
	public boolean checkPassword(byte[] passwordHash, byte[] salt){
		return Arrays.equals(passwordHash, getSaltyHash(salt));
	}
	
	/**
	 * @deprecated
	 * @param passwordHash
	 * @return returns true if passwordHash is the same as the stored password hash
	 */
	@Deprecated public boolean checkPassword(String passwordHash){
		String password = "";// = Hex.encodeHexString(this.passwordHash);
		return password.equalsIgnoreCase(passwordHash);
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [userRights=" + Arrays.toString(userRights)
				+ ", userName=" + userName + ", passwordHash="
				+ Arrays.toString(passwordHash) + "]";
	}


}
