package file_system.element;

import file_system.Segment;
import sun.audio.AudioStream;
import ui.waveform.AudioUtil;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileWriter;
import java.io.*;
import java.nio.file.*;

/**
 * Created by Tate on 21/05/2016.
 */
public class AudioFile extends BaseFileSystemElement {

    public static final String[] FILE_EXTENSIONS = new String[]{".wav"};
    public static final int MIN_SPLIT_FRAMES = 50;

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

    public int getNoFrames() {
        AudioInputStream as = getStream();
        int no = (int) as.getFrameLength();
        try {
            as.close();
        } catch (IOException e) {
            //We're really in trouble
            e.printStackTrace();
        }
        return no;
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

        System.out.println("Copying " + file.toString() + " to " + newFile.toString());
        Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public AudioFile split(Segment newSegment, long frame) throws IOException {
        File file1 = File.createTempFile("split1", Long.toString(System.nanoTime()) + FILE_EXTENSIONS[0]);
        file1.deleteOnExit();
        File file2 = File.createTempFile("split2", Long.toString(System.nanoTime()) + FILE_EXTENSIONS[0]);
        file2.deleteOnExit();

        AudioInputStream audioStream = getStream();
        AudioInputStream audioStream1 = new AudioInputStream(audioStream, audioStream.getFormat(), frame);
        AudioInputStream audioStream2 = new AudioInputStream(audioStream, audioStream.getFormat(), audioStream.getFrameLength()-frame);

        AudioSystem.write(audioStream1, AudioFileFormat.Type.WAVE, file1);
        AudioSystem.write(audioStream2, AudioFileFormat.Type.WAVE, file2);

        audioStream.close();
        audioStream1.close();
        audioStream2.close();

        file = file1;

        return new AudioFile(newSegment, file2.toPath());
    }

    public void join(AudioFile audioFile2) throws IOException {
        File joinedFile = File.createTempFile("join", Long.toString(System.nanoTime()) + FILE_EXTENSIONS[0]);
        joinedFile.deleteOnExit();

        AudioInputStream audio1 = getStream();
        AudioInputStream audio2 = audioFile2.getStream();
        AudioInputStream audioJoined = new AudioInputStream(new SequenceInputStream(audio1, audio2), audio1.getFormat(), audio1.getFrameLength() + audio2.getFrameLength());

        AudioSystem.write(audioJoined, AudioFileFormat.Type.WAVE, joinedFile);
        audioJoined.close();
        audio1.close();
        audio2.close();

        file = joinedFile;
    }

    @Override
    public String[] getFileExtensions() {
        return FILE_EXTENSIONS;
    }
}
