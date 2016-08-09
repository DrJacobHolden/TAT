package tat.view;

import file_system.Recording;
import file_system.Segment;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import tat.Position;
import tat.PositionListener;
import ui.waveform.WaveformSegment;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tate on 4/07/2016.
 */
public class WaveformDisplay extends ScrollPane implements PositionListener {

    public Recording recording;
    private List<WaveformSegment> imageViews = new ArrayList<>();
    Scale scale = new Scale();

    private HBox hBox = new HBox();
    private Group cursorGroup = new Group();

    Group group = new Group();

    private double zoomFactor = 1;

    protected Rectangle cursor = new Rectangle();

    private Position position;

    public void setPosition(Position position) {
        this.position = position;
        position.addSelectedListener(this);
    }

    public WaveformDisplay() {
        super();
        setFitToWidth(true);

        cursorGroup.getChildren().add(hBox);
        group.getChildren().add(cursorGroup);
        setContent(group);

        //Never show vertical scroll bar
        setVbarPolicy(ScrollBarPolicy.NEVER);
        setHbarPolicy(ScrollBarPolicy.ALWAYS);

        cursorGroup.getTransforms().add(scale);

        //Doesn't work on maximise :(
        heightProperty().addListener(observable -> {
            resizeHeight();
        });

        widthProperty().addListener(observable -> {
            setZoomFactor(1);
        });

        //Cursor
        cursorGroup.getChildren().add(cursor);
        cursor.setFill(Colours.WHITE);
        //Use exact sizes specified
        cursor.setStrokeWidth(0);
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public void setZoomFactor(double newValue) {
        if (newValue < 1) {
            newValue = 1;
        }
        zoomFactor = newValue;

        double newScale = newValue * getInternalWidth()/getImageWidth();
        scale.setX(newScale);
        updateCursorWidth();
        setHvalue(cursor.getX()/getImageWidth());
    }

    private double getInternalWidth() {
        return getWidth()-getInsets().getLeft()-getInsets().getRight();
    }

    private double getInternalHeight() {
        //This is not set when we start, so it was easier to just hardcode it
        double scrollbarHeight = 20;
        return getHeight()-getInsets().getBottom()-getInsets().getTop()-scrollbarHeight;
    }

    private void resizeHeight() {
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
        for (Segment segment : recording) {
            try {
                addWaveform(new WaveformSegment(segment));
            } catch (UnsupportedAudioFileException | IOException e) {
                e.printStackTrace();
            }
        }

        resizeHeight();
        resetColours();
        updateCursorPosition(0);
    }

    public void addWaveform(WaveformSegment iv) {
        int index = iv.getSegment().getSegmentNumber()-1;
        iv.setOnMouseClicked(e -> segmentClicked(iv, e));
        hBox.getChildren().add(index, iv);
        imageViews.add(index, iv);
    }

    public void removeWaveform(WaveformSegment iv) {
        hBox.getChildren().remove(iv);
        imageViews.remove(iv);
    }

    private void segmentClicked(WaveformSegment iv, MouseEvent ev) {
        position.setSelected(iv.getSegment(), iv.getFrameForPosition(ev.getX()), this);
    }

    private void updateCursorPosition(double newX) {
        cursor.setX(newX);
        cursor.setY(0);
        cursor.setHeight(getImageHeight());
    }

    private void updateCursorWidth() {
        cursor.setWidth(2/scale.getX());
    }

    public void resetColours() {
        for (int i=0; i<imageViews.size(); i++) {
            if ((i+1)%2==0) {
                imageViews.get(i).setColourEven();
            } else {
                imageViews.get(i).setColourOdd();
            }
        }
    }

    private WaveformSegment imageViewForSegment(Segment segment) {
        for (WaveformSegment iv : imageViews) {
            if (iv.getSegment() == segment) {
                return iv;
            }
        }
        return null;
    }

    public void onSplit(Segment segment1, Segment segment2, int splitFrame) {
        WaveformSegment splitWaveformSegment = imageViews.get(segment1.getSegmentNumber()-1);
        WaveformSegment newWaveform = splitWaveformSegment.split(segment2, splitFrame);
        //Add after segment1
        addWaveform(newWaveform);
        //Select will be called after new waveform is selected in EditorMenuController
        resetColours();
    }

    public void onJoin(Segment segment1, Segment segment2) {
        WaveformSegment waveform1 = imageViews.get(segment1.getSegmentNumber()-1);
        WaveformSegment waveform2 = imageViews.get(segment2.getSegmentNumber()-1);
        waveform1.join(waveform2);
        removeWaveform(waveform2);
        //Select will be called after waveform is selected in EditorMenuController
        resetColours();
    }

    @Override
    public void positionChanged(Segment segment, int frame, Object initiator) {
        WaveformSegment iv = imageViewForSegment(segment);
        resetColours();
        iv.setColourSelected();

        if (iv != null) {
            updateCursorPosition(iv.getBoundsInParent().getMinX() + iv.getPositionForFrame(frame));
        }
    }
}
