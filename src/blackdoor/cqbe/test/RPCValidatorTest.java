package blackdoor.cqbe.test;

import static org.junit.Assert.*;

import java.net.InetAddress;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.cqbe.rpc.RPCValidator;

public class RPCValidatorTest {
	
	JSONObject validRpc;
	JSONObject invalidRpcTooManyFields;

	@Before
	public void setUp() throws Exception {
		RPCBuilder builder = new RPCBuilder();
		builder.setDestinationO(Address.getNullAddress());
		builder.setSourceIP(InetAddress.getByName("::FFFF:192.168.1.1"));
		builder.setSourcePort(1234);
		builder.setIndex(1);
		builder.setValue("base64String");
		validRpc = builder.buildLOOKUP();
		invalidRpcTooManyFields = builder.buildLOOKUP();
		invalidRpcTooManyFields.put("this shouldn't be here", true);
	}

	@Test
	public void testIsValid() {
		assertEquals("lookup request should be verified as valid", RPCValidator.isValid(validRpc.toString()), "valid");
		assertEquals("RPC should be identified as invalid", RPCValidator.isValid(invalidRpcTooManyFields.toString()), "invalid");
	}

}
