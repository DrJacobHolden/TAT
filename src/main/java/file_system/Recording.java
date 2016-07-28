package file_system;

import file_system.element.AlignmentFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by kalda on 28/06/2016.
 */
public class Recording implements Iterable<Segment> {

    public Map<Integer, Segment> getSegments() {
        return segments;
    }

    private String baseName;
    private Map<Integer, Segment> segments = new HashMap<>();
    private int size = 0;

    private List<Path> toDeleteOnSave = new ArrayList<>();

    public int size() {
        return size;
    }

    public String getBaseName() {
        return baseName;
    }

    public Recording(String baseName) {
        this.baseName = baseName;
    }

    public void addSegment(Segment segment) {
        int segmentNo = segment.getSegmentNumber();
        if (segments.get(segmentNo) != null) {
            throw new IllegalStateException("Segment with id " + segmentNo + " already exists");
        }
        segments.put(segmentNo, segment);
        segment.setRecording(this);
        if (segmentNo > size) {
            //Not +1, index from 1
            size = segmentNo;
        }
    }

    //Index from 1
    public Segment getSegment(int segmentNo) {
        return segments.get(segmentNo);
    }

    public boolean missingSegments() {
        for (int i=0; i<size; i++) {
            if (segments.get(i) == null) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, Recording> groupSegments(List<Segment> segments) {
        Map<String, Recording> recordingMap = new HashMap<>();

        for (Segment segment : segments) {
            String baseName = segment.getBaseName();
            Recording recording = recordingMap.get(baseName);
            //We haven't encountered any segments with this basename yet.
            if (recording == null) {
                recording = new Recording(baseName);
                recordingMap.put(baseName, recording);
            }
            recording.addSegment(segment);
        }
        return recordingMap;
    }

    public void save() throws IOException {
        for (Segment segment : this) {
            segment.save();
        }
        deleteMarked();
    }

    public Segment split(Segment segment1, long frame, int stringPos) throws IOException {
        assertCorrectOrdering();

        //Alignment is no longer valid
        maybeMarkAlignmentForDelete(segment1);

        //Split the segment
        Segment segment2 = segment1.split(frame, stringPos);

        //Move segments along to make room for new one
        for (int i=size; i>segment1.getSegmentNumber(); i--) {
            Segment seg = segments.get(i);
            seg.setSegmentNumber(i+1);
            segments.put(i+1, seg);
        }

        //Put segment after first
        segments.put(segment2.getSegmentNumber(), segment2);
        size++;
        assertCorrectOrdering();
        return segment2;
    }

    private void assertCorrectOrdering() {
        int i=1;
        for (Segment segment : this) {
            if (segment == null) {
                throw new IllegalStateException("Segment at position " + i + " is null");
            } else if (segment.getSegmentNumber() != i++) {
                throw new IllegalStateException("Segment " + segment.toString() + " has number " + segment.getSegmentNumber()
                + " when it is in position " + (i-1));
            }
        }
    }

    //Needs to be called whenever a file has an attribute change, or on a join
    private void markFilesForDelete(List<Path> paths) {
        toDeleteOnSave.addAll(paths);
    }

    private void maybeMarkAlignmentForDelete(Segment segment) {
        AlignmentFile alignment = segment.getAlignmentFile();
        try {
            File file = alignment.getFileForPath(segment.getPath(alignment));
            toDeleteOnSave.add(file.toPath());
        } catch (FileNotFoundException e) {
            //No worries, alignment doesn't exist
        }
    }

    private void deleteMarked() {
        while (toDeleteOnSave.size() > 0) {
            Path path = toDeleteOnSave.remove(toDeleteOnSave.size()-1);
            try {
                System.out.println("Deleting file " + path);
                Files.delete(path);
            } catch (IOException e) {
                System.out.println("Failed to delete file " + path);
            }
        }
    }

    public Segment join(Segment segment1, Segment segment2) throws IOException {
        assertCorrectOrdering();

        //Alignment is no longer valid
        maybeMarkAlignmentForDelete(segment1);
        maybeMarkAlignmentForDelete(segment1);

        segment1.join(segment2);

        //Move segments along to fill gaps. Will overwrite segment2
        for (int i=segment2.getSegmentNumber(); i<=size; i++) {
            Segment seg = segments.get(i);
            if (i==size()) {
                //Last item won't be overridden, so mark for delete before changing segment number
                markFilesForDelete(seg.getPossiblePaths());
                //Last index should be removed
                segments.remove(i);
            }
            //Keep segment number and do not overwrite segment1
            if (seg != segment2) {
                seg.setSegmentNumber(i - 1);
                segments.put(i - 1, seg);
            }
        }
        size--;
        assertCorrectOrdering();
        return segment1;
    }

    @Override
    public Iterator<Segment> iterator() {
        return new Iterator<Segment>() {
            int index = 1;
            @Override
            public boolean hasNext() {
                return index <= size;
            }

            @Override
            public Segment next() {
                index++;
                return getSegment(index-1);
            }
        };
    }
}
