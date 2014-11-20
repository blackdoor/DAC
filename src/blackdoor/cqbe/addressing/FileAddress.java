package blackdoor.cqbe.addressing;

import java.io.File;

/**
 * Created by nfischer3 on 11/19/14.
 */
public class FileAddress extends Address {
    private File f;

    public FileAddress(File f){

    }

    public File getFile(){
        return f;
    }
}
