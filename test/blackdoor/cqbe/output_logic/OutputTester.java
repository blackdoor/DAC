package blackdoor.cqbe.output_logic;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.output_logic.Router;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.cqbe.rpc.RPCValidator;
import blackdoor.net.SocketIOWrapper;
import blackdoor.util.DBP;

public class OutputTester {

	public static void main(String[] args) throws Exception{
		DBP.VERBOSE = true;
		testPrimitiveLookup();
		System.out.println();
		testPrimitivePut();
	}
	
	public static void testRouter() throws RPCException, IOException{
		System.out.println(Router.ping(new L3Address(InetAddress.getLoopbackAddress(), 1234)));
	}
	
	private static int startTestServer(final String cannedResponse) throws InterruptedException{
		final int port = 123;
		new Thread(){
			@Override
			public void run() {
				try{
				ServerSocket ss = new ServerSocket(port);
				SocketIOWrapper io = new SocketIOWrapper(ss.accept());
				String input =  io.read();
				System.out.println("input: " + input);
				System.out.println("input " + (RPCValidator.isValid(input).equals("valid") ? "is valid" : "is not valid"));
				
				System.out.println("Sending response " + new JSONObject(cannedResponse).toString(2));
				io.write(cannedResponse);
				io.close();
				ss.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}.start();
		Thread.sleep(100);
		return port;
	}
	
	public static void testPrimitiveLookup() throws IOException, RPCException, AddressException, InterruptedException{
		String response = "{\"jsonrpc\": \"2.0\",  \"result\": [    {\"overlay\": \"overlay address\", \"IP\" : \"192.168.1.1\", \"port\":1234 },    {\"overlay\": \"overlay address\", \"IP\" : \"192.168.1.1\", \"port\":1235 },    {\"overlay\": \"overlay address\", \"IP\" : \"192.168.1.1\", \"port\":1236 },    {\"overlay\": \"overlay address\", \"IP\" : \"192.168.1.1\", \"port\":1237 }  ],  \"id\": 1}";
		int port = startTestServer(response);
		System.out.println("response: " + Router.primitiveLookup(new L3Address(InetAddress.getLoopbackAddress(), port), Address.getNullAddress()));
	}

	public static void testPrimitivePut() throws Exception{
		JSONObject j = new JSONObject();
		j.put("jsonrpc", "2.0");
		j.put("result", true);
		j.put("id", 1234);
		//System.out.println(j.toString(2));
		//String response = "{\"jsonrpc\": \"2.0\",\"result\": \"true\",\"id\": 3}";
		int port = startTestServer(j.toString());
		System.out.println("result: " + Router.primitivePut(new L3Address(InetAddress.getLoopbackAddress(), port), L3Address.getNonNodeAddress(), new byte[13]));
	}

	public static void testPrimitiveGetWithLookupResponse(){
		
		//int port = startTestServer
	}
	public static void testPrimitiveGetWithValueResponse(){
		
	}
	public static void testPrimitiveGetWithIndexResponse(){
		
	}
}
