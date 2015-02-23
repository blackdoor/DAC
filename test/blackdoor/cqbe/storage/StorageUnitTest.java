package blackdoor.cqbe.storage;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
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
		CASFileAddress file2 = new CASFileAddress(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/file2.txt"));
		CASFileAddress file3 = new CASFileAddress(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/file3.txt"));
		c.put(file2);
		assertTrue("Size of 2",c.size() == 2);
		c.put(file3);
		assertTrue("Size of 3",c.size() == 3);
	}

	@Test
	public void testRemove() throws Exception {
		StorageController c = new StorageController(new File("/Users/cyrilvandyke/Documents/DAC/src/controller").toPath(),Address.getNullAddress());
		CASFileAddress file = new CASFileAddress(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/file.txt"));
		c.put(file);
		assertTrue("Controller will have knowledge of 1 file",c.size() == 1);
		c.remove(file);
		assertTrue("Controller will have knowledge of 0 files",c.size() == 0);
	}
	
	@Test
	/**
	 * NOTE: THIS TEST MAY ONLY BE RUN ONCE SUCCESSFULLY, the test file must be manually replaced
	 * @throws Exception
	 */
	public void testDelete() throws Exception {
		StorageController c = new StorageController(new File("/Users/cyrilvandyke/Documents/DAC/src/controller").toPath(),Address.getNullAddress());
		CASFileAddress file4 = new CASFileAddress(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/file4.txt"));
		c.put(file4);
		assertTrue("Controller will have knowledge of 1 file",c.size() == 1);
		c.delete(file4);
		assertTrue("Controller will have knowledge of 0 files",c.size() == 0);
		//Self-check if the folder is empty
	}
	
	@Test
	public void testDeleteThirdBucket() throws Exception {
		StorageController c = new StorageController(new File("/Users/cyrilvandyke/Documents/DAC/src/controller").toPath(),Address.getNullAddress());
		CASFileAddress filea = new CASFileAddress(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/filea.txt"));
		CASFileAddress fileb = new CASFileAddress(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/fileb.txt"));
		CASFileAddress filec = new CASFileAddress(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/filec.txt"));
		CASFileAddress filed = new CASFileAddress(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/filed.txt"));
		c.put(filea);
		c.put(fileb);
		c.put(filec);
		c.put(filed);
		assertTrue("Size of table should be 4", c.size() == 4);
		c.deleteThirdBucket();
		assertTrue("Size of table should still be 4, no deletions should occur", c.size() == 4);
	}
	
	@Test
	public void testGarbageCollectReferences() throws Exception {
		StorageController c = new StorageController(new File("/Users/cyrilvandyke/Documents/DAC/src/controller").toPath(),Address.getNullAddress());
		CASFileAddress filex = new CASFileAddress(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/filex.txt"));
		CASFileAddress filey = new CASFileAddress(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/filey.txt"));
		CASFileAddress filez = new CASFileAddress(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/filez.txt"));
		CASFileAddress fileq = new CASFileAddress(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/fileq.txt"));
		c.put(filex);
		c.put(filey);
		c.put(filez);
		c.put(fileq);
		assertTrue("Size of table should be 4",c.size()==4);
		Files.delete(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/filex.txt").toPath());
		Files.delete(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/filey.txt").toPath());
		Files.delete(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/filez.txt").toPath());
		Files.delete(new File("/Users/cyrilvandyke/Documents/DAC/src/controller/fileq.txt").toPath());
		assertTrue("Size of table should be 4",c.size()==4);
		c.garbageCollectReferences();
		assertTrue("Size of table should be 0",c.size()==0);
	}
	
	@Test
	public void testDomainRestriction(){
		StorageController c = new StorageController(new File("/Users/cyrilvandyke/Documents/DAC/src/controller").toPath(),Address.getNullAddress());
		CASFileAddress file;
		try {
			file = new CASFileAddress(new File("/Users/cyrilvandyke/Documents/DAC/src/testFiles/file.txt"));
			c.put(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			assertTrue("Caught domain error successfully",true);
		}

		assertTrue("Size should remain 0, file is not in domain",c.size()==0);
	}
	
	
}
