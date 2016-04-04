package ui.waveform;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import ui.icon.Icon;
import ui.icon.IconLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Created by max on 29/03/16.
 * A waveform display that can be zoomed and scrolled
 */
public class ZoomableWaveformPane extends StackPane {

    protected final double ZOOM = 1.2;

    private double zoomLevel = 1;

    //For zoom centering
    private double scrollpaneViewportPercent;

    protected Pane waveformPane;
    protected WaveformImageView waveformImageView;
    protected ScrollPane waveformScrollPane;

    public ZoomableWaveformPane() {
        initialize(new WaveformImageView());
    }

    public ZoomableWaveformPane(WaveformImageView wf) {
        initialize(wf);
    }

    /**
     * Set up the scroll pane
     * @param wf The WaveformImageView this contains
     */
    protected void initialize(WaveformImageView wf) {
        waveformImageView = wf;
        //Allow clicking anywhere on the image
        waveformImageView.pickOnBoundsProperty().setValue(true);

        waveformPane = new Pane(wf);
        waveformScrollPane = new ScrollPane(waveformPane);
        //Never scroll vertically
        waveformScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        //Handle window resize
        waveformScrollPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //If the waveform image is smaller than the scrollpane width then fill scrollpane
                if (waveformImageView.getFitWidth() - waveformScrollPane.getWidth()  < 0.1)
                    resizeWaveform(1);
                else
                    //Otherwise update the zoom value
                    zoomLevel = waveformImageView.getFitWidth() / waveformScrollPane.getWidth();
            }
        });
        setupZoomCentering();

        addContents();

    }

    /**
     * Adds the waveformScrollPane and zoomButtons to this StackPane
     */
    protected void addContents() {
        ZoomButtons zoomButtons = new ZoomButtons(this);
        zoomButtons.setAlignment(Pos.TOP_RIGHT);
        getChildren().add(waveformScrollPane);
        getChildren().add(zoomButtons);
    }

    public void setAudioStream(File audioFile) throws IOException, UnsupportedAudioFileException {
        waveformImageView.setAudioStream(audioFile);
        imageChanged();
    }

    public void setAudioStream(AudioInputStream audioStream) throws UnsupportedAudioFileException {
        waveformImageView.setAudioStream(audioStream);
        imageChanged();
    }

    /**
     * Called when the waveform image is changed
     */
    protected void imageChanged() {
        waveformScrollPane.setFitToHeight(true);
    }

    public void zoomIn() {
        resizeWaveform(zoomLevel * ZOOM);
    }

    public void zoomOut() {
        if (waveformImageView.getFitWidth() / ZOOM >= getWidth())
            resizeWaveform((zoomLevel) / ZOOM);
        else
            //If you are close to zooming out fully then just zoom out fully
            resizeWaveform(1);
    }

    protected void resizeWaveform(double zoomLevel) {
        this.zoomLevel = zoomLevel;
        //For zoom centering
        scrollpaneViewportPercent = waveformScrollPane.getHvalue();
        waveformImageView.setFitWidth(waveformScrollPane.getWidth() * zoomLevel);
    }

    /**
     * Make sure zooming doesn't jump you to a random position in the waveform
     */
    private void setupZoomCentering() {
        waveformPane.widthProperty().addListener((ChangeListener) (observable, oldvalue, newValue) -> {
            waveformScrollPane.setHvalue(scrollpaneViewportPercent);
        });
    }

}
