package blackdoor.cqbe.filestor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.addressing.CASFileAddress;
import blackdoor.cqbe.addressing.FileAddress;

public class FileTableTest {
	
	File fileTable;
	List<FileAddress> files;

	@Before
	public void setUp() throws Exception {
		JSONObject jso = new JSONObject();
		files = new ArrayList<FileAddress>();
		for(int i = 0; i < 5; i++){
			File f = new File(String.valueOf(i));
			Files.write(f.toPath(), new byte[]{(byte) i}, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
			CASFileAddress fa = new CASFileAddress(f);
			files.add(fa);
			jso.put("/files/" + i, fa.overlayAddressToString());
		}
		fileTable = new File("file.table");
		Files.write(fileTable.toPath(), jso.toString().getBytes(FileTable.charset), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
	}
	
	

	@Test
	public void testSetEntry() throws IOException, AddressException {
		for(int i = 5; i < 10; i++){
			File f = new File(String.valueOf(i));
			Files.write(f.toPath(), new byte[]{(byte) i}, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
			CASFileAddress fa = new CASFileAddress(f);
			files.add(fa);
			FileTable.setEntry(fileTable, "/files/"+i, fa);
		}
		for(int i = 5; i < 10; i++){
			assertEquals(files.get(i).overlayAddressToString(), FileTable.getEntry(fileTable, "/files/" + i));
		}
	}

	@Test
	public void testGetEntry() throws IOException, AddressException {
		for(int i = 0; i < 5; i++){
			assertEquals(files.get(i), FileTable.getEntry(fileTable, "/files/" + i));
		}
	}
	
	@After
	public void tearDown(){
		fileTable.delete();
		for(FileAddress f : files){
			f.getFile().delete();
		}
	}

}
