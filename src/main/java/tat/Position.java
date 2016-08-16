package tat;

import file_system.Segment;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kalda on 21/07/2016.
 */
public class Position {

    private Set<PositionListener> positionListeners = new HashSet<>();

    private Segment segment;
    private int frame;

    public void addSelectedListener(PositionListener l) {
        positionListeners.add(l);
    }

    public Segment getSegment() {
        return segment;
    }

    public int getFrame() {
        return frame;
    }

    public void setSelected(Segment segment, int frame, Object initiator) {
        if (segment != this.segment || frame != this.frame) {
            this.segment = segment;
            this.frame = frame;
            System.out.println("Selected segment: " + segment.getSegmentNumber() + " frame: " + frame + " from " + initiator);
            positionListeners.stream().forEach(l -> l.positionChanged(segment, frame, initiator));
        }
    }
}

