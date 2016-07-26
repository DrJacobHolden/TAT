package alignment.formats;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kalda on 8/04/2016.
 */
public class TextGrid implements AlignmentFormat {

    public static TextGrid load(InputStream in)
        throws IOException {
        in.close();
        return new TextGrid();
    }
}
