package blackdoor.cqbe.rpc;

import static org.junit.Assert.*;

import java.net.InetAddress;

import org.junit.Test;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.rpc.Rpc.Method;

public class RPCUnitTest {
	private int SRC_PORT = 1234;

	@Test
	public void testPING() {
		PingRpc pingrpc;
		try {
			pingrpc = (PingRpc) getGoodMockRPC(Method.PING);
			assertTrue(pingrpc.getDestination()
					.equals(Address.getFullAddress()));
			assertTrue(pingrpc.getSource().equals(
					new L3Address(InetAddress.getLoopbackAddress(), SRC_PORT)));
		} catch (RPCException e) {
			e.printStackTrace();
			fail("Ping was not built properly");
		}
		// TODO do more tests on it here.
	}

	@Test
	public void testLOOKUP() {
		LookupRpc lookuprpc;
		try {
			lookuprpc = (LookupRpc) getGoodMockRPC(Method.LOOKUP);
			assertTrue(lookuprpc.getDestination().equals(
					Address.getFullAddress()));
			assertTrue(lookuprpc.getSource().equals(
					new L3Address(InetAddress.getLoopbackAddress(), SRC_PORT)));
		} catch (RPCException e) {
			fail("Lookup was not built properly");
		}
		// TODO do more tests on it here.
	}

	@Test
	public void testGET() {
		GetRpc getrpc;
		try {
			getrpc = (GetRpc) getGoodMockRPC(Method.GET);
			assertTrue(getrpc.getDestination().equals(
					Address.getFullAddress()));
			assertTrue(getrpc.getSource().equals(
					new L3Address(InetAddress.getLoopbackAddress(), SRC_PORT)));
			assertEquals(getrpc.getIndex(), 2);
		} catch (RPCException e) {
			fail("Get was not built properly");
		}
	}

	@Test
	public void testPUT() {
		fail("Not yet implemented");
	}

	@Test
	public void testSHUTDOWN() {
		fail("Not yet implemented");
	}

	@Test
	public void testFromJsonString() throws RPCException {
		RPCBuilder builder = new RPCBuilder();
		builder.setDestinationO(Address.getFullAddress());
		builder.setSource(new L3Address(InetAddress.getLoopbackAddress(),
				SRC_PORT));
		builder.setIndex(1);
		// test lookup
		LookupRpc lookupRpc = (LookupRpc) Rpc.fromJsonString(builder
				.buildLookupObject().toJSONString());
		assertTrue(lookupRpc.getDestination().equals(Address.getFullAddress()));
		assertTrue(lookupRpc.getSource().equals(
				new L3Address(InetAddress.getLoopbackAddress(), SRC_PORT)));
		// test ping
		PingRpc pingRpc = (PingRpc) Rpc.fromJsonString(builder
				.buildPingObject().toJSONString());
		assertTrue(pingRpc.getDestination().equals(Address.getFullAddress()));
		assertTrue(pingRpc.getSource().equals(
				new L3Address(InetAddress.getLoopbackAddress(), SRC_PORT)));
		//test get	
		GetRpc getRpc = (GetRpc) Rpc.fromJsonString(builder.buildGetObject().toJSONString());
		assertTrue(getRpc.getDestination().equals(Address.getFullAddress()));
		assertTrue(getRpc.getSource().equals(new L3Address(InetAddress.getLoopbackAddress(), 1234)));
		assertEquals(1, getRpc.getIndex());
	}

	@Test
	public void testRPCBuilder() {
		RPCBuilder builder = new RPCBuilder();
		builder.setDestinationO(Address.getFullAddress());
		builder.setSource(new L3Address(InetAddress.getLoopbackAddress(),
				SRC_PORT));
		builder.setIndex(1);
		// test lookup
		LookupRpc lookupRpc = builder.buildLookupObject();
		assertTrue(lookupRpc.getDestination().equals(Address.getFullAddress()));
		assertTrue(lookupRpc.getSource().equals(
				new L3Address(InetAddress.getLoopbackAddress(), SRC_PORT)));
		// test ping
		PingRpc pingRpc = builder.buildPingObject();
		assertTrue(pingRpc.getDestination().equals(Address.getFullAddress()));
		assertTrue(pingRpc.getSource().equals(
				new L3Address(InetAddress.getLoopbackAddress(), SRC_PORT)));
		//test get
		GetRpc getRpc = builder.buildGetObject();
		assertEquals(getRpc.getIndex(), 1);
	}

	@Test
	public void testToJSONString() {
		RPCBuilder builder = new RPCBuilder();
		builder.setDestinationO(Address.getFullAddress());
		builder.setSource(new L3Address(InetAddress.getLoopbackAddress(),
				SRC_PORT));
		builder.setIndex(2);
		// Test Lookup
		LookupRpc lookupRpc = builder.buildLookupObject();

		// test ping
		PingRpc pingRpc = builder.buildPingObject();
		//test get
		GetRpc getRpc = builder.buildGetObject();
		System.out.println(getRpc.toJSON().toString(2));
	}

	public Rpc getGoodMockRPC(Rpc.Method method) throws RPCException {
		RPCBuilder builder = new RPCBuilder();
		builder.setDestinationO(Address.getFullAddress());
		builder.setSource(new L3Address(InetAddress.getLoopbackAddress(),
				SRC_PORT));
		builder.setIndex(2);
		Rpc rpc = null;
		switch (method) {

		case GET:
			rpc = builder.buildGetObject();
			break;
		case PUT:
			rpc = builder.buildPutObject();
			break;
		case LOOKUP:
			rpc = builder.buildLookupObject();
			break;
		case PING:
			rpc = builder.buildPingObject();
			break;
		case SHUTDOWN:
			// TODO not done
			break;
		}
		return rpc;
	}

	public Rpc getBadMockRPC(Rpc.Method method) throws RPCException {
		RPCBuilder builder = new RPCBuilder();
		builder.setDestinationO(Address.getFullAddress());
		builder.setSource(new L3Address(InetAddress.getLoopbackAddress(),
				SRC_PORT));
		Rpc rpc = null;
		switch (method) {
		case GET:
			rpc = builder.buildGetObject();
			break;
		case PUT:
			rpc = builder.buildPutObject();
			break;
		case LOOKUP:
			rpc = builder.buildLookupObject();
			break;
		case PING:
			rpc = builder.buildPingObject();
			break;
		case SHUTDOWN:
			// TODO not done
			break;
		}
		return rpc;

	}
}
