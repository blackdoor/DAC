package blackdoor.cqbe.storage;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.Test;

import blackdoor.cqbe.node.Node.NodeBuilder;
import blackdoor.cqbe.node.NodeException;
import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.cli.dh256;
import blackdoor.cqbe.node.Node;
import blackdoor.cqbe.node.server.ServerException;
import blackdoor.cqbe.addressing.CASFileAddress;

public class StorageUnitTest {

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testPut() throws Exception {
		StorageController c = new StorageController(new File("/Users/cyrilvandyke/Documents/DAC/src/controller").toPath(),Address.getNullAddress());
		CASFileAddress file = new CASFileAddress(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/file.txt"));
		c.put(file);
		assertTrue("Size of table should be 1", c.size() == 1);
		
	}

}
