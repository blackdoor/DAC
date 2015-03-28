package blackdoor.cqbe.filestor;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.json.JSONException;
import org.json.JSONObject;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.addressing.FileAddress;

public class FileTable {
	
	public static final Charset charset = StandardCharsets.UTF_8;
	
	public static void setEntry(File table, String path, FileAddress value) throws IOException{
		FileChannel fc = FileChannel.open(table.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE);
		FileLock fl = fc.lock();
		ByteBuffer bb = ByteBuffer.allocate((int) table.length());
		fc.read(bb);
		JSONObject ft = new JSONObject(new String(bb.array(), charset));
		ft.put(path, value.overlayAddressToString());
		fc.write(ByteBuffer.wrap(ft.toString().getBytes(charset)));
		fl.close();
		fc.close();
		bb.clear();
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
}
