package file_system.element;

import file_system.Segment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

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

    @Override
    public void save() throws FileNotFoundException {
        String path = segment.getPath(this);
        PrintWriter out = new PrintWriter(path);
        out.println(annotation);
    }

    public static AnnotationFile load(String path) {
        return null;
    }
}
