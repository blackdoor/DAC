package blackdoor.cqbe.node.server;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.NavigableSet;
import java.util.Random;

import blackdoor.cqbe.rpc.AckResponse;
import blackdoor.cqbe.rpc.PutRpc;
import blackdoor.cqbe.rpc.GetRpc;
import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.cqbe.rpc.RPCException.JSONRPCError;
import blackdoor.cqbe.rpc.ResultRpcResponse;
import blackdoor.cqbe.rpc.Rpc;
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

	private JSONObject rpc;
	private SocketIOWrapper io;
	private String errorData = null;

	public RPCHandler(SocketIOWrapper outy, JSONObject rpc) {
		this.rpc = rpc;
		this.io = outy;
	}

	/**
	 * Handles appropriate RPC call
	 * 
	 * @throws IOException
	 */
	public void handle() throws IOException {

		JSONObject responseObject;
		try {
			addRequestSenderToAT();
			Rpc requestObject = Rpc.fromJson(rpc);
			switch (requestObject.getMethod()) {// rpc.getString("method")){
			case GET:
				responseObject = handleGetRequest();
				break;
			case PUT:
				responseObject = handlePutRequest();
				break;
			case LOOKUP:
				responseObject = handleLookupRequest();
				break;
			case PING:
				responseObject = handlePingRequest();
				break;
			case SHUTDOWN:
				handleShutdown();
				return;
			default:
				io.close();
				throw new RuntimeException(
						"WTF IS THISSSS??? I'm looking at a method type that I don't recognize! WHERE is the validator? Is it on vacation? Cause it's not validating!");
			}
		} catch (JSONException j) {
			DBP.printException(j);
			DBP.printerrorln("Apparently the RPC validator is broken");
			DBP.printerrorln("A JSON-RPC response is not being sent, better fix the validator");
			return;
		} catch (AddressException a) {
			DBP.printException(a);
			responseObject = RPCBuilder.RPCResponseFactory(rpc.getInt("id"),
					false, null,
					RPCException.JSONRPCError.INVALID_ADDRESS_FORMAT,
					a.getMessage());
		} catch (RPCException e) {
			if (errorData != null)
				responseObject = RPCBuilder.RPCResponseFactory(
						rpc.getInt("id"), false, null, e.getRPCError(),
						errorData);
			else
				responseObject = RPCBuilder.RPCResponseFactory(
						rpc.getInt("id"), false, null, e.getRPCError());
		} catch (UnknownHostException e) {
			responseObject = RPCBuilder.RPCResponseFactory(rpc.getInt("id"),
					false, null,
					RPCException.JSONRPCError.INVALID_ADDRESS_FORMAT);
			DBP.printException(e);
		}


		try {
			// DBP.printdevln("in handle");
			// DBP.printdevln("about to write response " + responseObject);

			io.write(responseObject.toString());
		} finally {
			io.close();
		}
	}

	/**
	 * adds sender ip and port from rpc to this nodes's address table if
	 * applicable. DOES NOT CHECK validity
	 * 
	 * @throws JSONException
	 * @throws UnknownHostException
	 */
	private void addRequestSenderToAT() throws UnknownHostException{
		JSONObject params = rpc.getJSONObject("params"); 
		L3Address sender = new L3Address(InetAddress.getByName(params.getString("sourceIP")), params.getInt("sourcePort"));
		if(!L3Address.isNonNodeAddress(sender))
		{
			L3Address result = Node.getAddressTable().add(sender);
			if(!sender.equals(result))
				DBP.printdemoln("Adding " + sender + " to address table from handler");
		}
	}

	/**
	 * Sends response to server, to be sent to the RPC builder
	 */
	private void sendRPC() {

	}

	/**
	 * Accepts an RPC request and calls further functions to handle it
	 */
	private void acceptRPC() {

	}

	/**
	 * Checks the validity of an RPC statement using the RPC Validator
	 * 
	 * @return True if valid RPC statement, False otherwise
	 */
	private Boolean checkValidity() {
		return null;
	}

	/**
	 * Returns an error-message to the server
	 */
	private void sendError() {
	}

	/**
	 * Determines the nature of the request after validity
	 */
	private void parseRequest() {
	}

	/**
	 * Handles a ping request
	 */
	private JSONObject handlePingRequest() {
		return RPCBuilder.RPCResponseFactory(rpc.getInt("id"), true, "pong",
				null);
	}

	/**
	 * Handles a put request
	 * 
	 * @throws RPCException
	 */
	private JSONObject handlePutRequest() throws RPCException {
		StorageController storageController = Node.getStorageController();
		PutRpc rpc = (PutRpc) Rpc.fromJson(this.rpc);
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
		return new ResultRpcResponse(rpc.getId(), result).toJSON();
	}

	/**
	 * Handles a get request
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
	private JSONObject handleGetRequest() throws RPCException,
			UnknownHostException, AddressException {
		StorageController storage = Node.getStorageController();
		JSONObject responseObject = new JSONObject();
		GetRpc rpc = (GetRpc) Rpc.fromJsonString(this.rpc.toString());
		try {
			int index = rpc.getIndex();
			if (index != 0) {
				JSONArray result = new JSONArray();
				NavigableSet<Address> keys = storage.getBucket(index);
				for (Address key : keys) {
					result.put(key.overlayAddressToString());
				}
				responseObject = RPCBuilder.RPCResponseFactory(rpc.getId(),
						true, result, null);
			}
			if (index == 0) {
				if (storage.containsValue(rpc.getDestination())) {
					FileAddress value = storage.get(rpc.getDestination());
					File file = value.getFile();
					try {
						byte[] byteArray = Files.readAllBytes(file.toPath());
						String result = Base64.encode(byteArray);
						responseObject = RPCBuilder.RPCResponseFactory(
								rpc.getId(), true, result, null);
					} catch (IOException e) {
						throw new RPCException(JSONRPCError.NODE_STORAGE_ERROR);
					}
				}
				if (!storage.containsValue(rpc.getDestination())) {
					return handleLookupRequest();
				}
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
	private JSONObject handleLookupRequest() throws AddressException,
			UnknownHostException, RPCException {
		JSONObject responseObject;
		try {
			JSONObject params = rpc.getJSONObject("params");
			Address dest = new Address(params.getString("destinationO"));// TODO
																			// change
																			// all
																			// "destinationO"
																			// to
																			// "destO"
			Address src = new L3Address(InetAddress.getByName(params
					.getString("sourceIP")), params.getInt("sourcePort")); // TODO
																			// change
																			// all
																			// "source"
																			// to
																			// "src"
			AddressTable nodeTable = Node.getAddressTable();
			AddressTable nearest = nodeTable.getNearestAddresses(
					Node.getN() + 1, dest);
			nearest.remove(src.getOverlayAddress());
			JSONArray result = new JSONArray();
			JSONObject entry;
			for (L3Address a : nearest.values()) {
				entry = new JSONObject();
				entry.put("overlay", a.overlayAddressToString());
				entry.put("IP", a.getLayer3Address().getHostAddress());
				entry.put("port", a.getPort());
				result.put(entry);
			}
			responseObject = RPCBuilder.RPCResponseFactory(rpc.getInt("id"),
					true, result, null);
		} catch (JSONException e) {
			errorData = e.getMessage();
			throw new RPCException(RPCException.JSONRPCError.INVALID_PARAMS);
		}
		return responseObject;
	}

	private void handleShutdown() throws RPCException, IOException {
		Socket sock = io.getSocket();
		if (!sock.getInetAddress().isLoopbackAddress()
				|| !sock.getLocalAddress().isLoopbackAddress()) {
			throw new RPCException(JSONRPCError.NON_LO_SHUTDOWN);
		}
		int challenge = new Random().nextInt();
		io.write(challenge);
		String handshake = io.read();
		if (!handshake.equals(String.valueOf(challenge))) {
			DBP.printerrorln("Shutdown request was recieved but sender was unable to handshake. "
					+ "It is possible that this was an attempted loopback spoofing attack.");
			return;
		}
		System.out.println("Shutting down node.");
		long mark = System.nanoTime();
		Node.shutdown();
		while(System.nanoTime() - mark < Server.TIMEOUT * 1000000000);
		System.exit(0);
	}

}
