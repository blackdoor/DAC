package blackdoor.crypto;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;

import blackdoor.struct.ByteQueue;

public class EncryptedInputStream extends FilterInputStream{
	private HistoricSHE cipher;
	private ByteQueue buffer;
	
	public EncryptedInputStream(InputStream in, HistoricSHE cipher) {
		super(in);
		if(!cipher.isConfigured())
			throw new RuntimeException("Cipher not configured.");
		this.cipher = cipher;
		buffer = new ByteQueue(cipher.blockSize*2);
		buffer.setResizable(true);
	}
	
	private void bufferBlock() throws IOException{
		byte[] plainText = new byte[cipher.blockSize];
		in.read(plainText);
		buffer.enQueue(cipher.update(plainText));
	}
	
	private void bufferBytes(int size) throws IOException{
		while(buffer.filled() < size){
			bufferBlock();
			}
	}
	
	public int read() throws IOException{
		try{
			return buffer.deQueue(1)[0];
		}catch(BufferUnderflowException e){
			bufferBlock();
			return read();
		}
	}
	
	public int read(byte[] b) throws IOException{
		return read(b, 0, b.length);
	}
	
	public int read(byte[]b, int off, int len) throws IOException{
		bufferBytes(len-off);
		buffer.deQueue(b, off, len);
		return len-off;
	}
	
}
