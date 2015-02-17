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
		return super.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
    
    
    
}
