package tat;

import file_system.Segment;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kalda on 21/07/2016.
 */
public class Position {

    private Set<PositionListener> positionListeners = new HashSet<>();

    public void addSelectedListener(PositionListener l) {
        positionListeners.add(l);
    }

    public void setSelected(Segment segment, double frame, Object initiator) {
        positionListeners.stream().forEach(l -> {
            l.positionChanged(segment, frame, initiator);
        });
    }
}

