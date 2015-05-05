package blackdoor.cqbe.rpc;

import java.io.Serializable;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

import blackdoor.cqbe.rpc.RPCException.JSONRPCError;
import blackdoor.util.DBP;

/**
 * @author Nathaniel Fischer
 * @version v1.0.0 - May 4, 2015
 */
public abstract class RpcResponse implements Serializable {

	/**
	 * @param response
	 * @return
	 * @throws RPCException
	 */
	public static RpcResponse fromJson(JSONObject response) throws RPCException {
		try {
			// check version
			if (!response.getString("jsonrpc").equals("2.0")) {
				throw new RPCException(JSONRPCError.INVALID_RESPONSE);
			}
			if (response.has("result"))
				return ResultRpcResponse.fromJson(response);
			if (response.has("error")) {
				return ErrorRpcResponse.fromJson(response);
			}
		} catch (JSONException e) {
			throw new RPCException(JSONRPCError.PARSE_ERROR);
		}
		return null;
	}

	/**
	 * @param response
	 * @return
	 * @throws RPCException
	 */
	public static RpcResponse fromJson(String response) throws RPCException {
		try {
			return fromJson(new JSONObject(response));
		} catch (JSONException e) {
			throw new RPCException(JSONRPCError.PARSE_ERROR);
		}
	}

	private Integer id;
	private boolean successful;

	public RpcResponse(Integer id, boolean successful) {
		super();
		this.id = id;
		this.successful = successful;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	protected void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the successful
	 */
	public boolean isSuccessful() {
		return successful;
	}

	/**
	 * @param successful
	 *            the successful to set
	 */
	protected void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	/**
	 * @return
	 */
	protected JSONObject getRpcOuterShell() {
		JSONObject shell = new JSONObject();
		shell.put("jsonrpc", "2.0");
		shell.put("id", getId() == -1 ? JSONObject.NULL : getId());
		return shell;
	}

	/**
	 * @return
	 */
	public abstract JSONObject toJSON();

	/**
	 * @return
	 */
	public String toJSONString() {
		return toJSON().toString();
	}

}
