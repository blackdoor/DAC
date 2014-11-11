package blackdoor.crypto;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class EncryptedOutputStream extends FilterOutputStream{
	private HistoricSHE cipher;
	/**
	 * If true, when this stream is closed using its close() method, any 
	 * unprocessed data will be padded and then encrypted.
	 * If false, when this stream is closed any unprocessed data will be discarded.
	 */
	public boolean padOnClose;
	
	/**
	 * Constructs an EncryptedOutputStream from an OutputStream and a Cipher. 
	 * Note: if the specified output stream or cipher is null, a NullPointerException may be thrown later when they are used.
	 * @param out the OutputStream object
	 * @param cipher an initialized Cipher object
	 */
	public EncryptedOutputStream(OutputStream out, HistoricSHE cipher) {
		super(out);
		if(!cipher.isConfigured())
			throw new RuntimeException("Cipher not configured.");
		padOnClose = false;
		this.cipher = cipher;
	}
	/**
	 * Writes the specified byte to this output stream.
	 */
	@Override
	public void write(int b) throws IOException{
		out.write(cipher.update(new byte[]{(byte) b}));
	}
	
	
	@Override
	public void write(byte[] b) throws IOException{
		//byte[] debug;// = new byte[b.length];
		//debug = ;
		//System.out.print(Misc.bytesToHex(debug));
		out.write(cipher.update(b));
		//return debug;
	}
	
	public void write(byte[] b, int off, int len) throws IOException{
		byte[] todo = new byte[len];
		System.arraycopy(b, off, todo, 0, len);
		write(todo);
	}
	
	//@Override
//	public void flush() throws IOException{
		//write(cipher.doFinal());
		//out.flush();
	//}
	@Override
	public void close() throws IOException{
		if(padOnClose)
			out.write(cipher.doFinal());
		out.close();
	}
	/**
	 * 
	 * @return the underlying SHE cipher for this Stream
	 */
	public HistoricSHE getCipher(){
		return cipher;
	}
	
}
