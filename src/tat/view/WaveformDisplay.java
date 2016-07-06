package tat.view;

import file_system.Recording;
import file_system.Segment;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.transform.Scale;
import ui.waveform.WaveformImageView;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tate on 4/07/2016.
 */
public class WaveformDisplay extends ScrollPane {

    public Recording recording;
    private List<WaveformImageView> imageViews = new ArrayList<>();
    Scale scale = new Scale();
    private HBox hBox = new HBox();

    Group group = new Group();

    private DoubleProperty zoomFactor = new SimpleDoubleProperty(1);

    public double getZoomFactor() {
        return zoomFactor.getValue();
    }

    public void setZoomFactor(double newValue) {
        if (newValue < 1) {
            newValue = 1;
        }
        zoomFactor.setValue(newValue);
    }

    public WaveformDisplay() {
        super();
        setFitToWidth(true);

        group.getChildren().add(hBox);
        setContent(group);

        hBox.getTransforms().add(scale);
        zoomFactor.addListener((observable, oldValue, newValue) -> {
            scale.setX(((double) newValue) * (getWidth()/getImageWidth()));
        });
    }

    private double getImageWidth() {
        double width = 0;
        for (WaveformImageView iv : imageViews) {
            width += iv.getImage().getWidth();
        }
        return width;
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
    }

    public void drawWaveform() {
        for (Segment segment : recording.getSegments().values()) {
            WaveformImageView iv = new WaveformImageView();
            try {
                iv.setAudioStream(segment.getAudioFile().getStream());
                hBox.getChildren().add(iv);
                imageViews.add(iv);
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }
    }
}
