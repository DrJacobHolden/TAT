package tat.audio;

import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by kalda on 28/07/2016.
 */
public class AudioConvertor {
    public static byte[] toByteArray(File input) throws IOException, UnsupportedAudioFileException {
        WavLoader loader = new WavLoader();

        if (!input.toString().endsWith(".wav")) {
            try {
                Converter myConverter = new Converter();
                File convertedFile = Files.createTempFile(input.getName(), ".wav").toFile();
                convertedFile.deleteOnExit();
                myConverter.convert(input.getPath(), convertedFile.getPath());
                input = convertedFile;
            } catch (JavaLayerException e) {
                e.printStackTrace();
                throw new UnsupportedAudioFileException();
            }
        }

        return loader.toByteArray(input);
    }
}
