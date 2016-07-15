package file_system.element;

import alignment.formats.TextGrid;
import file_system.Segment;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by Tate on 21/05/2016.
 */
public class AlignmentFile extends BaseFileSystemElement {

    public static final String[] FILE_EXTENSIONS = new String[]{};

    public AlignmentFile(Segment segment, TextGrid grid) {

    }

    public AlignmentFile(Segment segment, Path path) {

    }

    public AlignmentFile(Segment segment, File file) {

    }

    @Override
    public void save() {

    }

    @Override
    public String[] getFileExtensions() {
        return new String[]{".textgrid"};
    }
}
