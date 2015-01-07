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

    public File getFile(){
        return f;
    }
    
}
