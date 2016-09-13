package tat.audio;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by kalda on 28/07/2016.
 */
public class WavLoader {

    public byte[] toByteArray(File file) throws IOException, UnsupportedAudioFileException {
        //Inefficient if we are converting, but needed to prevent file handle bug in Java
        byte[] data = Files.readAllBytes(file.toPath());
        AudioInputStream is = AudioSystem.getAudioInputStream(new ByteArrayInputStream(data));

        AudioFormat format = is.getFormat();

        if (format.getChannels() != 1 || format.getSampleSizeInBits() != 16) {
            data = convertToMono16Little(is);
        }
        is.close();
        return data;
    }

    public byte[] convertToMono16Little(AudioInputStream in) throws IOException {
        //Potentially slow
        AudioFormat format = new AudioFormat(in.getFormat().getSampleRate(), 16, 1, true, false);
        AudioInputStream out = AudioSystem.getAudioInputStream(format, in);
        ByteArrayOutputStream outByte = new ByteArrayOutputStream();
        AudioSystem.write(out, AudioFileFormat.Type.WAVE, outByte);
        return outByte.toByteArray();
    }
}
