package blackdoor.cqbe.test;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.util.Arrays;

import org.junit.Test;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.rpc.*;

public class RpcTest {
	
	@Test
	public void testRPCBuilder() throws RPCException{
		RPCBuilder builder = new RPCBuilder();
		builder.setDestinationO(Address.getFullAddress());
		builder.setSourceIP(InetAddress.getLoopbackAddress());
		builder.setSourcePort(1234);
		builder.setIndex(1);
		//test lookup
		LookupRpc lookupRpc = builder.buildLookupObject();
		assertTrue(lookupRpc.getDestination().equals(Address.getFullAddress()));
		assertTrue(lookupRpc.getSource().equals(new L3Address(InetAddress.getLoopbackAddress(), 1234)));
		//test ping
		PingRpc pingRpc = builder.buildPingObject();
		assertTrue(pingRpc.getDestination().equals(Address.getFullAddress()));
		assertTrue(pingRpc.getSource().equals(new L3Address(InetAddress.getLoopbackAddress(), 1234)));
		//test put
		byte[] value = new byte[16];
		builder.setValue(value);
		PutRpc putRpc = builder.buildPutObject();
		assertTrue(Arrays.equals(putRpc.getValue(), value));
		//test get
		GetRpc getRpc = builder.buildGetObject();
		builder.setIndex(1);
		assertEquals(getRpc.getIndex(), 1);
	}

	@Test
	public void testFromJsonString() throws RPCException {
		RPCBuilder builder = new RPCBuilder();
		builder.setDestinationO(Address.getFullAddress());
		builder.setSourceIP(InetAddress.getLoopbackAddress());
		builder.setSourcePort(1234);
		builder.setIndex(1);
		//test lookup
		LookupRpc lookupRpc = (LookupRpc) Rpc.fromJsonString(builder.buildLookupObject().toJSONString());
		assertTrue(lookupRpc.getDestination().equals(Address.getFullAddress()));
		assertTrue(lookupRpc.getSource().equals(new L3Address(InetAddress.getLoopbackAddress(), 1234)));
		//test ping
		PingRpc pingRpc = (PingRpc) Rpc.fromJsonString(builder.buildPingObject().toJSONString());
		assertTrue(pingRpc.getDestination().equals(Address.getFullAddress()));
		assertTrue(pingRpc.getSource().equals(new L3Address(InetAddress.getLoopbackAddress(), 1234)));
		//test put
		byte[] value = new byte[16];
		builder.setValue(value);
		PutRpc putRpc = (PutRpc) Rpc.fromJsonString(builder.buildPutObject().toJSONString());
		assertTrue(Arrays.equals(putRpc.getValue(), value));
		//test get	
		GetRpc getRpc = (GetRpc) Rpc.fromJsonString(builder.buildGetObject().toJSONString());
		getRpc.setIndex(1);
		assertTrue(getRpc.getDestination().equals(Address.getFullAddress()));
		assertTrue(getRpc.getSource().equals(new L3Address(InetAddress.getLoopbackAddress(), 1234)));
		assertEquals(1, getRpc.getIndex());
		
		//test shutdown
		ShutdownRpc shutdownRpc = (ShutdownRpc) Rpc.fromJsonString(ShutdownRpc.getShutdownRPC().toJSONString());
	}

	@Test
	public void testToJSONString() {
		RPCBuilder builder = new RPCBuilder();
		builder.setDestinationO(Address.getFullAddress());
		builder.setSourceIP(InetAddress.getLoopbackAddress());
		builder.setSourcePort(1234);
		builder.setIndex(1);
		//Test Lookup
		LookupRpc lookupRpc = builder.buildLookupObject();
		System.out.println(lookupRpc.toJSON().toString(2));
		//test ping
		PingRpc pingRpc = builder.buildPingObject();
		System.out.println(pingRpc.toJSON().toString(2));

		//test put
		byte[] value = new byte[16];
		builder.setValue(value);
		PutRpc putRpc = builder.buildPutObject();
		System.out.println(putRpc.toJSON().toString(2));

		//test get
		GetRpc getRpc = builder.buildGetObject();
		System.out.println(getRpc.toJSON().toString(2));
		
		//test shutdown
		System.out.println(ShutdownRpc.getShutdownRPC().toJSON().toString(2));

	}

}
