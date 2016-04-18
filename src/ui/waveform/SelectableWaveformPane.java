package ui.waveform;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 29/03/16.
 *
 * This class represents a GUI selectable, zoomable waveform for an audio file.
 */
public class SelectableWaveformPane extends ZoomableWaveformPane {

    protected WaveformTime cursorPosition = new WaveformTime(this){{
        setStroke(Color.MAGENTA);
    }};

    /**
     * Used purely for GUI updates.
     */
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
        //Respond to mouse click events
        waveformImageView.setOnMouseClicked(event -> clicked(event));
    }

    protected void imageChanged() {
        super.imageChanged();
        //Add after the image has been added
        addWaveformTime(cursorPosition);
    }

    /**
     * Adds a line to the waveform scroll pane
     */
    public void addWaveformTime(WaveformTime w) {
        waveformPane.getChildren().add(w);
        waveformTimes.add(w);
    }

    public void removeWaveformTime(WaveformTime w) {
        waveformPane.getChildren().remove(w);
        waveformTimes.remove(w);
    }

    @Override
    protected void resizeWaveform(double zoom) {
        super.resizeWaveform(zoom);
        //Move grid lines to the correct positions when resizing
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

    public interface WaveformTimeListener {
        void onChange(WaveformTime time);
    }
}
