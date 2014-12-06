package blackdoor.cqbe.test;

import blackdoor.cqbe.rpc.RPCBuilder;
import org.json.JSONObject;

/**
 * Created by nfischer3 on 12/5/14.
 */
public class RPCTester {
    public static void main(String[] args){
        testResponseBuilder();
    }

    public static void testResponseBuilder(){
        JSONObject goodResponse = RPCBuilder.RPCResponseFactory(5, true, "good one", null);
        System.out.println(goodResponse.toString(2));
        RPCBuilder.JSONRPCError e = RPCBuilder.JSONRPCError.NODE_SHAT;
        JSONObject errorResponse = RPCBuilder.RPCResponseFactory(null, false, null, e, "poop");
        System.out.println(errorResponse.toString(2));
    }
}
