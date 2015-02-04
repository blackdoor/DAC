package blackdoor.cqbe.addressing;

import java.io.File;
import java.io.IOException;

import blackdoor.crypto.Hash;

/**
 * Created by nfischer3 on 11/19/14.
 */
public abstract class FileAddress extends Address {
    protected File f;

   // public static FileAddress(File f) throws IOException;
    
    protected void setFile(File f){
    	this.f = f;
    }

    public File getFile(){
        return f;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((f == null) ? 0 : f.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		FileAddress other = (FileAddress) obj;
		if (f == null) {
			if (other.f != null)
				return false;
		} else if (!f.equals(other.f))
			return false;
		return true;
	}
    
    
    
}
