package file_system.element;

import file_system.Segment;
import sun.audio.AudioStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Tate on 21/05/2016.
 */
public class AudioFile implements FileSystemElement {

    public static final String[] FILE_EXTENSIONS = {".wav"};

    private Segment segment;

    private File file;

    public AudioFile(Segment segment, AudioStream stream) {

    }

    public AudioFile(Segment segment, Path path) throws FileNotFoundException {
        //Try to load file with file extensions
        for (String ext : FILE_EXTENSIONS) {
            File possibleFile = new File(path.toString()+ext);
            if (possibleFile.exists()) {
                file = possibleFile;
                return;
            }
        }
        throw new FileNotFoundException();
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
    public void save() {
        Path path = segment.getPath(this);
    }
}
