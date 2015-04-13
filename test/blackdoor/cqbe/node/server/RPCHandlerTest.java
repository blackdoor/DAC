package blackdoor.cqbe.node.server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.invoke.MethodHandles.Lookup;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.output_logic.Router;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.net.SocketIOWrapper;

public class RPCHandlerTest {
	
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
					// System.out.println("input: " + input);
					// System.out.println("Sending response " + new
					// JSONObject(cannedResponse).toString(2));
					io.write(cannedResponse);
					io.close();
					ss.close();
				} catch (Exception e) {
					e.printStackTrace();
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
	
	L3Address node;
	
	@Before
	public void setup() throws Exception{
		node = new L3Address(InetAddress.getLocalHost(), 1778);
	}

	@Test
	public void testLookup() throws Exception{
		System.out.println(Router.primitiveLookup(node, L3Address.getFullAddress()));
	}
	
	@Test
	public void testPing() throws RPCException {
		System.out.println(Router.ping(node));
	}
	
	@Test
	public void testPut() throws IOException, RPCException{
		System.out.println(Router.primitivePut(node, L3Address.getFullAddress(), new byte[]{0xB,0,0,0xB,5}));
	}
	
	@Test
	public void testGetValue(){
		
	}
	
	@Test
	public void testGetIndex(){
		
	}
	
	@Test
	public void testGetTable(){
		
	}

}
