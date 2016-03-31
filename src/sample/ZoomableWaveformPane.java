package sample;

import icon.Icon;
import icon.IconLoader;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
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

    private double zoomLevel = 1;

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

        addContents();
        //TODO: Fix
        waveformImageView.setFitWidth(1024);
    }

    /**
     * Adds the toolbar for zooming in and out of the image
     */
    protected void addZoomToolbar() {
        Button zoomIn = new ZoomInButton("", new Icon(IconLoader.getInstance().zoomInIcon));
        Button zoomOut = new ZoomOutButton("", new Icon(IconLoader.getInstance().zoomOutIcon));

        zoomToolbar.getItems().addAll(zoomIn, zoomOut);
        toolbars.getChildren().add(zoomToolbar);
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
        waveformImageView.setFitWidth(waveformScrollPane.getWidth() * zoomLevel);
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
        ZoomInButton(String text, ImageView iv1) {
            super(text, iv1);
        }
        {
            setOnAction(event -> zoomIn());
        }
    }

    protected class ZoomOutButton extends Button {
        ZoomOutButton(String text, ImageView iv1) {
            super(text, iv1);
        }
        {
            setOnAction(event -> zoomOut());
        }
    }
}
