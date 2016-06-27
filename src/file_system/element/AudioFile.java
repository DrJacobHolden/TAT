package file_system.element;

import file_system.Segment;
import sun.audio.AudioStream;

import java.io.File;

/**
 * Created by Tate on 21/05/2016.
 */
public class AudioFile implements FileSystemElement {

    private Segment segment;
    //Will have to not be stream
    private AudioStream stream;

    public AudioFile(Segment segment, AudioStream stream) {

    }

    @Override
    public void save() {
        String path = segment.getPath(this);

    }

    public static AudioFile load(String path) {
        return null;
    }
}
