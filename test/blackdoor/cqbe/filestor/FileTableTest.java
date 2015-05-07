package blackdoor.cqbe.filestor;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
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
		String current = null;

		BufferedReader reader = new BufferedReader(new FileReader(fileTable));
		for(int i = 5; i < 10; i++){
			assertEquals(files.get(i).overlayAddressToString(), FileTable.getEntry(fileTable, "/files/" + i).toString());
		}
		reader.close();
	}

	@Test
	public void testGetEntry() throws IOException, AddressException {
		for(int i = 0; i < 5; i++){
			assertEquals(files.get(i), FileTable.getEntry(fileTable, "/files/" + i));
		}
	}
	
	@Test
	public void testRemoveEntry() throws IOException, AddressException {
		File f = new File(String.valueOf(11));
		Files.write(f.toPath(), new byte[]{(byte) 11}, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		int index = files.size();
		CASFileAddress fa = new CASFileAddress(f);
		files.add(fa);
		FileTable.setEntry(fileTable, "/files/11", fa);
		assertEquals(files.get(index),FileTable.getEntry(fileTable, "/files/11"));
		FileTable.removeEntry(fileTable, "/files/11");
		assertEquals(null,FileTable.getEntry(fileTable, "/files/11"));
	}
    
	@Test
	public void testListEntries() throws JSONException, IOException{
		FileTable.listEntries(fileTable,"/files/");
	}
	
	@After
	public void tearDown(){
		fileTable.delete();
		for(FileAddress f : files){
			f.getFile().delete();
		}
	}
	

}