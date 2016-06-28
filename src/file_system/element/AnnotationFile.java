package file_system.element;

import file_system.Segment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Created by Tate on 21/05/2016.
 */
public class AnnotationFile implements FileSystemElement {

    private Segment segment;
    private String annotation;

    public AnnotationFile(Segment segment, String annotation) {
        this.segment = segment;
        this.annotation = annotation;
    }

    public AnnotationFile(Segment segment, Path path) {

    }

    @Override
    public void save() throws FileNotFoundException {
        Path path = segment.getPath(this);
        PrintWriter out = new PrintWriter(path.toFile());
        out.println(annotation);
    }
}
