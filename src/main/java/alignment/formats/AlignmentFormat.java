package alignment.formats;

import java.io.InputStream;

/**
 * Created by kalda on 8/04/2016.
 */
public interface AlignmentFormat {
    public String getFileExtension();
    public InputStream getStream();
}
