package file_system.element;

import file_system.Segment;
import sun.plugin.javascript.navig.Array;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by Tate on 21/05/2016.
 */
public class AnnotationFile extends BaseFileSystemElement {

    private static Logger LOGGER = Logger.getLogger(AnnotationFile.class.getName());

    private final Charset ENCODING =  StandardCharsets.UTF_8;

    private Segment segment;

    //Empty string if not loaded
    private String annotation = "";

    public AnnotationFile(Segment segment, String annotation) {
        this.segment = segment;
        this.annotation = annotation;
    }

    public AnnotationFile(Segment segment, Path path) {
        this.segment = segment;
        try {
            File file = getFileForPath(path);
            byte[] encoded = Files.readAllBytes(file.toPath());
            this.annotation = new String(encoded, ENCODING);
        } catch (IOException e) {
            //Meh, we tried. Annotation can be empty string.
            LOGGER.info("Couldn't find file at "+path+" with extensions "+ Arrays.toString(getFileExtensions()));
        }
    }

    @Override
    public void save() throws FileNotFoundException {
        Path path = segment.getPath(this);
        PrintWriter out = new PrintWriter(path.toFile());
        out.println(annotation);
    }

    public String getString() {
        return annotation;
    }

    @Override
    public String[] getFileExtensions() {
        return new String[]{".txt"};
    }
}
