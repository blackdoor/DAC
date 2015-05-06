package blackdoor.cqbe.addressing;

import java.io.File;

/**
 * <p>
 * 
 * @author Nathan Fischer
 * @version v1.0.0 - May 4, 2015
 */
public abstract class FileAddress extends Address {
	protected File f;

	// public static FileAddress(File f) throws IOException;

	protected void setFile(File f) {
		this.f = f;
	}

	/**
	 * Returns the file object referenced by this address.
	 * <p>
	 * 
	 * @return
	 */
	public File getFile() {
		return f;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blackdoor.cqbe.addressing.Address#toString()
	 */
	@Override
	public String toString() {
		return "FileAddress [f=" + f + ", overlayAddressToString()="
				+ overlayAddressToString() + "]";
	}

}
