package backup.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;

public final class FileLock {
    
    private final String uid;
    private final File file;
    private RandomAccessFile raFile;
    private java.nio.channels.FileLock fLock;

    public FileLock(String uid) throws IOException, 
    FileNotFoundException, URISyntaxException {
        this.uid = uid;
        String tmpDir = System.getProperty("java.io.tmpdir");        
        file = new File(tmpDir + this.uid + ".lock");
    }
    
    public boolean tryLock() throws FileNotFoundException,
    IOException, URISyntaxException {
        raFile = new RandomAccessFile(file, "rw");
        fLock = raFile.getChannel().tryLock();
        return fLock != null;
    }

    public void release() throws FileNotFoundException,
    IOException, URISyntaxException {
        fLock.release();
        raFile.close();
    }   
    
}
