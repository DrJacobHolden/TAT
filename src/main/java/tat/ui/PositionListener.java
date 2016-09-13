package tat.ui;

import tat.corpus.Segment;

/**
 * Created by kalda on 23/07/2016.
 */
public interface PositionListener {
    void positionChanged(Segment segment, int frame, Object initiator);
}
