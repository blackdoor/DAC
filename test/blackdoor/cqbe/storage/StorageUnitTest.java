package blackdoor.cqbe.storage;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.unit.TestAssistant;
import blackdoor.cqbe.addressing.CASFileAddress;

public class StorageUnitTest {

	private File f;
	private File f2;
	private File f3;
	private File f4;
	private File fa;
	private File fb;
	private File fc;
	private File fd;
	private File fx;
	private File fy;
	private File fz;
	private File fq;
	private File fHidden;
	
	private CASFileAddress file;
	private CASFileAddress file2;
	private CASFileAddress file3;
	private CASFileAddress file4;
	private CASFileAddress filea;
	private CASFileAddress fileb;
	private CASFileAddress filec;
	private CASFileAddress filed;
	private CASFileAddress filex;
	private CASFileAddress filey;
	private CASFileAddress filez;
	private CASFileAddress fileq;
	private CASFileAddress fileHidden;
	private TestAssistant ta = new TestAssistant();
	private StorageController c;
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Rule
	public TemporaryFolder diffFolder = new TemporaryFolder();
	
	@Before
	public void setUp() throws Exception {
		createTempFiles();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testPut() throws Exception {
		c.put(file);
		assertTrue("Size of table should be 1", c.size() == 1);
		c.put(file2);
		assertTrue("Size of 2",c.size() == 2);
		c.put(file3);
		assertTrue("Size of 3",c.size() == 3);
	}

	@Test
	public void testRemove() throws Exception {
		c.put(file);
		assertTrue("Controller will have knowledge of 1 file",c.size() == 1);
		c.remove(file);
		assertTrue("Controller will have knowledge of 0 files",c.size() == 0);
	}
	
	@Test
	public void testDelete() throws Exception {
		c.put(file4);
		assertTrue("Controller will have knowledge of 1 file",c.size() == 1);
		c.delete(file4);
		assertTrue("Controller will have knowledge of 0 files",c.size() == 0);
		assertTrue("File is deleted",!file4.getFile().exists());
	}
	
	@Test
	public void testDeleteThirdBucket() throws Exception {
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
		c.put(filex);
		c.put(filey);
		c.put(filez);
		c.put(fileq);
		assertTrue("Size of table should be 4",c.size()==4);
		Files.delete(filex.getFile().toPath());
		Files.delete(filey.getFile().toPath());
		Files.delete(filez.getFile().toPath());
		Files.delete(fileq.getFile().toPath());	
		assertTrue("Size of table should be 4",c.size()==4);
		c.garbageCollectReferences();
		assertTrue("Size of table should be 0",c.size()==0);
	}
	
	
	@Test
	public void testDomainRestriction(){
		try {
			c.put(fileHidden);
		} catch (IndexOutOfBoundsException e) {
			assertTrue("Caught domain error successfully",true);
		}
		assertTrue("Size should remain 0, file is not in domain",c.size()==0);
	}

	
	private void createTempFiles() throws Exception {
		
		c = new StorageController(folder.getRoot().toPath(),Address.getNullAddress());
		
		f = folder.newFile("file.txt");
		f2 = folder.newFile("file2.txt");
		f3 = folder.newFile("file3.txt");
		f4 = folder.newFile("file4.txt");
		fa = folder.newFile("filea.txt");
		fb = folder.newFile("fileb.txt");
		fc = folder.newFile("filec.txt");
		fd = folder.newFile("filed.txt");
		fx = folder.newFile("filex.txt");
		fy = folder.newFile("filey.txt");
		fz = folder.newFile("filez.txt");
		fq = folder.newFile("fileq.txt");
		fHidden = diffFolder.newFile("hidden.txt");
		
		Files.write(f.toPath(),"1".getBytes());
		Files.write(f2.toPath(),"2".getBytes());
		Files.write(f3.toPath(),"3".getBytes());
		Files.write(f4.toPath(),"4".getBytes());
		Files.write(fa.toPath(),"a".getBytes());
		Files.write(fb.toPath(),"b".getBytes());
		Files.write(fc.toPath(),"c".getBytes());
		Files.write(fd.toPath(),"d".getBytes());
		Files.write(fx.toPath(),"x".getBytes());
		Files.write(fy.toPath(),"y".getBytes());
		Files.write(fz.toPath(),"z".getBytes());
		Files.write(fq.toPath(),"q".getBytes());
		
		file = new CASFileAddress(f);
		file2 = new CASFileAddress(f2);
		file3 = new CASFileAddress(f3);
		file4 = new CASFileAddress(f4);
		filea = new CASFileAddress(fa);
		fileb = new CASFileAddress(fb);
		filec = new CASFileAddress(fc);
		filed = new CASFileAddress(fd);
		filex = new CASFileAddress(fx);
		filey = new CASFileAddress(fy);
		filez = new CASFileAddress(fz);
		fileq = new CASFileAddress(fq);
		
		fileHidden = new CASFileAddress(fHidden);
	}
}
