package blackdoor.cqbe.output_logic;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.output_logic.Router;
import blackdoor.cqbe.rpc.AckResponse;
import blackdoor.cqbe.rpc.JSONRPCResult;
import blackdoor.cqbe.rpc.NakResult;
import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.cqbe.rpc.RPCUnitTest;
import blackdoor.cqbe.rpc.RPCValidator;
import blackdoor.cqbe.rpc.Rpc;
import blackdoor.cqbe.rpc.Rpc.Method;
import blackdoor.net.SocketIOWrapper;

public class RouterTest {

	private String errorSample =
			"{\"id\": null, \"error\": { \"message\": \"Invalid params\", \"code\": -32602 }, \"jsonrpc\": \"2.0\"}";
	private static Thread serverThread;

	static int startTestServer(final String cannedResponse) throws InterruptedException {
		final int port = 49225;
		serverThread = new Thread() {
			@Override
			public void run() {
				try {
					ServerSocket ss = new ServerSocket(port);
					SocketIOWrapper io = new SocketIOWrapper(ss.accept());
					String input = io.read();
					System.out.println("input: " + input);
					Rpc built = Rpc.fromJsonString(input);
					
					// System.out.println("Sending response " + new
					// JSONObject(cannedResponse).toString(2));
					io.write(cannedResponse);
					io.close();
					ss.close();
				} catch (Exception e) {
					throw new RuntimeException("SHITSHITSHITSHITSHITSHITSHITSHITSHITSHITSHIT");
				}
			}
		};
		serverThread.start();
		Thread.sleep(100);
		return port;
	}


	private static void stopTestServer() {
		serverThread.stop();
	}

	@Test
	public void testPing() throws IOException, RPCException, InterruptedException {
		String pongSample =
				"{ \"id\": 5, \"result\": { \"result\": \"pong\", \"type\": \"P\" }, \"jsonrpc\": \"2.0\"  }";
		int port = startTestServer(pongSample);
		assertTrue(Router.ping(new L3Address(InetAddress.getLoopbackAddress(), port)));
		stopTestServer();
	}

	@Test
	public void testPrimitivePut() throws InterruptedException, IOException, RPCException {
		String ackSample =
				"{\"id\": 5, \"result\": {\"result\":true,\"type\":\"A\"}, \"jsonrpc\": \"2.0\" }";
		String nakSample =
				"{\"id\": 5, \"result\": {\"result\":false,\"type\":\"N\"}, \"jsonrpc\": \"2.0\" }";

		int port1 = startTestServer(ackSample);
		assertTrue(Router.primitivePut(new L3Address(InetAddress.getLoopbackAddress(), port1),
				Address.getFullAddress(), new byte[13]));
		stopTestServer();

		int port2 = startTestServer(nakSample);
		assertFalse(Router.primitivePut(new L3Address(InetAddress.getLoopbackAddress(), port2),
				Address.getFullAddress(), new byte[13]));
		stopTestServer();
		
	}


	@Test
	public void testPrimitiveLookup() throws InterruptedException, IOException, RPCException {
		String tableSample =
				"{\"id\": 5, \"result\": { \"result\": [ { \"port\": 1234,\"IP\": \"216.58.192.14\" }, { \"port\": 1236,  \"IP\": \"216.58.192.14\"  },  { \"port\": 1235, \"IP\": \"216.58.192.14\" } ], \"type\": \"T\" }, \"jsonrpc\": \"2.0\" }";
		int port = startTestServer(tableSample);
		System.out.println(Router.primitiveLookup(new L3Address(InetAddress.getLoopbackAddress(),
				port), Address.getFullAddress()));
		stopTestServer();
	}

