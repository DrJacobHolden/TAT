package file_system;

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
        //Split the segment
        Segment segment2 = segment1.split(frame, stringPos);

        //Move segments along to make room for new one
        for (int i=segment1.getSegmentNumber()+1; i<=size; i++) {
            Segment seg = segments.get(i);
            seg.setSegmentNumber(i+1);
            segments.put(i+1, seg);
        }

        //Put segment after first
        segments.put(segment1.getSegmentNumber()+1, segment2);
        size++;
        return segment2;
    }

    //Needs to be called whenever a file has an attribute change, or on a join
    private void markFilesForDelete(List<Path> paths) {
        toDeleteOnSave.addAll(paths);
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
        segment1.join(segment2);

        //Move segments along to fill gaps. Will overwrite segment2
        for (int i=segment2.getSegmentNumber()+1; i<=size; i++) {
            Segment seg = segments.get(i);
            if (i==size()) {
                //Last item won't be overridden, so mark for delete before changing segment number
                markFilesForDelete(seg.getPossiblePaths());
                //Last index should be removed
                segments.remove(i);
            }
            seg.setSegmentNumber(i-1);
            segments.put(i-1, seg);
        }
        size--;
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
