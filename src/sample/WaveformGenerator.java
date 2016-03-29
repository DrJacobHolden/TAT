package sample;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by max on 29/03/16 with help from http://codeidol.com/java/swing/Audio/Build-an-Audio-Waveform-Display/
 */
public class WaveformGenerator {

    private AudioInputStream audioInputStream;

    private int frameLength;
    //private float sampleRate;
    private int frameSize;
    private int numChannels;


    public WaveformGenerator(File f) throws IOException, UnsupportedAudioFileException {
        audioInputStream = AudioSystem.getAudioInputStream(f);
        analyseInputFile();
    }

    public void analyseInputFile() {
        frameLength = (int) audioInputStream.getFrameLength();
        frameSize = audioInputStream.getFormat().getFrameSize();
        numChannels = audioInputStream.getFormat().getChannels();
        audioInputStream.getFormat();
    }

    public int[] getFrameArray() throws IOException {
        //Fail if not mono
        //Fail if not 16 bit

        byte[] bytes = new byte[frameLength * frameSize];
        int[] frames = new int[frameLength];

        audioInputStream.read(bytes);

        for (int t = 0, sampleIndex=0; t < bytes.length; sampleIndex++) {
            int sample = getSixteenBitSample((int) bytes[t++], (int) bytes[t++]);
            frames[sampleIndex] = sample;
        }

        return frames;
    }

    public Image getWaveformImage(int resolution) throws IOException {
        int[] frames = getFrameArray();

        int width = frames.length / resolution;
        int height = 256;
        Color colour= Color.color(0, 1, 0);

        WritableImage waveformImage = new WritableImage(width, height);

        PixelWriter pixelWriter = waveformImage.getPixelWriter();
        for (int x=0; x<width; x++) {
            int amplitude = 0;
            for (int i=x*resolution; i<(x+1) *resolution; i++) {
                if (frames[i] > amplitude) {
                    amplitude = frames[i]/255;
                }
            }
            for (int y=0; y<amplitude; y++) {
                pixelWriter.setColor(x, y, colour);
            }
        }

        return waveformImage;
    }

    private int getSixteenBitSample(int high, int low) {
        return (high << 8) + (low & 0x00ff);
    }
}
