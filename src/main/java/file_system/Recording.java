package file_system;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kalda on 28/06/2016.
 */
public class Recording {

    public Map<Integer, Segment> getSegments() {
        return segments;
    }

    private String baseName;
    private Map<Integer, Segment> segments = new HashMap<>();
    private int size = 0;

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
        for (Segment segment : segments.values()) {
            segment.save();
        }
    }
}
