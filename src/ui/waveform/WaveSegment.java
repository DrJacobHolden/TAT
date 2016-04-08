package ui.waveform;

import ui.AudioEditor;

/**
 * Created by Tate on 8/04/2016.
 */
public class WaveSegment {

    //The start time of this segment, null means the start of the audio file
    private SelectableWaveformPane.WaveformTime start;
    //The end time of this segment, null means the end of the audio file
    private SelectableWaveformPane.WaveformTime end;

    public WaveSegment(SelectableWaveformPane.WaveformTime start, SelectableWaveformPane.WaveformTime end) {
        this.start = start;
        this.end = end;
    }

    public boolean contains(SelectableWaveformPane.WaveformTime time) {
        if (start.getFrame() > time.getFrame()) return false;
        if (end.getFrame() < time.getFrame()) return false;
        return true;
    }

    public boolean equals(WaveSegment other) {
        if (start.getFrame() == other.start.getFrame() && end.getFrame() == other.end.getFrame())
            return true;
        return false;
    }
}
