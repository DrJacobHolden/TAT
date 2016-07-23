package file_system.element;

import file_system.Segment;
import sun.plugin.javascript.navig.Array;
import ui.text_box.Annotation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by Tate on 21/05/2016.
 */
public class AnnotationFile extends BaseFileSystemElement {

    public static final String[] FILE_EXTENSIONS = new String[]{".txt"};

    private static Logger LOGGER = Logger.getLogger(AnnotationFile.class.getName());

    private final Charset ENCODING =  StandardCharsets.UTF_8;

    private Segment segment;

    //Empty string if not loaded
    private String annotation = "";

    public AnnotationFile(Segment segment, String annotation) {
        this.segment = segment;
        this.annotation = annotation;
    }

    public AnnotationFile(Segment segment, File file) {
        this.segment = segment;
        try {
            byte[] encoded = Files.readAllBytes(file.toPath());
            this.annotation = new String(encoded, ENCODING);
        } catch (IOException e) {
            //Meh, we tried. Annotation can be empty string.
            LOGGER.info("Failed to load annotation file.");
        }
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
        File saveFile = Paths.get(segment.getPath(this).toString() + (FILE_EXTENSIONS[0])).toFile();
        //Ensure directory exists
        saveFile.toPath().getParent().toFile().mkdirs();
        PrintWriter out = new PrintWriter(saveFile);
        out.println(annotation);
        out.close();
    }

    public String getString() {
        return annotation;
    }

    public void setString(String str) {
        annotation = str;
    }

    public AnnotationFile split(Segment newSegment, int strPos) {
        AnnotationFile newAnnotationFile = new AnnotationFile(newSegment, annotation.substring(strPos));
        annotation = annotation.substring(0, strPos);
        return newAnnotationFile;
    }

    @Override
    public String[] getFileExtensions() {
        return FILE_EXTENSIONS;
    }
}
