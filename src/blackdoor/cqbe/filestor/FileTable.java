package blackdoor.cqbe.filestor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.addressing.FileAddress;

public class FileTable {
	
	public static final Charset charset = StandardCharsets.UTF_8;
	
	public static void setEntry(File table, String path, FileAddress value) throws IOException{
		File lockFile = new File("default.filestore.lock");
		while(!lockFile.renameTo(lockFile)){
			
		}
		
		RandomAccessFile rafLock = new RandomAccessFile(lockFile,"rw");
		FileChannel fc = rafLock.getChannel();
		FileLock lock = fc.lock();
		
		BufferedReader reader = new BufferedReader(new FileReader(table));
		String current = reader.readLine();
		//System.out.println(current);
		JSONObject ft = new JSONObject(current);
		ft.put(path, value.overlayAddressToString());
		PrintWriter writer = new PrintWriter(table);
		writer.print(ft.toString());
		writer.close();
		reader.close();
		
		lock.release();
		fc.close();
		rafLock.close();
		
	}
	
	/**
	 * 
	 * @param table
	 * @param path
	 * @return the address at path in table, or null if there is no address for that path
	 * @throws IOException
	 * @throws AddressException
	 */
	public static Address getEntry(File table, String path) throws IOException, AddressException{
		Address ret;
		FileChannel fc = FileChannel.open(table.toPath(), StandardOpenOption.READ);
		//FileLock fl = fc.lock();
		ByteBuffer bb = ByteBuffer.allocate((int) table.length());
		bb.clear();
		fc.read(bb);
		JSONObject ft = new JSONObject(new String(bb.array(), charset));
		try {
			ret = new Address(ft.getString(path));
		} catch (JSONException e) {
			return null;
		}
		//fl.close();
		fc.close();
		return ret;
	}
	
	/**
	 * 
	 * @param table
	 * @param path
	 * @throws IOException
	 */
	public static void removeEntry(File table, String path) throws IOException{
		File lockFile = new File("default.filestore.lock");
		while(!lockFile.renameTo(lockFile)){
			
		}
		
		RandomAccessFile rafLock = new RandomAccessFile(lockFile,"rw");
		FileChannel fc = rafLock.getChannel();
		FileLock lock = fc.lock();
		
		
		File temp = new File("temp.txt");
		BufferedReader reader = new BufferedReader(new FileReader(table));
		BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
		
		JSONObject ft = new JSONObject(reader.readLine());
		ft.remove(path);
		writer.write(ft.toString() + System.getProperty("line.seperator"));
		writer.close();
		reader.close();
		Boolean success = temp.renameTo(table);
		
		reader.close();
		writer.close();
		lock.release();
		fc.close();
		rafLock.close();
	}
	
	public static void listEntries(File table, String base) throws JSONException, IOException{
		
		BufferedReader reader = new BufferedReader(new FileReader(table));
		JSONObject ft = new JSONObject(reader.readLine());
		Iterator<?> keys = ft.keys();
		Boolean allFlag = base.equals("-a");
		if(!keys.hasNext()) System.out.println("FileStore is Empty!");
		while(keys.hasNext()){
			String key = (String)keys.next();
			if(allFlag){
				System.out.println(key + " : " + ft.get(key));
			} else {
				if(key.startsWith(base)){
					System.out.println(key);
				}
			}
		}
		reader.close();
	}
	
	public static void listEntries(File table) throws JSONException, IOException{
		listEntries(table,"");
	}
}