package blackdoor.cqbe.output_logic;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;

import blackdoor.util.DBP;

public class SocketIOWrapper {
	private Socket sock;
	private BufferedInputStream in;
	private OutputStream out;
	public final int BUFFER_SIZE = 256;
	public final float BUFFER_GROWTH_FACTOR = 1.25f;
	public final String ENCODING = "UTF-8";
	
	public SocketIOWrapper(Socket sock) throws IOException{
		this.sock = sock;
		in = new BufferedInputStream(sock.getInputStream());
		out = sock.getOutputStream();
	}
	
	public String read() throws IOException{
		//not feeling fantastic about this implementation, need to test and tweak buffer size and growth factor
		byte[] buffer = new byte[BUFFER_SIZE];
		int x;
		int filled = 0;
		while( (x = in.read()) != 0 && x != -1){
			if(filled >= buffer.length){
				buffer = Arrays.copyOf(buffer, (int) Math.ceil(buffer.length * BUFFER_GROWTH_FACTOR));
			}
			buffer[filled] = (byte) x;
			filled ++;
		}
		return new String(Arrays.copyOfRange(buffer, 0, filled), Charset.forName(ENCODING));		
	}
	
	public byte[] write(Object s) throws IOException{
		byte[] ret = null;
		try {
			ret = String.valueOf(s).getBytes(ENCODING);
			ret = Arrays.copyOf(ret, ret.length + 1);
		} catch (UnsupportedEncodingException e) {
			DBP.printException(e);
		}
		out.write(ret);
		return ret;
	}
	
	public Socket getSocket(){
		return sock;
	}
	
	public void close() throws IOException{
		sock.close();
	}
	
}
