package ui.waveform;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 29/03/16.
 *
 * This class represents a GUI selectable, zoomable waveform for an audio file.
 */
public class SelectableWaveformPane extends ZoomableWaveformPane {

    protected WaveformTime cursorPosition = new WaveformTime(){{
        setStroke(Color.MAGENTA);
    }};
    protected List<WaveformTime> waveformTimes = new ArrayList<>();

    /**
     * Gets the position of the waveform cursor. This WaveformTime object can be modified and the GUI will update
     * automatically.
     * @return The cursor position
     */
    public WaveformTime getCursorPosition() {
        return cursorPosition;
    }

    @Override
    protected void initialize(WaveformImageView wf) {
        super.initialize(wf);
        waveformImageView.setOnMouseClicked(event -> clicked(event));
    }

    protected void imageChanged() {
        super.imageChanged();
        addWaveformTime(cursorPosition);
    }

    /**
     * Adds a line to the waveform scroll pane
     */
    protected void addWaveformTime(WaveformTime w) {
        waveformPane.getChildren().add(w);
        waveformTimes.add(w);
    }

    @Override
    protected void resizeWaveform(double zoom) {
        super.resizeWaveform(zoom);
        for (WaveformTime waveformTime : waveformTimes){
            waveformTime.updateGui();
        }
    }

    protected void clicked(MouseEvent event) {
        event.getSource();

        //Update cursor position
        double percent = event.getX() / (waveformImageView.getFitWidth());
        cursorPosition.setPercent(percent);

        System.out.println(cursorPosition);
    }

    /**
     * Represents a time position  within a waveform
     */
    protected class WaveformTime extends Line {
        long frame = 0;
        double percent = 0;

        public WaveformTime() {
            setStartX(0);
            setEndX(0);
            setStartY(1);
        }

        protected List<WaveformTimeListener> changeListeners = new ArrayList<>();

        public double getPercent() {
            return percent;
        }

        public long getFrame() {
            return frame;
        }

        public void setFrame(long frame) throws IllegalArgumentException {
            long length = waveformImageView.getAudioStream().getFrameLength();
            if (frame >= length) {
                throw new IllegalArgumentException();
            } else {
                this.frame = frame;
                percent = frame/((double) length);
            }
            updateGui();
            notifyChange();
        }

        public void setPercent(double percent) throws  IllegalArgumentException {
            if (percent > 1) {
                throw new IllegalArgumentException();
            } else {
                this.percent = percent;
                this.frame = (long) (percent * (double) waveformImageView.getAudioStream().getFrameLength());
            }
            updateGui();
            notifyChange();
        }

        protected void notifyChange() {
            for (WaveformTimeListener listener : changeListeners) {
                listener.onChange(this);
            }
        }

        protected void updateGui() {
            setEndY(waveformPane.getHeight());
            setStartX(waveformImageView.getFitWidth() * percent);
            setEndX(waveformImageView.getFitWidth() * percent);
        }

        public void addChangeListener(WaveformTimeListener listener) {
            changeListeners.add(listener);
        }

        public String toString() {
            return "" + percent + " Frame " + frame + "/" + waveformImageView.getAudioStream().getFrameLength();
        }
    }

    public abstract class WaveformTimeListener {
        public abstract void onChange(WaveformTime time);
    }
}
