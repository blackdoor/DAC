package blackdoor.cqbe.rpc;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.rpc.*;
import blackdoor.cqbe.rpc.RPCException.JSONRPCError;

public class RpcResponseTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented"); // TODO
	}
	
	@Test
	public void testValue() throws Exception{
		byte[] value = new byte[] {1,2,3,4,5};
		ValueResult vr = new ValueResult(value);
		ResultRpcResponse result = new ResultRpcResponse(5, vr);
		System.out.println(result.toJSON().toString(2));
		RpcResponse parsed = RpcResponse.fromJson(result.toJSON());
		assertTrue(parsed instanceof ResultRpcResponse);
		ResultRpcResponse parsedResult = (ResultRpcResponse) parsed;
		assertTrue(parsedResult.getResult() instanceof ValueResult);
		assertTrue(Arrays.equals(((ValueResult)parsedResult.getResult()).getValue(), value));
		assertEquals(result.getId(), parsed.getId());
	}
	
	@Test
	public void testIndex() throws Exception{
		HashSet<Address> index = new HashSet<Address>();
		index.add(new L3Address(InetAddress.getByName("google.com"), 1234));
		index.add(new L3Address(InetAddress.getByName("google.com"), 1235));
		index.add(new L3Address(InetAddress.getByName("google.com"), 1236));
		IndexResult ir = new IndexResult(index);
		ResultRpcResponse result = new ResultRpcResponse(5, ir);
		//System.out.println(result.toJSON().toString(2));
		RpcResponse parsed = RpcResponse.fromJson(result.toJSON());
		assertTrue(parsed instanceof ResultRpcResponse);
		ResultRpcResponse parsedResult = (ResultRpcResponse) parsed;
		assertTrue(parsedResult.getResult() instanceof IndexResult);
		assertEquals(parsedResult.getResult().getValue(), index);
		assertEquals(result.getId(), parsed.getId());
	}
	
	@Test
	public void testTable() throws UnknownHostException, RPCException{
		AddressTable at = new AddressTable();
		at.add(new L3Address(InetAddress.getByName("google.com"), 1234));
		at.add(new L3Address(InetAddress.getByName("google.com"), 1235));
		at.add(new L3Address(InetAddress.getByName("google.com"), 1236));
		TableResult tr = new TableResult(at);
		ResultRpcResponse result = new ResultRpcResponse(5, tr);
		//System.out.println(result.toJSON().toString(2));
		RpcResponse parsed = RpcResponse.fromJson(result.toJSON());
		assertTrue(parsed instanceof ResultRpcResponse);
		ResultRpcResponse parsedResult = (ResultRpcResponse) parsed;
		assertTrue(parsedResult.getResult() instanceof TableResult);
		assertEquals(parsedResult.getResult().getValue(), at);
		assertEquals(result.getId(), parsed.getId());
	}
	
	@Test
	public void testPong() throws RPCException{
		PongResult pong = new PongResult();
		ResultRpcResponse result = new ResultRpcResponse(5, pong);
		//System.out.println(result.toJSON().toString(2));
		RpcResponse parsed = RpcResponse.fromJson(result.toJSON());
		assertTrue(parsed instanceof ResultRpcResponse);
		ResultRpcResponse parsedResult = (ResultRpcResponse) parsed;
		assertTrue(parsedResult.getResult() instanceof PongResult);
		assertEquals(result.getId(), parsed.getId());
	}
	
	@Test
	public void testError() throws RPCException{
		ErrorRpcResponse error = new ErrorRpcResponse(JSONRPCError.INVALID_PARAMS);
		RpcResponse parsed = RpcResponse.fromJson(error.toJSON());
		assertTrue(parsed instanceof ErrorRpcResponse);
		assertEquals(error.getError(), ((ErrorRpcResponse) parsed).getError());
	}
}
