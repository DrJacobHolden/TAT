package file_system;

import alignment.formats.TextGrid;
import file_system.element.AlignmentFile;
import file_system.element.AnnotationFile;
import file_system.element.AudioFile;
import file_system.element.FileSystemElement;
import sun.audio.AudioStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by kalda on 25/06/2016.
 * Represents related alignment files, audio files and transcriptions.
 * A grouping contains the associated metadata.
 */
public class Segment {

    private final FileSystem fileSystem;

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    private int segmentNumber;
    private int speakerId;
    private String baseName;

    public int getSegmentNumber() {
        return segmentNumber;
    }

    public int getSpeakerId() {
        return speakerId;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setSegmentNumber(int segmentNumber) {
        this.segmentNumber = segmentNumber;
    }

    public void setSpeakerId(int speakerId) {
        this.speakerId = speakerId;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    private AlignmentFile alignmentFile;
    private AnnotationFile annotationFile;
    private AudioFile audioFile;

    public AlignmentFile getAlignmentFile() {
        if (alignmentFile == null) {
            alignmentFile = new AlignmentFile(this, getPath(AlignmentFile.class));
        } //TODO: Catch error opening
        return alignmentFile;
    }

    public AnnotationFile getAnnotationFile() {
        if (annotationFile == null) {
            annotationFile = new AnnotationFile(this, getPath(AnnotationFile.class));
        }
        return annotationFile;
    }

    public AudioFile getAudioFile() {
        if (audioFile == null) {
            try {
                audioFile = new AudioFile(this, getPath(AudioFile.class));
            } catch (FileNotFoundException e) {
                return null;
            }
        }
        return audioFile;
    }

    public Segment(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public Segment(FileSystem fileSystem, int segmentNumber, int speakerId, String baseName) {
        this.fileSystem = fileSystem;
        this.segmentNumber = segmentNumber;
        this.speakerId = speakerId;
        this.baseName = baseName;
    }

    public Segment addAlignment(TextGrid alignment) {
        alignmentFile = new AlignmentFile(this, alignment);
        return this;
    }

    public Segment addAnnotation(String annotation) {
        annotationFile = new AnnotationFile(this, annotation);
        return this;
    }

    public Segment addAudio(AudioStream stream) {
        audioFile = new AudioFile(this, stream);
        return this;
    }

    public void save() throws FileNotFoundException {
        if (annotationFile != null) {
            annotationFile.save();
        }
        if (alignmentFile != null) {
            alignmentFile.save();
        }
        if (audioFile != null) {
            audioFile.save();
        }
    }

    public Path getPath(FileSystemElement element) {
        return getPath(element.getClass());
    }

    public Path getPath(Class<? extends FileSystemElement> cls) {
        if (AudioFile.class.isAssignableFrom(cls)) {
            return fileSystem.pathFromString(this, fileSystem.getAudioStorageRule());
        } else if (AnnotationFile.class.isAssignableFrom(cls)) {
            return fileSystem.pathFromString(this, fileSystem.getAnnotationStorageRule());
        } else if (AlignmentFile.class.isAssignableFrom(cls)) {
            return fileSystem.pathFromString(this, fileSystem.getAlignmentStorageRule());
        }
        //TODO: Throw error
        return null;
    }
}
