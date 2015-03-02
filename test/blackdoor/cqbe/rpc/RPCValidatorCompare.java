package blackdoor.cqbe.rpc;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.node.Node;
import blackdoor.cqbe.node.server.RPCHandler;
import blackdoor.cqbe.node.server.Server;
import blackdoor.cqbe.node.server.ServerException;
import blackdoor.cqbe.rpc.RPCException.JSONRPCError;
import blackdoor.cqbe.rpc.Rpc.Method;
import blackdoor.cqbe.test.ServerTester;
import blackdoor.net.SocketIOWrapper;

public class RPCValidatorCompare {

	
	JSONObject rpcValidLookup;
	JSONObject invalidRpcLookup;
	JSONObject rpcValidGet;
	JSONObject invalidRpcGet;
	JSONObject rpcValidPut;
	JSONObject invalidRpcPut;
	
	@Before
	public void setUp() throws Exception 
	{
		RPCBuilder builder = new RPCBuilder();
		builder.setDestinationO(Address.getNullAddress());
		builder.setSourceIP(InetAddress.getLocalHost());
		builder.setSourcePort(1234);
		builder.setIndex(1);
		builder.setValue(new byte [16]);
		rpcValidLookup = builder.buildLOOKUP();
		rpcValidGet = builder.buildGET();
		rpcValidPut = builder.buildPUT();
		invalidRpcLookup = builder.buildLOOKUP();
		invalidRpcGet = builder.buildGET();
		invalidRpcPut = builder.buildPUT();
	}
	

	@Test
	public void testValid1() throws RPCException, IOException, ServerException {
	Server server = new Server(1776);
	new Thread(server).start();
	SocketIOWrapper io = new SocketIOWrapper(new Socket(InetAddress.getLoopbackAddress(), 1776));
	
	//Testing a valid GET call
	Rpc rpc = new RPCUnitTest().getGoodMockRPC(Method.LOOKUP);
	RPCValidator validator = new RPCValidator(rpc.toJSONString(), io);
	String rpcValid = rpc.toJSON().toString(2);
	System.out.print(rpc.toJSON().toString(2));
	assertEquals("valid", validator.isValid(rpcValid));
	validator.handle();
	server.stop();
	
	
	//Testing an invalid LOOKUP call
	Rpc rpc2 = new RPCUnitTest().getBadMockRPC(Method.LOOKUP);
	RPCValidator validator2 = new RPCValidator(rpc.toJSONString(), io);
	String rpcValid2 = rpc.toJSON().toString(2);
	System.out.print(rpc.toJSON().toString(2));
	assertNotEquals("valid", validator2.isValid(rpcValid2));
	validator.handle();
	server.stop();
	}
	
	@Test
	public void testHandle() throws RPCException, IOException, ServerException{
	Server server = new Server(1776);
	new Thread(server).start();
	SocketIOWrapper io = new SocketIOWrapper(new Socket(InetAddress.getLoopbackAddress(), 1776));
	Rpc rpc3 = new RPCUnitTest().getGoodMockRPC(Method.LOOKUP);
	RPCValidator valid = new RPCValidator(rpc3.toString(), io);
	}
	
	@Test
	public void testValid(){
		assertEquals("should be valid LOOKUP", "valid", RPCValidator.isValid(rpcValidLookup.toString()));
		assertEquals("should be valid GET", "valid", RPCValidator.isValid(rpcValidGet.toString()));
		assertEquals("should be valid PUT", "valid", RPCValidator.isValid(rpcValidPut.toString()));
		
		System.out.println(rpcValidPut.toString(2));
		//TODO must add a checker to see if the index is 1, 2, or 3 THIS SHOULD NOT BE VALID
		assertEquals("should be invalid GET", "valid", RPCValidator.isValid(rpcValidGet.toString()));
	}
	
	@Test
	public void testMethodError() throws RPCException, IOException, ServerException 
	{
		invalidRpcLookup.put("method", "METH");
		System.out.print(invalidRpcLookup.toString(2));
		//Rpc invalid = Rpc.fromJson(invalidRpcLookup);
		//Both should have -32601, method not found
		//System.out.print(RPCValidator.buildError(RPCValidator.isValid(invalidRpcLookup.toString(2)), invalidRpcLookup.getInt("id")));
		//assertEquals(RPCValidator.buildError(RPCValidator.isValid(invalidRpcLookup.toString()), invalidRpcLookup.getInt("id")), Rpc.fromJson(invalidRpcLookup));
	}
	
	@Test
	public void testParamsError() throws RPCException{
		invalidRpcLookup.put("THisIsWrong", true);
		//This should throw exception but does not!
		Rpc invalid = Rpc.fromJson(invalidRpcLookup);
		//System.out.print(RPCValidator.buildError(RPCValidator.isValid(invalidRpcLookup.toString(2)), invalidRpcLookup.getInt("id")));
		//rename NODE SHAT ITSELF?
	}
		
	@Test
	public void testIndexError() throws RPCException, UnknownHostException
	{
		RPCBuilder builder = new RPCBuilder();
		builder.setDestinationO(Address.getNullAddress());
		builder.setSourceIP(InetAddress.getLocalHost());
		builder.setSourcePort(1234);
		builder.setIndex(1);
		//testing no index
		invalidRpcGet = builder.buildGET();
		System.out.println(invalidRpcGet.toString(2));	
		System.out.print(RPCValidator.buildError(RPCValidator.isValid(invalidRpcGet.toString(2)), invalidRpcGet.getInt("id")));
		
		//testing invalid index
		builder.setIndex(-1);
		invalidRpcGet = builder.buildGET();
		//Rpc invalidIndex = Rpc.fromJson(invalidRpcGet);//throws params exception
		//returns NODE SHAT ITSELF
		System.out.print(RPCValidator.buildError(RPCValidator.isValid(invalidRpcGet.toString(2)), invalidRpcGet.getInt("id")));
		//assertEquals(RPCValidator.buildError(RPCValidator.isValid(invalidRpcGet.toString()), invalidRpcGet.getInt("id")), Rpc.fromJson(invalidRpcGet));
	}
			
	}
	
	
	
	

	
