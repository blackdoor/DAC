package blackdoor.auth;

import java.io.Serializable;

import blackdoor.auth.AuthRequest.Operation;
@Deprecated //See the Portunes project for more features and an SQL user database
public class AuthReply implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean operationCompleted;
	private int id;
	private Operation operation;
	public String message;
	AuthReply(boolean operationCompleted, int id, Operation operation){
		this.operationCompleted = operationCompleted;
		this.id = id;
		this.operation = operation;
	}
	/**
	 * @return the operationCompleted
	 */
	public boolean isOperationCompleted() {
		return operationCompleted;
	}
	/**
	 * @param operationCompleted the operationCompleted to set
	 */
	public void setOperationCompleted(boolean operationCompleted) {
		this.operationCompleted = operationCompleted;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the operation
	 */
	public Operation getOperation() {
		return operation;
	}
	/**
	 * @param operation the operation to set
	 */
	public void setOperation(Operation operation) {
		this.operation = operation;
	}

}
