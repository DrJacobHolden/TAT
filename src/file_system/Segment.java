package file_system;

/**
 * A segment is a combination of an audio file, an annotation file and
 * an optional transcription file.
 *
 * Created by Tate on 21/05/2016.
 */
public class Segment {

    public AudioFile audioFile;
    public AnnotationFile annotationFile;
    public AlignmentFile alignmentFile;

    public String name;
    public int segmentId;

    //TODO: Speakers need to be named and handled more thoroughly for now an ID is fine
    public int speakerId;

}