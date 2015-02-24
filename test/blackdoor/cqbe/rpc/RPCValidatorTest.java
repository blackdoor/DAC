package blackdoor.cqbe.rpc;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.node.server.RPCHandler;
import blackdoor.cqbe.node.server.Server;
import blackdoor.cqbe.node.server.ServerException;
import blackdoor.cqbe.rpc.Rpc.Method;
import blackdoor.cqbe.test.ServerTester;
import blackdoor.net.SocketIOWrapper;

public class RPCValidatorTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testValid() throws RPCException, IOException, ServerException {
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
	
	
	}
	
