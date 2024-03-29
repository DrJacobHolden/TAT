package tat.corpus;

import tat.corpus.file.AlignmentFile;

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

    private boolean saveIsUpToDate = true;
    private String baseName;
    private Map<Integer, Segment> segments = new HashMap<>();
    private int size = 0;
    private List<Path> toDeleteOnSave = new ArrayList<>();

    public Recording(String baseName) {
        this.baseName = baseName;
    }

    public static Map<String, Recording> groupSegments(List<Segment> segments) {
        TreeMap<String, Recording> recordingMap = new TreeMap<>();

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

    public void invalidateSave() {
        saveIsUpToDate = false;
    }

    public boolean saveIsUpToDate() {
        return saveIsUpToDate;
    }

    public Map<Integer, Segment> getSegments() {
        return segments;
    }

    public int size() {
        return size;
    }

    public String getBaseName() {
        return baseName;
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
        for (int i = 0; i < size; i++) {
            if (segments.get(i) == null) {
                return true;
            }
        }
        return false;
    }

    public void save() throws IOException {
        //Delete files first, so that they can be saved
        deleteMarked();
        for (Segment segment : this) {
            segment.save();
        }
        saveIsUpToDate = true;
    }

    public Segment split(Segment segment1, long frame, int stringPos) throws IOException {
        assertCorrectOrdering();

        //Alignment is no longer valid
        maybeMarkAlignmentForDelete(segment1);

        //Split the segment
        Segment segment2 = segment1.split(frame, stringPos);

        //Move segments along to make room for new one
        for (int i = size; i > segment1.getSegmentNumber(); i--) {
            Segment seg = segments.get(i);
            seg.setSegmentNumber(i + 1);
            segments.put(i + 1, seg);
        }

        //Put segment after first
        segments.put(segment2.getSegmentNumber(), segment2);
        size++;
        assertCorrectOrdering();
        invalidateSave();
        return segment2;
    }

    private void assertCorrectOrdering() {
        int i = 1;
        for (Segment segment : this) {
            if (segment == null) {
                throw new IllegalStateException("Segment at position " + i + " is null");
            } else if (segment.getSegmentNumber() != i++) {
                throw new IllegalStateException("Segment " + segment.toString() + " has number " + segment.getSegmentNumber()
                        + " when it is in position " + (i - 1));
            }
        }
    }

    //Needs to be called whenever a file has an attribute change, or on a join
    protected void markFilesForDelete(List<Path> paths) {
        toDeleteOnSave.addAll(paths);
    }

    private void maybeMarkAlignmentForDelete(Segment segment) {
        AlignmentFile alignment = segment.getAlignmentFile();
        try {
            File file = alignment.getFileForPath(segment.getPath(alignment));
            toDeleteOnSave.add(file.toPath());
        } catch (FileNotFoundException e) {
            //No worries, tat.alignment doesn't exist
        }
    }

    private void deleteMarked() {
        while (toDeleteOnSave.size() > 0) {
            Path path = toDeleteOnSave.remove(toDeleteOnSave.size() - 1);
            try {
                Files.delete(path);
            } catch (IOException e) {
            }
        }
    }

    public Segment join(Segment segment1, Segment segment2) throws IOException {
        assertCorrectOrdering();

        //Alignment is no longer valid
        maybeMarkAlignmentForDelete(segment1);
        maybeMarkAlignmentForDelete(segment2);

        segment1.join(segment2);
        removeSegment(segment2);
        return segment1;
    }

    public void removeSegment(Segment toRemove) {
        //Move segments along to fill gaps. Will overwrite segment2
        for (int i = toRemove.getSegmentNumber(); i <= size; i++) {
            Segment seg = segments.get(i);
            if (i == size()) {
                //Last item won't be overridden, so mark for delete before changing segment number
                markFilesForDelete(seg.getPossiblePaths());
                //Last index should be removed
                segments.remove(i);
            }
            //Keep segment number and do not overwrite segment1
            if (seg != toRemove) {
                seg.setSegmentNumber(i - 1);
                segments.put(i - 1, seg);
            }
        }
        size--;
        assertCorrectOrdering();
        invalidateSave();
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
                return getSegment(index - 1);
            }
        };
    }

    public boolean hasNoAnnotation() {
        boolean hasAnnotation = false;
        for (Segment seg : this) {
            hasAnnotation = hasAnnotation || !seg.getAnnotationFile().isEmpty();
        }
        return !hasAnnotation;
    }
}
