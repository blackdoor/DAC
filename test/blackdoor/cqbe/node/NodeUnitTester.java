package blackdoor.cqbe.node;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.Test;

import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.node.Node.NodeBuilder;
import blackdoor.cqbe.node.server.ServerException;

public class NodeUnitTester {

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testNodeBuilder() {
		NodeBuilder adamNode = new NodeBuilder();
		adamNode.setAdam(true);
		adamNode.setPort(1778);
		Node n = null;
		try{
			n = adamNode.buildNode();
		} catch (Exception e) {
			fail("Did not build node");
		}
		NodeBuilder secondNode = new NodeBuilder();
		secondNode.setAdam(false);
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		L3Address bootstrapNode = new L3Address(addr, 1778);
		secondNode.setPort(1778);
		secondNode.setBootstrapNode(bootstrapNode);
		Node s = null;
		try {
			s = secondNode.buildNode();
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			fail("Did not build node");
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			fail("Did not build server successfully");
		}
	}

}
