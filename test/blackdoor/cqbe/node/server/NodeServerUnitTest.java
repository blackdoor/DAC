package blackdoor.cqbe.node.server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONObject;
import org.junit.Test;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.node.server.NodeServer;
import blackdoor.cqbe.rpc.PingRpc;
import blackdoor.cqbe.rpc.PongResult;
import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.cqbe.rpc.ResultRpcResponse;
import blackdoor.cqbe.rpc.Rpc;
import blackdoor.cqbe.rpc.RpcResponse;
import blackdoor.net.SocketIOWrapper;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Jan 27, 2015
 */
public class NodeServerUnitTest {

	@Test
	public void testRun() throws InterruptedException, ServerException {
		NodeServer server = new NodeServer();
		new Thread(server).start();
		Thread.sleep(100);
		if (!server.isRunning()) {
			fail("Server was not started properly.");
		}

		server.stop();
		if (server.isRunning()) {
			fail("Server was not stopped properly.");
		}

	}

	@Test
	public void testBasicInteraction() throws InterruptedException,
			ServerException {
		NodeServer server = new NodeServer();
		new Thread(server).start();
		Thread.sleep(10);
		int port = 57006;
		TestInteraction ti = null;
		String response = null;
		try {
			ti = new TestInteraction(port, server.getPort());
			ti.write(ti.getActualRPC().toJSONString());
			response = ti.read();

			if (!ti.isPongResult(response))
				fail("Not a pong result in test interaction....");
		} catch (IOException e) {
			e.printStackTrace();
			fail("RPC Error: " + e.getMessage());
		} catch (RPCException e) {
			fail("Problems parsing the response from Interaction");
			e.printStackTrace();
		} finally {
			Thread.sleep(100);
			server.stop();
		}
	}

	@Test
	public void testThreadPoolMax() throws InterruptedException,
			ServerException {
		NodeServer server = new NodeServer();
		new Thread(server).start();
		Thread.sleep(10);
		int MaxPoolNum = 150;
		ArrayList<TestInteraction> tilist = new ArrayList<>();
		try {
			for (int i = 0; i < MaxPoolNum; i++) {
				tilist.add(new TestInteraction(i + 57006, server.getPort()));
			}

			for (TestInteraction elem : tilist) {
				new Thread(elem).start();
			}

		} catch (IOException e) {
			e.printStackTrace();
			fail("Test Error: " + e.getMessage());
		} finally {
			Thread.sleep(100);
			server.stop();
		}
	}

	/**
	 * 
	 * @author Cj Buresch
	 * @version v0.0.1 - Jan 29, 2015
	 */
	private class TestInteraction implements Runnable {

		private int port;
		private String desthost;
		private int destport;
		private SocketIOWrapper io;

		public TestInteraction(int port, int serverport) throws IOException {
			this.port = port;
			this.destport = serverport;
			io = new SocketIOWrapper(new Socket(
					InetAddress.getLoopbackAddress(), serverport));
		}

		public void writeRPC(Rpc content) throws IOException {
			io.write(content.toJSONString());
		}

		public void write(String content) throws IOException {
			io.write(content);
		}

		public String read() throws IOException {
			return io.read();
		}

		public void closeOut() throws IOException {
			io.close();
		}

		@Override
		public void run() {
			String response = null;
			try {
				write(getActualRPC().toJSONString());
				response = read();
				if (!isPongResult(response))
					fail("Not a pong result in test interaction....");
			} catch (IOException e) {
				fail("Problems with Interaction");
				e.printStackTrace();
			} catch (RPCException e) {
				// System.out.println(response);
				e.printStackTrace();
				fail("Problems parsing the response from Interaction");
			} finally {
				try {
					closeOut();
				} catch (IOException e) {
					fail("Problems closing Interaction");
					e.printStackTrace();
				}
			}
		}

		public boolean isPongResult(String response) throws RPCException {
			RpcResponse rpcres = RpcResponse.fromJson(response);
			PongResult pong = new PongResult();
			ResultRpcResponse result = new ResultRpcResponse(rpcres.getId(),
					pong);
			return result.isSuccessful();
		}

		public Rpc getActualRPC() {
			RPCBuilder builder = new RPCBuilder();
			builder.setDestinationO(Address.getFullAddress());
			builder.setSource(new L3Address(InetAddress.getLoopbackAddress(),
					port));
			builder.setIndex(2);
			PingRpc pingrpc = builder.buildPingObject();
			return pingrpc;
		}

		public JSONObject getActualJSONRPC() {
			RPCBuilder builder = new RPCBuilder();
			JSONObject obj = null;
			try {

				builder.setDestinationO(new L3Address(InetAddress
						.getByName(desthost), destport));
				builder.setSource(new L3Address(InetAddress
						.getLoopbackAddress(), port));
				obj = builder.buildLOOKUP();
			} catch (Exception e) {
				return null;
			}
			return obj;
		}

		public JSONObject getFakeRPC() throws IOException {
			JSONObject rpc = new JSONObject();
			rpc.put("jsonrpc", "2.0");
			rpc.put("method", "TESTER");
			rpc.put("id", 99999999);
			return rpc;
		}

	}

}
