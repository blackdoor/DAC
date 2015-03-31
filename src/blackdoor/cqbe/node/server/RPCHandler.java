package blackdoor.cqbe.node.server;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.NavigableSet;

import blackdoor.cqbe.rpc.AckResponse;
import blackdoor.cqbe.rpc.ErrorRpcResponse;
import blackdoor.cqbe.rpc.IndexResult;
import blackdoor.cqbe.rpc.PongResult;
import blackdoor.cqbe.rpc.PutRpc;
import blackdoor.cqbe.rpc.GetRpc;
import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.cqbe.rpc.TableResult;
import blackdoor.cqbe.rpc.ValueResult;
import blackdoor.cqbe.rpc.RPCException.JSONRPCError;
import blackdoor.cqbe.rpc.ResultRpcResponse;
import blackdoor.cqbe.rpc.Rpc;
import blackdoor.cqbe.rpc.RpcResponse;
import blackdoor.cqbe.rpc.ShutdownRpc;
import blackdoor.cqbe.storage.StorageController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.CASFileAddress;
import blackdoor.cqbe.addressing.FileAddress;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.node.Node;
import blackdoor.net.SocketIOWrapper;
import blackdoor.util.DBP;

/**
 * RPCHandler takes JSON RPC 2.0 request objects, does any requested operations
 * and forms a response.
 * 
 * @author nfischer3
 *
 *         Custom RPC response codes: -32001 - Malformed addresses
 */
public class RPCHandler {
	private String errorData = null;
	private SocketIOWrapper io = null;

	public RPCHandler() {
	}

	public RPCHandler(SocketIOWrapper io) {
		this.io = io;
	}

	/**
	 * Handles appropriate RPC call
	 * 
	 * @param request
	 * @return
	 * 
	 * @throws IOException
	 */
	public RpcResponse handle(String requeststring) throws IOException {
		Rpc request = null;
		RpcResponse response = null;
		try {
			request = Rpc.fromJsonString(requeststring);

			if (isValid(request)) {
				addRequestSenderToAT(request);
				switch (request.getMethod()) {// rpc.getString("method")){
				case GET:
					response = handleGetRequest(request);
					break;
				case PUT:
					response = handlePutRequest(request);
					break;
				case LOOKUP:
					response = handleLookupRequest(request);
					break;
				case PING:
					response = handlePingRequest(request);
					break;
				case SHUTDOWN:
					handleShutdown(request);
					return null;
				default:
					throw new RuntimeException(
							"WTF IS THISSSS??? I'm looking at a method type that I don't recognize! WHERE is the validator? Is it on vacation? Cause it's not validating!");
				}
			} else
				response = buildError(request);

		} catch (JSONException j) {
			// TODO not convinced this is needed anymore
			DBP.printException(j);
			DBP.printerrorln("Apparently the RPC validator is broken");
			DBP.printerrorln("A JSON-RPC response is not being sent, better fix the validator");
			return null;
		} catch (RPCException e) {
			if (request == null && errorData != null)
				response = new ErrorRpcResponse(e.getRPCError(), errorData);
			else if (request != null && errorData == null)
				response = new ErrorRpcResponse(request, e.getRPCError());
			else
				response = new ErrorRpcResponse(e.getRPCError());
		} catch (AddressException | UnknownHostException e) {
			response = new ErrorRpcResponse(request,
					RPCException.JSONRPCError.INVALID_ADDRESS_FORMAT);

			DBP.printException(e);
		} catch (IOException e) {
			// TODO What do? shutdown had issues.....
		}

		return response;
	}

