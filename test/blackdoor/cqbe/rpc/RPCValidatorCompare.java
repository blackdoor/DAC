package blackdoor.cqbe.rpc;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONObject;
import org.json.JSONString;
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
	JSONObject rpcValidPing;
	JSONObject invalidRpcPing;
	JSONObject rpcValidShutdown;
	
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
		rpcValidPing = builder.buildPING();
		invalidRpcLookup = builder.buildLOOKUP();
		//builder.setIndex(5);
		//invalidRpcGet = builder.buildGET();
		invalidRpcPut = builder.buildPUT();
		invalidRpcPing = builder.buildPING();
		rpcValidShutdown = builder.buildSHUTDOWN();
		
		
	}
	
	@Test
	public void testValid(){
		assertEquals("should be valid LOOKUP", "valid", RPCValidator.isValid(rpcValidLookup.toString()));
		assertEquals("should be valid GET", "valid", RPCValidator.isValid(rpcValidGet.toString()));
		assertEquals("should be valid PUT", "valid", RPCValidator.isValid(rpcValidPut.toString()));
		assertEquals("should be valid PING", "valid", RPCValidator.isValid(rpcValidPing.toString()));
		assertEquals("should be valid SHUTDOWN", "valid", RPCValidator.isValid(rpcValidShutdown.toString()));
	}
	
	@Test
	public void testMethodError() throws RPCException, IOException, ServerException 
	{
		invalidRpcLookup.put("method", "METH");
		System.out.print(invalidRpcLookup.toString(2));
		//Rpc invalid = Rpc.fromJson(invalidRpcLookup);
		//Both should have -32601, method not found
		JSONObject thing = RPCValidator.buildError(
				RPCValidator.isValid(invalidRpcLookup.toString()),
				invalidRpcLookup.getInt("id"));
		JSONRPCError err = JSONRPCError.fromJSON(thing.getJSONObject("error"));
		JSONRPCError err2 = null;
		try{
			Rpc.fromJson(invalidRpcLookup);
		}catch(RPCException e){
			err2 = e.getRPCError();
		}
		System.out.println(err.toString());
		assertEquals(err, err2);
	}
	
	
	@Test
	public void testParamsError() throws RPCException{
		//Shouldn't there be a fixed # of params?
		invalidRpcLookup.put("THisIsWrong", true);
		JSONObject paramError = RPCValidator.buildError(RPCValidator.isValid(invalidRpcLookup.toString()), invalidRpcLookup.getInt("id"));
		JSONRPCError err2 = null;
		JSONRPCError err = JSONRPCError.fromJSON(paramError.getJSONObject("error"));
		try{
			Rpc invalid = Rpc.fromJson(invalidRpcLookup);
			}catch(RPCException e){
			err2 = e.getRPCError();
		}
		assertEquals(err, err2);
	}
	
		
	@Test
	public void testIndexError() throws RPCException, UnknownHostException
	{
		RPCBuilder builder = new RPCBuilder();
		builder.setDestinationO(Address.getNullAddress());
		builder.setSourceIP(InetAddress.getLocalHost());
		builder.setSourcePort(1234);
		builder.setIndex(-1);
		//testing invalid index;
		invalidRpcLookup.remove("method");
		invalidRpcLookup.put("method", Method.GET);
		//now invalidRpcLookup is essentially a GET without an index
		JSONObject indexError = RPCValidator.buildError(RPCValidator.isValid(invalidRpcLookup.toString()),
		invalidRpcLookup.getInt("id"));
		JSONRPCError err = JSONRPCError.fromJSON(indexError.getJSONObject("error"));
		JSONRPCError err2 = null;
		try{
		   invalidRpcGet= builder.buildGET();
		   Rpc.fromJson(invalidRpcGet);
		}catch(RPCException e){
			err2 = e.getRPCError();
		}
		assertEquals(err, err2);
	}
	
	
//needs to be fixed
@Test
public void testBase64Error()
	{
	//invalidRpcPing.remove("method");
	//invalidRpcPing.put("method",  Method.PUT);
	JSONObject params = invalidRpcPut.getJSONObject("params");
	params.put("value", "hi");
	invalidRpcPut.put("params", params);
	System.out.println(invalidRpcPut.toString(2));
	JSONObject base64Error = RPCValidator.buildError(RPCValidator.isValid(invalidRpcPut.toString()),
			invalidRpcPut.getInt("id"));
	JSONRPCError err2 = null;
	JSONRPCError err = JSONRPCError.fromJSON(base64Error.getJSONObject("error"));
	try{
		Rpc.fromJson(invalidRpcPut);
	}catch(RPCException e){
		err2 = e.getRPCError();
	}
	assertEquals(err, err2);
	}

@Test
public void invalidAddress()
{
	JSONObject params = rpcValidGet.getJSONObject("params");
	params.put("destinationO", "notYoAddress");
	rpcValidGet.put("params", params);
	System.out.println(rpcValidGet.toString(2));
	JSONObject addressError = RPCValidator.buildError(RPCValidator.isValid(rpcValidGet.toString()),
			rpcValidGet.getInt("id"));
	JSONRPCError err2 = null;
	JSONRPCError err = JSONRPCError.fromJSON(addressError.getJSONObject("error"));
	try{
		Rpc.fromJson(rpcValidGet);
	}catch(RPCException e){
		err2 = e.getRPCError();
	}
	assertEquals(err, err2);
	}


@Test
public void invalidRequest()
{
	JSONObject rpcJson = rpcValidGet.getJSONObject("params");
	JSONRPCError err = null;
	try{
		Rpc.fromJson(rpcJson);
	}catch(RPCException e){
		err = e.getRPCError();
	}
	System.out.println(err);
}
}

	
	
	
	

	
