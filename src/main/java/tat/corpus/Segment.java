package tat.corpus;

import tat.alignment.AlignmentException;
import tat.alignment.formats.AlignmentFormat;
import tat.alignment.formats.TextGrid;
import tat.alignment.maus.WebMaus;
import tat.corpus.attribute.CustomAttributeInstance;
import tat.corpus.file.AlignmentFile;
import tat.corpus.file.AnnotationFile;
import tat.corpus.file.AudioFile;
import tat.corpus.file.FileSystemElement;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kalda on 25/06/2016.
 * Represents related tat.alignment files, audio files and transcriptions.
 * A grouping contains the associated metadata.
 */
public class Segment {

    //EEK. Lots of associations. BEWARE! Constant for safety.
    private final Corpus corpus;
    public List<CustomAttributeInstance> customAttributes = new ArrayList<>();
    private Recording recording;
    //Default to 1
    private int segmentNumber = 1;
    private int speakerId = 0;
    //This is only used if the segment is not yet associated with a recording yet.
    private String baseName;
    private AlignmentFile alignmentFile;
    private AnnotationFile annotationFile;
    private AudioFile audioFile;

    public Segment(Corpus corpus) {
        this.corpus = corpus;
    }

    public Segment(Corpus corpus, int segmentNumber, int speakerId, String baseName) {
        this.corpus = corpus;
        this.segmentNumber = segmentNumber;
        this.speakerId = speakerId;
        this.baseName = baseName;
    }

    public Corpus getCorpus() {
        return corpus;
    }

    public Recording getRecording() {
        return recording;
    }

    public void setRecording(Recording recording) {
        if (this.recording != null) {
            throw new UnsupportedOperationException("Re-assignment of recording is not allowed.");
        }
        this.recording = recording;
    }

    public int getSegmentNumber() {
        return segmentNumber;
    }

    public void setSegmentNumber(int segmentNumber) {
        attributeChanged();
        this.segmentNumber = segmentNumber;
    }

    public int getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(int speakerId) {
        attributeChanged();
        this.speakerId = speakerId;
    }

    public String getBaseName() {
        //Return the basename of the recording if the recording has been set
        if (recording != null) {
            return recording.getBaseName();
        }
        return baseName;
    }

    public void setBaseName(String baseName) {
        if (recording != null) {
            throw new UnsupportedOperationException("Set the basename of the recording instead");
        }
        this.baseName = baseName;
    }

    private void attributeChanged() {
        if (recording != null) {
            recording.markFilesForDelete(getPossiblePaths());
        }
    }

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

    protected void loadExternalAnnotationFile(File file) {
        //When saved will use generated file name
        this.annotationFile = new AnnotationFile(this, file);
    }

    protected void loadExternalAlignmentFile(File file) {
        //When saved will use generated file name
        this.alignmentFile = new AlignmentFile(this, file);
    }

    public AudioFile getAudioFile() {
        if (audioFile == null) {
            try {
                audioFile = new AudioFile(this, getPath(AudioFile.class));
            } catch (IOException | UnsupportedAudioFileException e) {
                e.printStackTrace();
                return null;
            }
        }
        return audioFile;
    }

    protected void loadExternalAudioFile(File file) throws IOException, UnsupportedAudioFileException {
        this.audioFile = new AudioFile(this, file.toPath());
    }

    public void save() throws IOException {
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
            return corpus.pathFromString(this, corpus.getAudioStorageRule());
        } else if (AnnotationFile.class.isAssignableFrom(cls)) {
            return corpus.pathFromString(this, corpus.getAnnotationStorageRule());
        } else if (AlignmentFile.class.isAssignableFrom(cls)) {
            return corpus.pathFromString(this, corpus.getAlignmentStorageRule());
        }
        //TODO: Throw error
        return null;
    }

    public List<Path> getPossiblePaths() {
        List<Path> paths = new ArrayList<>();

        Arrays.stream(new FileSystemElement[]{audioFile, annotationFile, alignmentFile})
                .filter(element -> element != null)
                .forEach(element -> {
                    try {
                        paths.add(element.getFileForPath(getPath(element)).toPath());
                    } catch (FileNotFoundException e) {
                        //No worries!
                    }
                });

        return paths;
    }

    public Segment split(long frame, int stringPos) throws IOException {
        Segment segment2 = new Segment(corpus, segmentNumber + 1, speakerId, baseName);
        segment2.setRecording(recording);

        AudioFile audio2 = getAudioFile().split(segment2, (int) frame);
        AnnotationFile annotation2 = getAnnotationFile().split(segment2, stringPos);

        segment2.audioFile = audio2;
        segment2.annotationFile = annotation2;

        return segment2;
    }

    public void join(Segment segment2) throws IOException {
        if (segment2.audioFile != null) {
            audioFile.join(segment2.audioFile);
        }
        if (segment2.annotationFile != null) {
            annotationFile.join(segment2.annotationFile);
        }
    }

    public void generateAlignment() throws IOException, AlignmentException {
        WebMaus maus = new WebMaus();
        maus.setOutFormat(TextGrid.class);
        AlignmentFormat response = (TextGrid) maus.generateAlignment(getAnnotationFile().getString(), getAudioFile().getAudioStream());
        this.alignmentFile = new AlignmentFile(this, response);
    }
}
