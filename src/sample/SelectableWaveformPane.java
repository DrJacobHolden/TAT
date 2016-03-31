package sample;

import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 29/03/16.
 *
 * This class represents a GUI selectable, zoomable waveform for an audio file.
 */
public class SelectableWaveformPane extends ZoomableWaveformPane {

    protected WaveformTime cursorPosition = new WaveformTime();
    protected Line cursorLine;

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

        //Clickline can't be added until after waveform
        if (cursorLine == null) {
            addCursorLine();
        }
    }

    /**
     * Adds the cursor line to the waveform scroll pane
     */
    protected void addCursorLine() {
        cursorLine = new Line();
        waveformPane.getChildren().add(cursorLine);

        cursorLine.setStartX(0);
        cursorLine.setEndX(0);
        cursorLine.setStartY(1);
        cursorLine.setEndY(waveformImageView.getFitHeight());

        //Update the line whenever the time is changed
        cursorPosition.addChangeListener(new WaveformTimeListener() {
            @Override
            public void onChange(WaveformTime time) {
                updateCursorLine();
            }
        });
    }

    protected void updateCursorLine(){
        cursorLine.setStartX(waveformImageView.getFitWidth() * cursorPosition.getPercent());
        cursorLine.setEndX(waveformImageView.getFitWidth() * cursorPosition.getPercent());
    }

    @Override
    protected void resizeWaveform(double zoom) {
        super.resizeWaveform(zoom);
        //Update cursor position with resize
        if (cursorLine != null)
            updateCursorLine();
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
    protected class WaveformTime {
        long frame = 0;
        double percent = 0;

        protected List<WaveformTimeListener> changeListeners = new ArrayList<>();

        public double getPercent() {
            return percent;
        }

        public long getFrame() {
            return frame;
        }

        public void setFrame(int frame) throws IllegalArgumentException {
            long length = waveformImageView.getAudioStream().getFrameLength();
            if (frame >= length) {
                throw new IllegalArgumentException();
            } else {
                this.frame = frame;
                percent = frame/((double) length);
            }
            notifyChange();
        }

        public void setPercent(double percent) throws  IllegalArgumentException {
            if (percent > 1) {
                throw new IllegalArgumentException();
            } else {
                this.percent = percent;
                this.frame = (long) (percent * (double) waveformImageView.getAudioStream().getFrameLength());
            }
            notifyChange();
        }

        protected void notifyChange() {
            for (WaveformTimeListener listener : changeListeners) {
                listener.onChange(this);
            }
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
