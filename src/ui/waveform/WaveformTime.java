package ui.waveform;

import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a time position within a waveform.
 */
public class WaveformTime extends Line implements Comparable<WaveformTime> {
    private SelectableWaveformPane selectableWaveformPane;
    long frame = 0;
    double percent = 0;

    public WaveformTime(SelectableWaveformPane selectableWaveformPane) {
        this.selectableWaveformPane = selectableWaveformPane;
        setStartX(0);
        setEndX(0);
        setStartY(1);
    }

    protected List<SelectableWaveformPane.WaveformTimeListener> changeListeners = new ArrayList<>();

    public double getPercent() {
        return percent;
    }

    public long getFrame() {
        return frame;
    }

    public void setFrame(long frame) throws IllegalArgumentException {
        setFrame(frame, true);
    }

    public void setFrame(long frame, boolean shouldNotify) throws IllegalArgumentException {
        long length = selectableWaveformPane.waveformImageView.getAudioStream().getFrameLength();
        if (frame > length) {
            throw new IllegalArgumentException();
        } else {
            this.frame = frame;
            percent = frame/((double) length);
        }
        updateGui();
        if(shouldNotify)
            notifyChange();
    }

    public void setPercent(double percent) throws  IllegalArgumentException {
        if (percent > 1) {
            throw new IllegalArgumentException();
        } else {
            this.percent = percent;
            this.frame = (long) (percent * (double) selectableWaveformPane.waveformImageView.getAudioStream().getFrameLength());
        }
        updateGui();
        notifyChange();
    }

    protected void notifyChange() {
        for (SelectableWaveformPane.WaveformTimeListener listener : changeListeners) {
            listener.onChange(this);
        }
    }

    protected void updateGui() {
        setEndY(selectableWaveformPane.waveformPane.getHeight());
        setStartX(selectableWaveformPane.waveformImageView.getFitWidth() * percent);
        setEndX(selectableWaveformPane.waveformImageView.getFitWidth() * percent);
    }

    public void addChangeListener(SelectableWaveformPane.WaveformTimeListener listener) {
        changeListeners.add(listener);
    }

    public String toString() {
        return "" + percent + " Frame " + frame + "/" + selectableWaveformPane.waveformImageView.getAudioStream().getFrameLength();
    }

    @Override
    public int compareTo(WaveformTime o) {
        if (this.getFrame() > o.getFrame())
            return 1;
        return -1;
    }
}
