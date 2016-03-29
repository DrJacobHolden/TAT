package sample;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Created by max on 29/03/16.
 */
public class WaveformImageView extends ImageView implements WaveformDisplay {

    protected AudioInputStream audioStream;
    protected WaveformGenerator waveformGenerator;

    public void setAudioStream(File audioFile) throws IOException, UnsupportedAudioFileException {
        setAudioStream(AudioSystem.getAudioInputStream(audioFile));
    }

    public void setAudioStream(AudioInputStream audioStream) throws UnsupportedAudioFileException {
        this.audioStream = audioStream;
        this.waveformGenerator = new WaveformGenerator(audioStream);
        generateAndDisplayWaveform();
    }

    public AudioInputStream getAudioStream() {
        return audioStream;
    }

    public void generateAndDisplayWaveform() {
        try {
            //10 is just a guess at a good resolution. Should really changed depending on zoom.
            int resolution = 10;
            Image waveform = this.waveformGenerator.getWaveformImage(resolution);
            setImage(waveform);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
