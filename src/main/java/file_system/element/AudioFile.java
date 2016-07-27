package file_system.element;

import file_system.Segment;
import org.apache.commons.io.EndianUtils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.nio.file.*;

/**
 * Created by Tate on 21/05/2016.
 */
public class AudioFile extends BaseFileSystemElement {

    public static final String[] FILE_EXTENSIONS = new String[]{".wav"};
    public static final int MIN_SPLIT_FRAMES = 50;

    int WAV_HEADER_LENGTH = 44;

    private Segment segment;

    private byte[] storedFile;

    private InputStream getDataStream() {
        return new ByteArrayInputStream(storedFile);
    }

    public AudioInputStream getAudioStream() {
        try {
            return AudioSystem.getAudioInputStream(getDataStream());
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AudioFile(Segment segment, Path path) throws IOException {
        initialise(segment, Files.readAllBytes(getFileForPath(path).toPath()));
    }

    private AudioFile(Segment segment, byte[] data) {
        initialise(segment, data);
    }

    private void initialise(Segment segment, byte[] data) {
        this.segment = segment;
        this.storedFile = data;
    }

    public int getNoFrames() {
        System.out.println(segment.getSegmentNumber());
        AudioInputStream as = getAudioStream();
        int no = (int) as.getFrameLength();
        try {
            as.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return no;
    }

    @Override
    public void save() throws IOException {
        File file = Paths.get(segment.getPath(this).toString() + FILE_EXTENSIONS[0]).toFile();
        //Ensure directory exists
        file.toPath().getParent().toFile().mkdirs();

        //Overwrite existing file, if exists
        file.delete();
        Files.copy(getDataStream(), file.toPath());
    }

    public AudioFile split(Segment newSegment, int frame) throws IOException {
        AudioInputStream audioStream = getAudioStream();
        int frameSize = audioStream.getFormat().getFrameSize();

        byte[] out1 = new byte[WAV_HEADER_LENGTH + frame*frameSize];
        //Copy relevant part of file
        System.arraycopy(storedFile, 0, out1, 0, out1.length);
        //Write length
        EndianUtils.writeSwappedInteger(out1, WAV_HEADER_LENGTH -4, out1.length-WAV_HEADER_LENGTH);

        byte[] out2 = new byte[WAV_HEADER_LENGTH + storedFile.length-out1.length];
        //Copy part of header
        System.arraycopy(storedFile, 0, out2, 0, WAV_HEADER_LENGTH -4);
        //Write length
        EndianUtils.writeSwappedInteger(out2, WAV_HEADER_LENGTH -4, out2.length-WAV_HEADER_LENGTH);
        System.arraycopy(storedFile, out1.length, out2, WAV_HEADER_LENGTH, storedFile.length-out1.length);

        storedFile = out1;
        return new AudioFile(newSegment, out2);
    }

    public void join(AudioFile audioFile2) throws IOException {
        byte[] file2 = audioFile2.storedFile;
        byte[] joined = new byte[storedFile.length+file2.length-WAV_HEADER_LENGTH];
        //Copy all of first file
        System.arraycopy(storedFile, 0, joined, 0, storedFile.length);
        //Append all but header of second file
        System.arraycopy(file2, WAV_HEADER_LENGTH, joined, storedFile.length, file2.length-WAV_HEADER_LENGTH);
        //Write length
        EndianUtils.writeSwappedInteger(joined, WAV_HEADER_LENGTH -4, joined.length-WAV_HEADER_LENGTH);

        storedFile = joined;
    }

    @Override
    public String[] getFileExtensions() {
        return FILE_EXTENSIONS;
    }
}