	/*
	 * Tested in the testGetValue() method
	 * @Test
	 * public void testPrimitiveGet() throws RPCException, IOException, InterruptedException
	 * {
	 * byte[] byteValue = new byte[]{0xB,0,0,0xB,0x5};
	 * String base64Value = Base64.encode(byteValue);
	 * String value = "{\"id\": 5, \"result\": { \"result\": \""+base64Value+
	 * "\", \"type\": \"V\"}, \"jsonrpc\": \"2.0\" }";
	 * String table =
	 * "{\"id\": 5, \"result\": { \"result\": [ { \"port\": 1234,\"IP\": \"216.58.192.14\" }, { \"port\": 1236,  \"IP\": \"216.58.192.14\"  },  { \"port\": 1235, \"IP\": \"216.58.192.14\" } ], \"type\": \"T\" }, \"jsonrpc\": \"2.0\" }"
	 * ;
	 * //String getSample =
	 * "{\"id\": 1164132160,\"method\": \"GET\", \"params\": {\"index\": 1, \"destinationO\": \"FF:FF:FF:FF:FF:FF:FF:FF:FF:FF:FF:FF:FF:FF:FF:FF\",\"sourcePort\": 1234,\"sourceIP\": \"localhost\"},\"jsonrpc\": \"2.0\"}"
	 * ;
	 * int port3 = startTestServer(value);
	 * System.out.println(Router.primitiveGet(new L3Address(InetAddress.getLoopbackAddress(),
	 * port3), Address.getFullAddress(), 123).toJSONString());
	 * stopTestServer();
	 * }
	 */

	@Test
	public void testGetValue() throws RPCException, IOException, InterruptedException {
		byte[] byteValue = new byte[] {0xB, 0, 0, 0xB, 0x5};
		byte[] byteValueFalse = new byte[] {0xE, 0, 0, 0x6};
		String base64Value = Base64.encode(byteValue);
		String base64ValueFalse = Base64.encode(byteValueFalse);
		String value =
				"{\"id\": 5, \"result\": { \"result\": \"" + base64Value
						+ "\", \"type\": \"V\"}, \"jsonrpc\": \"2.0\" }";
		int p = startTestServer(value);
		assertTrue(Arrays.equals(
				Router.getValue(new L3Address(InetAddress.getLoopbackAddress(), p),
						Address.getFullAddress()), byteValue));
		stopTestServer();
		int m = startTestServer(value);
		assertFalse(Arrays.equals(
				Router.getValue(new L3Address(InetAddress.getLoopbackAddress(), m),
						Address.getFullAddress()), byteValueFalse));
		stopTestServer();
	}

	@Test
	public void testIndex() throws RPCException, IOException, InterruptedException {
		Address a = new L3Address(InetAddress.getLoopbackAddress(), 1);
		Address b = new L3Address(InetAddress.getLoopbackAddress(), 2);
		Address c = new L3Address(InetAddress.getLoopbackAddress(), 3);
		Set compare = new HashSet();
		compare.add(a);
		compare.add(b);
		compare.add(c);
		String indexSample =
				"{ \"id\": 5,  \"result\": {  \"result\": [   \""+a.overlayAddressToString()+"\", \""+b.overlayAddressToString()+"\",  \""+c.overlayAddressToString()+"\"  ],\"type\": \"I\"  },  \"jsonrpc\": \"2.0\" }";
		int p = startTestServer(indexSample);
		assertTrue(
				Router.getIndex(new L3Address(InetAddress.getLoopbackAddress(), p), 1).equals(compare));
		stopTestServer();
		int n = startTestServer(indexSample);
		assertNotEquals(
				Router.getIndex(new L3Address(InetAddress.getLoopbackAddress(), p), 1).toString(),
				("[48:CE:24:8D:65:A8:08:BD:CA:24:37:50:9E:00:D3:93, A8:B3:E0:27:21:B9:3C:7F:1F:10:42:71:F9:13:CA:B6, 5t:5B:77:15:AB:DD:58:F1:79:BE:E5:BE:61:05:60:F2]"));
		stopTestServer();
	}

}
