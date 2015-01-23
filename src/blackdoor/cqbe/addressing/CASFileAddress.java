package blackdoor.cqbe.addressing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import blackdoor.crypto.Hash;

public class CASFileAddress extends FileAddress {

	
	public CASFileAddress(File f) throws IOException {
		super.f = f;
		overlayAddress = Hash.getFileHash(f);
	}
	
	/**
	 * Creates a new file with the data in bin if one does not already exist.
	 * Throws an exception if the file does already exist
	 * @param f
	 * @param bin
	 * @throws IOException
	 */
	public CASFileAddress(File f, byte[] bin) throws IOException{
		Files.write(f.toPath(), bin, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
		super.f = f;
		overlayAddress = Hash.getFileHash(f);
	}
	
	/**
	 * Don't be stupid and try to read big files into memory.
	 * @return
	 * @throws IOException
	 */
	public byte[] getBinary() throws IOException{
		return Files.readAllBytes(getFile().toPath());
	}
	
		
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CASFileAddress [address="+overlayAddressToString()+", file="+f.getAbsolutePath()+"]";
	}

	
}
