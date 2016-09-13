package tat.audio;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import tat.ui.Colours;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

/**
 * Created by max on 29/03/16
 * Generates a waveform image for an audio file
 */
public class WaveformGenerator {

    private AudioInputStream audioStream;

    private int frameLength;
    private int frameSize;

    public WaveformGenerator(AudioInputStream audioStream) throws UnsupportedAudioFileException {
        this.audioStream = audioStream;
        if (audioStream.getFormat().getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            throw new UnsupportedAudioFileException("Must PCM_SIGNED");
        } else if (audioStream.getFormat().getChannels() != 1) {
            throw new UnsupportedAudioFileException("Must be mono");
        }
        analyseInputFile();
    }

    public void analyseInputFile() {
        frameLength = (int) audioStream.getFrameLength();
        frameSize = audioStream.getFormat().getFrameSize();
    }

    /**
     * Actually generate an image and return it
     *
     * @param widthResolution Generate one vertical line per this many frames (larger value is a smaller image)
     * @return The image
     * @throws IOException
     */
    public Image getWaveformImage(int widthResolution, int height) throws IOException {
        int[] frames = AudioUtil.getFrameArray(audioStream);

        int width = frames.length / widthResolution;
        Color background = Colours.SECONDARY_GRAY;
        Color colour = Colours.TRANSPARENT;

        int sampleSize = (int) Math.pow(2, 16);
        double verticalScaling = height / (double) sampleSize;
        int halfHeight = height / 2;

        WritableImage waveformImage = new WritableImage(width, height);
        PixelWriter pixelWriter = waveformImage.getPixelWriter();

        for (int x = 0; x < width; x++) {
            int maxAmplitude = 0, minAmplitude = 0;
            for (int i = x * widthResolution; i < (x + 1) * widthResolution; i++) {
                int offset = frames[i];
                if (offset > maxAmplitude) {
                    maxAmplitude = offset;
                } else if (offset < minAmplitude) {
                    minAmplitude = offset;
                }
            }
            int minY = (int) (minAmplitude * verticalScaling) + halfHeight;
            int maxY = (int) (maxAmplitude * verticalScaling) + halfHeight;
            for (int y = 0; y < height; y++) {
                if (y >= minY && y <= maxY) {
                    pixelWriter.setColor(x, y, colour);
                } else {
                    pixelWriter.setColor(x, y, background);
                }
            }
        }

        return waveformImage;
    }
}
