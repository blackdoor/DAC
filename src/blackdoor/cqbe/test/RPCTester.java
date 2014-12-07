package blackdoor.cqbe.test;

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
public class RPCTester {
    public static void main(String[] args) throws Exception{
        DBP.VERBOSE = true;
        testRPCHandler();
        testResponseBuilder();
    }

    public static void testRPCHandler() throws IOException, RPCException {
        RPCBuilder rpcBuilder = new RPCBuilder();
        rpcBuilder.setSourcePort(1234);
        rpcBuilder.setDestinationO(new Address());
        rpcBuilder.setSourceIP(InetAddress.getByName("192.168.1.1"));
        JSONObject rpc = rpcBuilder.buildPING();
        System.out.println(rpc);
        RPCHandler handler = new RPCHandler(System.out, rpc);
        handler.handle();
    }

    public static void testResponseBuilder(){
        JSONObject goodResponse = RPCBuilder.RPCResponseFactory(5, true, "good one", null);
        System.out.println(goodResponse.toString(2));
        RPCBuilder.JSONRPCError e = RPCBuilder.JSONRPCError.NODE_SHAT;
        JSONObject errorResponse = RPCBuilder.RPCResponseFactory(null, false, null, e, "poop");
        System.out.println(errorResponse.toString(2));
    }
}
