package sample;

import javafx.scene.Node;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Created by max on 29/03/16.
 */
public interface WaveformDisplay {
    void setAudioStream(File audioFile) throws IOException, UnsupportedAudioFileException;

    void setAudioStream(AudioInputStream audioStream) throws UnsupportedAudioFileException;
}
