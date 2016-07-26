package tat;

import file_system.Segment;

/**
 * Created by kalda on 23/07/2016.
 */
public interface PositionListener {
    void positionChanged(Segment segment, int frame, Object initiator);
}
