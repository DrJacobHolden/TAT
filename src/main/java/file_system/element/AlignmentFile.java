package file_system.element;

import alignment.formats.AlignmentFormat;
import alignment.formats.TextGrid;
import file_system.Segment;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Tate on 21/05/2016.
 */
public class AlignmentFile extends BaseFileSystemElement {

    private Segment segment;
    private AlignmentFormat alignment;

    public static final String[] FILE_EXTENSIONS = new String[]{TextGrid.FILE_EXTENSION};

    //Currently this is the only way to make a valid alignment file
    public AlignmentFile(Segment segment, AlignmentFormat alignment) {
        this.alignment = alignment;
        this.segment = segment;
    }

    public AlignmentFile(Segment segment, Path path) {
        this(segment, path.toFile());
    }

    public AlignmentFile(Segment segment, File file) {
        this.segment = segment;
    }

    @Override
    public void save() throws IOException {
        if (alignment != null) {
            Path saveFile = Paths.get(segment.getPath(this).toString() + alignment.getFileExtension());
            Files.copy(alignment.getStream(), saveFile);
        }
    }

    @Override
    public String[] getFileExtensions() {
        return FILE_EXTENSIONS;
    }
}
