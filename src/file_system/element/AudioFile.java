package file_system.element;

import file_system.Segment;
import sun.audio.AudioStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;

/**
 * Created by Tate on 21/05/2016.
 */
public class AudioFile extends BaseFileSystemElement {

    public static final String[] FILE_EXTENSIONS = new String[]{".wav"};

    private Segment segment;

    private File file;

    public AudioFile(Segment segment, File audioFile) {
        this.segment = segment;
        this.file = audioFile;
    }

    public AudioFile(Segment segment, Path path) throws FileNotFoundException {
        this.segment = segment;
        //Try to load file with file extensions
        file = getFileForPath(path);
    }

    public File getFile() {
        return file;
    }

    public AudioInputStream getStream() {
        try {
            return AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void save() throws IOException {
        File newFile = Paths.get(segment.getPath(this).toString() + FILE_EXTENSIONS[0]).toFile();
        //Ensure directory exists
        newFile.toPath().getParent().toFile().mkdirs();
        Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public String[] getFileExtensions() {
        return FILE_EXTENSIONS;
    }
}
