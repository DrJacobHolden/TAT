package file_system.element;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Tate on 21/05/2016.
 */
public interface FileSystemElement {
    public void save() throws IOException;
    public String[] getFileExtensions();
}
