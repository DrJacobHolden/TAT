package sample;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Created by max on 29/03/16.
 * A waveform display that can be zoomed and scrolled
 */
public class ZoomableWaveformPane extends VBox {

    protected final double ZOOM = 1.2;

    protected Pane waveformPane;
    protected WaveformImageView waveformImageView;
    protected ScrollPane waveformScrollPane;

    protected HBox toolbars = new HBox();
    protected ToolBar zoomToolbar = new ToolBar();

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
        addContents();
        //TODO: Fix
        waveformImageView.setFitWidth(1024);
    }

    /**
     * Adds the toolbar for zooming in and out of the image
     */
    protected void addZoomToolbar() {
        Button zoomIn = new ZoomInButton();
        Button zoomOut = new ZoomOutButton();

        zoomIn.setText("+");
        zoomOut.setText("-");

        zoomToolbar.getItems().addAll(zoomIn, zoomOut);
        toolbars.getChildren().add(zoomToolbar);
    }

    public void zoomIn() {
        waveformImageView.setFitWidth(waveformImageView.getFitWidth() * ZOOM);
    }

    public void zoomOut() {
        if (waveformImageView.getFitWidth() / ZOOM >= getWidth()) {
            waveformImageView.setFitWidth(waveformImageView.getFitWidth() / ZOOM);
        }
    }

    /**
     * Add the contents to this HBox
     */
    protected void addContents() {
        getChildren().add(waveformScrollPane);
        getChildren().add(toolbars);
        addToolbars();
    }

    protected void addToolbars() {
        addZoomToolbar();
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
        waveformImageView.setFitHeight(200);
        waveformScrollPane.setPrefViewportHeight(waveformImageView.getFitHeight());
    }

    protected class ZoomInButton extends Button {
        {
            setOnAction(event -> zoomIn());
        }
    }

    protected class ZoomOutButton extends Button {
        {
            setOnAction(event -> zoomOut());
        }
    }
}
