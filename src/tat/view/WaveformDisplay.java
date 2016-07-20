package tat.view;

import file_system.Recording;
import file_system.Segment;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.transform.Scale;
import ui.waveform.WaveformSegment;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tate on 4/07/2016.
 */
public class WaveformDisplay extends ScrollPane {

    public Recording recording;
    private List<WaveformSegment> imageViews = new ArrayList<>();
    Scale scale = new Scale();
    private HBox hBox = new HBox();

    Group group = new Group();

    private double zoomFactor = 1;

    public double getZoomFactor() {
        return zoomFactor;
    }

    public void setZoomFactor(double newValue) {
        if (newValue < 1) {
            newValue = 1;
        }
        zoomFactor = newValue;

        //This works better than nothing
        //TODO: Make work better still

        System.out.println("internal width " + getInternalWidth());
        System.out.println("image width " + getImageWidth());
        System.out.println("new val " + newValue);

        double newScale = (double) newValue * getInternalWidth()/getImageWidth();

        double oldHVal = getHvalue();
        double innerWidth = hBox.getWidth() * newScale;
        Bounds viewport = getViewportBounds();

       double viewpointMid = -1*viewport.getMinX() + getWidth();

        double perc = viewpointMid/innerWidth;
        System.out.println(viewport.getMinX());
        System.out.println(innerWidth);

        scale.setX(newScale);

        setHvalue(oldHVal);
    }

    public WaveformDisplay() {
        super();
        setFitToWidth(true);

        group.getChildren().add(hBox);
        setContent(group);

        //Never show vertical scroll bar
        setVbarPolicy(ScrollBarPolicy.NEVER);
        setHbarPolicy(ScrollBarPolicy.ALWAYS);

        hBox.getTransforms().add(scale);

        //Doesn't work on maximise :(
        heightProperty().addListener(observable -> {
            resizeHeight();
        });

        //Doesnt't work
        widthProperty().addListener(observable -> {
            //TODO: Make more elegant
            setZoomFactor(1);
        });
    }

    private double getInternalWidth() {
        System.out.println("getWidth " + getWidth());
        return getWidth()-getInsets().getLeft()-getInsets().getRight();
    }

    private double getInternalHeight() {
        return getHeight()-getInsets().getBottom()-getInsets().getTop()-13;
    }

    private void resizeHeight() {
        //Scroll bar is 13 pixels tall
        //TODO: Obtain dynamically
        scale.setY(getInternalHeight()/getImageHeight());
    }

    private double getImageWidth() {
        double width = 0;
        for (WaveformSegment iv : imageViews) {
            width += iv.getImageWidth();
        }
        return width;
    }

    private double getImageHeight() {
        if (imageViews.size() < 0) {
            return 1;
        } else {
            return imageViews.get(0).getImageHeight();
        }
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
    }

    public void drawWaveform() {
        for (Segment segment : recording.getSegments().values()) {
            WaveformSegment iv = new WaveformSegment();
            try {
                iv.setAudioStream(segment.getAudioFile().getFile());
                hBox.getChildren().add(iv);
                imageViews.add(iv);
            } catch (UnsupportedAudioFileException | IOException e) {
                e.printStackTrace();
            }
        }

        //TODO: Remove
        imageViews.get(0).setColourSelected();
        resizeHeight();
        setColours();
    }

    public void setColours() {
        for (int i=0; i<imageViews.size(); i++) {
            if ((i+1)%2==0) {
                imageViews.get(i).setColourEven();
            } else {
                imageViews.get(i).setColourOdd();
            }
        }
    }
}