	/**
	 * <p>
	 * Checks for semantics, syntax and if the RPC is supported by this system.
	 *
	 * @param String
	 * @return String detailing whether the JSONObject is valid or not.
	 */
	public boolean isValid(Rpc request) {
		// TODO there are probably more things that make an RPC valid that arent
		// being checked here...
		if (request != null)
			return false;
		if (!hasValidAddress(request))
			return false;
		if (!hasValidSourceport(request))
			return false;
		return true;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	public boolean hasValidAddress(Rpc request) {
		// TODO what makes a valid address????
		return true;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	public boolean hasValidSourceport(Rpc request) {
		// TODO hardcoded port values???
		int port = request.getSource().getPort();
		if (port < 0 || port > 61001) {
			return false;
		}
		return true;
	}

	public RpcResponse buildError(Rpc request) {
		// ErrorRpcResponse response =
		if (!hasValidAddress(request) || !hasValidSourceport(request))
			return new ErrorRpcResponse(request,
					RPCException.JSONRPCError.INVALID_ADDRESS_FORMAT);
		return null;
	}

	/**
	 * adds sender ip and port from rpc to this nodes's address table if
	 * applicable. DOES NOT CHECK validity
	 * 
	 * @param request
	 * 
	 * @throws JSONException
	 * @throws UnknownHostException
	 */
	private void addRequestSenderToAT(Rpc request) throws UnknownHostException {
		if (!L3Address.isNonNodeAddress(request.getSource())) {
			L3Address result = Node.getAddressTable().add(request.getSource());
			if (!request.getSource().equals(result))
				DBP.printdemoln("Adding " + request.getSource()
						+ " to address table from handler");
		}
	}

	/**
	 * Handles a ping request
	 * 
	 * @param request
	 */
	private RpcResponse handlePingRequest(Rpc request) {
		PongResult pong = new PongResult();
		ResultRpcResponse result = new ResultRpcResponse(request.getId(), pong);
		return result;
	}

	/**
	 * Handles a put request
	 * 
	 * @param request
	 * 
	 * @throws RPCException
	 */
	private RpcResponse handlePutRequest(Rpc request) throws RPCException {

		StorageController storageController = Node.getStorageController();
		PutRpc rpc = (PutRpc) request;
		// TODO look at settings and find out how large of a value we are
		// willing to store
		// throw exception if value is oversized
		try {
			CASFileAddress value = new CASFileAddress(
					storageController.getDomain(), rpc.getValue());
			storageController.put(value);
		} catch (IOException e) {
			throw new RPCException(JSONRPCError.NODE_SHAT);
		}
		AckResponse result = new AckResponse();
		return new ResultRpcResponse(rpc.getId(), result);
	}

	/**
	 * Handles a get request
	 * 
	 * @param request
	 * 
	 * @throws AddressException
	 * @throws UnknownHostException
	 * @throws RPCException
	 *             if the index field is true (non-zero), returns list of keys
	 *             from indicated bucket if the index field is false (zero) and
	 *             destination matches that of a stored key, returns associated
	 *             value If index is false and destination does not match stored
	 *             key, return a lookup call
	 */
	private RpcResponse handleGetRequest(Rpc request) throws RPCException,
			UnknownHostException, AddressException {
		StorageController storage = Node.getStorageController();
		// JSONObject responseObject = new JSONObject();
		RpcResponse responseObject = null;
		GetRpc rpc = (GetRpc) request;
		try {
			int index = rpc.getIndex();
			if (index != 0) {
				// JSONArray result = new JSONArray();

				NavigableSet<Address> keys = storage.getBucket(index);
				IndexResult ir = new IndexResult(keys);
				// for (Address key : keys) {
				// result.put(key.overlayAddressToString());
				// }
				responseObject = new ResultRpcResponse(request.getId(), ir);
				// responseObject = RPCBuilder.RPCResponseFactory(rpc.getId(),
				// true, result, null);
			}
			if (index == 0) {
				if (storage.containsValue(rpc.getDestination())) {
					FileAddress value = storage.get(rpc.getDestination());
					File file = value.getFile();
					try {
						byte[] byteArray = Files.readAllBytes(file.toPath());
						// String result = Base64.encode(byteArray);
						ValueResult vr = new ValueResult(byteArray);
						responseObject = new ResultRpcResponse(request.getId(),
								vr);
						// responseObject = RPCBuilder.RPCResponseFactory(
						// rpc.getId(), true, result, null);
					} catch (IOException e) {
						throw new RPCException(JSONRPCError.NODE_STORAGE_ERROR);
					}
				}
				if (!storage.containsValue(rpc.getDestination()))
					return handleLookupRequest(request);

			}
		} catch (JSONException e) {
			errorData = e.getMessage();
		}
		return responseObject;
	}

	/**
	 *
	 * @return A JSON response object to be sent back over the socket/stream
	 * @throws AddressException
	 * @throws UnknownHostException
	 * @throws RPCException
	 */
	private RpcResponse handleLookupRequest(Rpc request)
			throws AddressException, UnknownHostException, RPCException {
		RpcResponse responseObject = null;
		try {
			// JSONObject params = rpc.getJSONObject("params");
			// // TODO change all to Dest0 or something
			// Address dest = new Address(params.getString("destinationO"));
			// // TODO change all "source"to"src"
			// Address src = new L3Address(InetAddress.getByName(params
			// .getString("sourceIP")), params.getInt("sourcePort"));
			AddressTable nodeTable = Node.getAddressTable();
			AddressTable nearest = nodeTable.getNearestAddresses(
					Node.getN() + 1, request.getDestination());
			nearest.remove(request.getSource().getOverlayAddress());

			// JSONObject entry;

			// for (L3Address a : nearest.values()) {
			// entry = new JSONObject();
			// entry.put("overlay", a.overlayAddressToString());
			// entry.put("IP", a.getLayer3Address().getHostAddress());
			// entry.put("port", a.getPort());
			// result.put(entry);
			// }

			TableResult tr = new TableResult(nearest);
			responseObject = new ResultRpcResponse(5, tr);
		} catch (JSONException e) {
			errorData = e.getMessage();
			throw new RPCException(RPCException.JSONRPCError.INVALID_PARAMS);
		}
		return responseObject;
	}

	private void handleShutdown(Rpc request) throws RPCException, IOException {
		// Assuming this
		if (this.io == null)
			throw new IOException();

		Socket sock = io.getSocket();
		if (!sock.getInetAddress().isLoopbackAddress()
				|| !sock.getLocalAddress().isLoopbackAddress()) {
			throw new RPCException(JSONRPCError.NON_LO_SHUTDOWN);
		}
		io.write(ShutdownRpc.CHALLENGE);
		String handshake = io.read();
		if (!handshake.equals(ShutdownRpc.HANDSHAKE)) {
			DBP.printerrorln("Shutdown request was recieved but sender was unable to handshake. "
					+ "It is possible that this was an attempted loopback spoofing attack.");
			return;
		}
		Node.shutdown();
	}

}
