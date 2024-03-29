package tat.corpus.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

/**
 * Created by kalda on 28/06/2016.
 */
public abstract class BaseFileSystemElement implements FileSystemElement {

    public File getFileForPath(Path path) throws FileNotFoundException {
        for (String ext : getFileExtensions()) {
            File possibleFile = new File(path.toString() + ext);
            if (path.toString().endsWith(ext)) {
                return path.toFile();
            } else if (possibleFile.exists()) {
                return possibleFile;
            }
        }
        throw new FileNotFoundException();
    }
}
