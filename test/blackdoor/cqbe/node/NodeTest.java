package blackdoor.cqbe.node;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.node.Node.NodeBuilder;
import blackdoor.cqbe.node.server.ServerException;
import blackdoor.util.DBP.SingletonAlreadyInitializedException;

public class NodeTest {
	
	@Before
	public void setUp() throws Exception {
		createNode();
	}

	private void createNode() throws NodeException, ServerException, SingletonAlreadyInitializedException {
		NodeBuilder node = new NodeBuilder();
		node.setAdam(true);
		node.buildNode();
	}

	@Test
	public void testGetAddressTable() {
		AddressTable a = Node.getAddressTable();
		assertNotNull(a);
	}

	@Test
	public void testGetAddress() {
		assertNotNull(Node.getAddress());
	}

	@Test
	public void testGetConfig() {
		assertNotNull(Node.getConfig());
	}

	@Test
	public void testGetStorageController() {
		assertNotNull(Node.getStorageController());
	}

	@Test
	public void testGetInstance() {
		assertNotNull(Node.getInstance());
	}

	@Test
	public void testGetN() {
		assertEquals("Test DEFAULT_ADDRESS_SIZE", Address.DEFAULT_ADDRESS_SIZE, Node.getN());
	}

	@Test
	public void testGetOverlayAddress() {
		assertNotNull(Node.getOverlayAddress());
	}

	@Ignore
	@Test
	public void testDestroyNode() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testStatusCheck() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testCheckStorage() {
		fail("Not yet implemented");
	}
	
	@After
	public void shutDown() {
		Node.shutdown();
	}

}
