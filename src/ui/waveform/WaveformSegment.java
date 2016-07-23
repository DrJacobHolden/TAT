package ui.waveform;

import file_system.Segment;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import tat.view.Colours;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Created by kalda on 7/07/2016.
 */
public class WaveformSegment extends StackPane {
    private final WaveformImageView imageView = new WaveformImageView();

    private final Background SELECTED_BACKGROUND = new Background(new BackgroundFill(Colours.ORANGE, CornerRadii.EMPTY, Insets.EMPTY));
    private final Background ODD_BACKGROUND = new Background(new BackgroundFill(Colours.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
    private final Background EVEN_BACKGROUND = new Background(new BackgroundFill(Colours.TERTIARY_GRAY, CornerRadii.EMPTY, Insets.EMPTY));
    private final Segment segment;

    public double getImageWidth() {
        return imageView.getImage().getWidth();
    }

    public double getImageHeight() { return imageView.getImage().getHeight(); }

    public WaveformSegment(Segment segment) throws IOException, UnsupportedAudioFileException {
        this.segment = segment;
        imageView.setAudioStream(segment.getAudioFile().getFile());
        getChildren().add(imageView);
    }

    public Segment getSegment() {
        return segment;
    }

    public void setColourSelected() {
        setBackground(SELECTED_BACKGROUND);
    }

    public void setColourOdd() {
        setBackground(ODD_BACKGROUND);
    }

    public void setColourEven() {
        setBackground(EVEN_BACKGROUND);
    }
}
