package tat.corpus.file;

import tat.corpus.Segment;
import tat.ui.element.annotation.AnnotationDisplay;

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

    private final Charset ENCODING = StandardCharsets.UTF_8;

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
            loadFromFile(file);
        } catch (IOException e) {
            //Meh, we tried. Annotation can be empty string.
            LOGGER.info("Failed to load annotation file.");
        }
    }

    public AnnotationFile(Segment segment, Path path) {
        this.segment = segment;
        try {
            File file = getFileForPath(path);
            loadFromFile(file);
        } catch (IOException e) {
            //Meh, we tried. Annotation can be empty string.
            LOGGER.info("Couldn't find file at " + path + " with extensions " + Arrays.toString(getFileExtensions()));
        }
    }

    private void loadFromFile(File file) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        this.annotation = new String(encoded, ENCODING).trim();
    }

    @Override
    public void save() throws FileNotFoundException {
        File saveFile = Paths.get(segment.getPath(this).toString() + (FILE_EXTENSIONS[0])).toFile();
        if (!isEmpty()) {
            //Ensure directory exists
            saveFile.toPath().getParent().toFile().mkdirs();
            PrintWriter out = new PrintWriter(saveFile);
            out.close();
        } else {
            //Delete empty annotation if it exists
            saveFile.delete();
        }
    }

    public String getString() {
        return annotation;
    }

    public void setString(String s) {
        s = s.trim();
        if (s.equals(AnnotationDisplay.DEFAULT_TEXT)) {
            s = "";
        }
        if (!s.equals(annotation)) {
            segment.getRecording().invalidateSave();
            annotation = s;
        }
    }

    public AnnotationFile split(Segment newSegment, int strPos) {
        AnnotationFile newAnnotationFile = new AnnotationFile(newSegment, annotation.substring(strPos));
        annotation = annotation.substring(0, strPos);
        return newAnnotationFile;
    }

    public void join(AnnotationFile annotationFile2) {
        annotation = (annotation + " " + annotationFile2.annotation).trim();
    }

    public boolean isEmpty() {
        String stripped = getString().trim();
        return stripped.equals("");
    }

    @Override
    public String[] getFileExtensions() {
        return FILE_EXTENSIONS;
    }
}
