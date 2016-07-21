package tat.view;

import file_system.Segment;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kalda on 21/07/2016.
 */
public class Selected {

    private Set<SelectedListener> selectedListeners = new HashSet<>();

    public void addSelectedListener(SelectedListener l) {
        selectedListeners.add(l);
    }

    public void setSelected(Segment segment, double frame) {
        for (SelectedListener l : selectedListeners) {
            l.selectionChanged(segment, frame);
        }
    }
}

interface SelectedListener {
    public void selectionChanged(Segment segment, double frame);
}
