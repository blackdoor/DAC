package blackdoor.cqbe.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.addressing.CASFileAddress;
import blackdoor.cqbe.storage.StorageController;
import blackdoor.util.DBP;

public class StorageTester {

	public StorageTester() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception{
		DBP.VERBOSE = true;
		testController();

	}
	
	public static void testController() throws Exception{
		StorageController c = new StorageController(new File("C:\\Temp\\controller").toPath(), Address.getNullAddress());
		CASFileAddress lolfile = new CASFileAddress(new File("C:\\Temp\\controller\\lol_file.txt"));
		c.put(lolfile);
		for(int i = 0; i<45; i++){
			c.put(new CASFileAddress(new File("C:\\Temp\\controller\\"+new Integer(i).toString()+".boop")));//, new byte[]{(byte) i}));
		}
		System.out.println(c.put(new CASFileAddress(new File("C:\\Temp\\controller\\lol2.txt"))));
		System.out.println(c);
		c.remap(new CASFileAddress(new File("C:\\Temp\\controller\\1.boop")), new CASFileAddress(new File("C:\\Temp\\controller\\10.boop")));
		//System.out.println(c.getBucket(3));
		c.garbageCollectReferences();
		System.out.println(c);
	}

}
