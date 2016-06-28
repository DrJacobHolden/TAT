package file_system.element;

import file_system.FileSystem;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Tate on 21/05/2016.
 */
public interface FileSystemElement {
    public void save() throws FileNotFoundException;
    public String[] getFileExtensions();
}
