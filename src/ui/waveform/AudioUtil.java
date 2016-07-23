package ui.waveform;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by kalda on 24/07/2016.
 */
public class AudioUtil {
    public static int[] getFrameArray(AudioInputStream audioStream) throws IOException {
        //With help from http://codeidol.com/java/swing/Audio/Build-an-Audio-Waveform-Display/

        int frameLength = (int) audioStream.getFrameLength();
        int frameSize = audioStream.getFormat().getFrameSize();

        byte[] bytes = new byte[frameLength * frameSize];
        int[] frames = new int[frameLength];

        audioStream.read(bytes);

        for (int t = 0, sampleIndex=0; t < bytes.length; sampleIndex++) {
            int sample = getSixteenBitSample((int) bytes[t++], (int) bytes[t++]);
            frames[sampleIndex] = sample;
        }

        return frames;
    }

    private static int getSixteenBitSample(int high, int low) {
        return (high << 8) + (low & 0x00ff);
    }

    public static void writeShortLE(OutputStream out, short value) throws IOException {
        //From http://stackoverflow.com/questions/9179536/writing-pcm-recorded-data-into-a-wav-file-java-android
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
    }

    public static void writeIntLE(OutputStream out, int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 24) & 0xFF);
    }
}
