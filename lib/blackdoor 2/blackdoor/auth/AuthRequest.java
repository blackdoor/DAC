/**
 * 
 */
package blackdoor.auth;

import java.io.Serializable;
import java.util.Arrays;

import blackdoor.auth.User.UserRight;

/**
 * @author kAG0
 * a network request that an operation be preformed on the server which recieves the request
 * 
 */
@Deprecated //See the Portunes project for more features and an SQL user database
public class AuthRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * each operation requires certain data members be supplied
	 * ADD requires: userName, authUserName, passwordHash, authPasswordHash, rights
	 * REMOVE requires: userName, authUserName, authPasswordHash
	 * CHECK requires: userName, passwordHash
	 * CHANGENAME requires: userName, newUserName, authUserName, authPasswordHash
	 * CHANGEPASSWORD requires: userName, passwordHash, newPasswordHash
	 *
	 */
	public enum Operation{
		ADD, REMOVE, CHECK, CHANGENAME, CHANGEPASSWORD
	}
	/**
	 * Challenge Salted Hash Indicator
	 * to help facilitate CHAP, the CSHI indicates whether passwordHash or authPasswordHash has been salted with the challenge
	 */
	public enum CSHI{
		NORMAL, AUTH
	}
	private String userName, authUserName, newUserName;
	private byte[] passwordHash, authPasswordHash, newPasswordHash;
	private UserRight[] rights;
	private int id;
	private Operation operation;
	private CSHI indicator;
	
	AuthRequest(Operation operation){
		this.operation = operation;
		id = (int)(Math.random()*10) * (int)(Math.random()*10);
	}
	
	public CSHI getIndicator(){
		return indicator;
	}
	
	public void setIndicator(CSHI indicator){
		this.indicator = indicator;
	}
	
	public int getID(){
		return id;
	}
	
	public Operation getOperation(){
		return operation;
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
	/**
	 * @return the authUserName
	 */
	public String getAuthUserName() {
		return authUserName;
	}
	/**
	 * @param authUserName the authUserName to set
	 */
	public void setAuthUserName(String authUserName) {
		this.authUserName = authUserName;
	}
	/**
	 * @return the newUserName
	 */
	public String getNewUserName() {
		return newUserName;
	}
	/**
	 * @param newUserName the newUserName to set
	 */
	public void setNewUserName(String newUserName) {
		this.newUserName = newUserName;
	}
	/**
	 * @return the passwordHash
	 */
	public byte[] getPasswordHash() {
		return passwordHash;
	}
	/**
	 * @param passwordHash the passwordHash to set
	 */
	public void setPasswordHash(byte[] passwordHash) {
		this.passwordHash = passwordHash;
	}
	/**
	 * @return the authPasswordHash
	 */
	public byte[] getAuthPasswordHash() {
		return authPasswordHash;
	}
	/**
	 * @param authPasswordHash the authPasswordHash to set
	 */
	public void setAuthPasswordHash(byte[] authPasswordHash) {
		this.authPasswordHash = authPasswordHash;
	}
	/**
	 * @return the newPasswordHash
	 */
	public byte[] getNewPasswordHash() {
		return newPasswordHash;
	}
	/**
	 * @param newPasswordHash the newPasswordHash to set
	 */
	public void setNewPasswordHash(byte[] newPasswordHash) {
		this.newPasswordHash = newPasswordHash;
	}
	/**
	 * @return the rights
	 */
	public UserRight[] getRights() {
		return rights;
	}
	/**
	 * @param rights the rights to set
	 */
	public void setRights(UserRight[] rights) {
		this.rights = rights;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(authPasswordHash);
		result = prime * result
				+ ((authUserName == null) ? 0 : authUserName.hashCode());
		result = prime * result + id;
		result = prime * result + Arrays.hashCode(newPasswordHash);
		result = prime * result
				+ ((newUserName == null) ? 0 : newUserName.hashCode());
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		result = prime * result + Arrays.hashCode(passwordHash);
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthRequest other = (AuthRequest) obj;
		if (!Arrays.equals(authPasswordHash, other.authPasswordHash))
			return false;
		if (authUserName == null) {
			if (other.authUserName != null)
				return false;
		} else if (!authUserName.equals(other.authUserName))
			return false;
		if (id != other.id)
			return false;
		if (!Arrays.equals(newPasswordHash, other.newPasswordHash))
			return false;
		if (newUserName == null) {
			if (other.newUserName != null)
				return false;
		} else if (!newUserName.equals(other.newUserName))
			return false;
		if (operation != other.operation)
			return false;
		if (!Arrays.equals(passwordHash, other.passwordHash))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AuthRequest [userName=" + userName + ", authUserName="
				+ authUserName + ", newUserName=" + newUserName
				+ ", passwordHash=" + Arrays.toString(passwordHash)
				+ ", authPasswordHash=" + Arrays.toString(authPasswordHash)
				+ ", newPasswordHash=" + Arrays.toString(newPasswordHash)
				+ ", id=" + id + ", operation=" + operation + "]";
	}
}
