package blackdoor.cqbe.rpc;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.node.server.RPCHandler;
import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.util.DBP;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by nfischer3 on 12/5/14.
 */

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.json.JSONObject;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.cqbe.rpc.RPCValidator;
import blackdoor.util.DBP;

public class RPCTester {

	public static void main(String[] args) throws Exception{
		DBP.VERBOSE = true;
		testValidator();
		testPassiveAT();
	}

	public static void testValidator() throws Exception {
		String rpc;
		RPCBuilder builder = new RPCBuilder();
		builder.setDestinationO(Address.getNullAddress());
		builder.setSourceIP(InetAddress.getByName("::FFFF:192.168.1.1"));
		builder.setSourcePort(1234);
		builder.setIndex(1);
		builder.setValue(new byte[16]);
		//rpc = builder.buildLOOKUP().toString();
		rpc = builder.buildSHUTDOWN().toString();
		//rpc = "{\"method\":\"shutdown\",\"id\":2052568617,\"params\":{\"sourcePort\":1234,\"extensions\":{},\"destinationO\":\"00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00\",\"sourceIP\":\"192.168.1.1\",\"sourceO\":\"1B:1D:95:95:3A:8C:6E:92:C8:0F:E9:D5:EA:A1:EA:12:A3:A1:C6:56:BA:CC:A9:2F:B0:35:F7:C9:8A:33:E5:8A\"}}{\"id\":2052568617,\"jsonrpc\":\"2.0\",\"error\":{\"code\":-32602,\"message\":\"Invalid params\"}}";
		rpc = rpc.substring(1);
		DBP.printdevln("Testing RPC:");
		DBP.printdevln(rpc);
		RPCValidator validator = new RPCValidator(rpc, System.out);
		validator.handle();
	}

	public static void testBuilder() throws Exception{
		InetAddress a = InetAddress.getByName("::FFFF:192.168.1.1");
		byte[] aBytes = new byte[16];
		System.arraycopy(a.getAddress(), 0, aBytes, 12, a.getAddress().length);
		aBytes[10] = (byte) 0xFF;
		aBytes[11] = (byte) 0xFF;
		InetAddress b = InetAddress.getByAddress(aBytes);
		System.out.println(b);

		RPCBuilder builder = new RPCBuilder();
		builder.setSourceIP(b);
		builder.setSourcePort(1234);
		builder.setDestinationO(new L3Address(a, 1234));
		builder.setIndex(1);
		System.out.println(builder.buildGET());
		System.out.println(builder.buildLOOKUP());
	}
	
	public static void testPassiveAT() throws RPCException, IOException{
		RPCBuilder builder = new RPCBuilder();
		builder.setSourceIP(InetAddress.getLoopbackAddress());
		builder.setSourcePort(1235);
		builder.setDestinationO(new L3Address(InetAddress.getLoopbackAddress(), 1234));
		builder.setIndex(1);
		JSONObject request = builder.buildGET();
		RPCHandler handler= new RPCHandler(System.out, request);
		handler.handle();
	}

    public static void testResponseBuilder(){
        JSONObject goodResponse = RPCBuilder.RPCResponseFactory(5, true, "good one", null);
        System.out.println(goodResponse.toString(2));
        RPCException.JSONRPCError e = RPCException.JSONRPCError.NODE_SHAT;
        JSONObject errorResponse = RPCBuilder.RPCResponseFactory(null, false, null, e, "poop");
        System.out.println(errorResponse.toString(2));
    }
}
