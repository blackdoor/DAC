package blackdoor.cqbe.rpc;

import java.io.Serializable;
import java.util.Random;

import org.json.JSONObject;

public abstract class RpcResponse implements Serializable {
	
	private int id;
	private boolean successful;

	public RpcResponse(int id, boolean successful) {
		super();
		this.id = id;
		this.successful = successful;
	}

	public RpcResponse(int id) {
		// TODO Auto-generated constructor stub
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
	protected void setId(int id) {
		this.id = id;
	}
	
	public void setId(Rpc request){
		this.id = request.getId();
	}

	/**
	 * @return the successful
	 */
	public boolean isSuccessful() {
		return successful;
	}

	/**
	 * @param successful the successful to set
	 */
	protected void setSuccessful(boolean successful) {
		this.successful = successful;
	}
	
	protected JSONObject getRpcOuterShell(){
		   JSONObject shell = new JSONObject();
		   shell.put("jsonrpc", "2.0");
		   shell.put("id", getId());
		   return shell;
	}
	
	public abstract JSONObject toJSON();
	   
	public String toJSONString(){
		return toJSON().toString();
	}

}
