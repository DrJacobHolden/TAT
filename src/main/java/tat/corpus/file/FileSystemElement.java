package tat.corpus.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Tate on 21/05/2016.
 */
public interface FileSystemElement {
    public void save() throws IOException;
    public String[] getFileExtensions();
    public File getFileForPath(Path path) throws FileNotFoundException;
}

