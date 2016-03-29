package sample;

import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Created by max on 29/03/16.
 */
public class AudioEditor extends SelectableWaveformPane {

    //The total length of the displayed waveform
    //TODO: FIX
    final double TRACK_LENGTH = 60;
    //TODO: FIX
    double secondsPerPixel = TRACK_LENGTH / 1024;

    Slider progressSlider1;
    Slider progressSlider2;

    public AudioEditor() {
        super();
    }

    protected void addWaveformClickListeners() {
        waveformScrollPane.setOnMousePressed(
                mouseEvent -> progressSlider1.setValue(mouseEvent.getSceneX()*secondsPerPixel)
        );

        waveformScrollPane.setOnMouseReleased(
                mouseEvent -> progressSlider2.setValue(mouseEvent.getSceneX()*secondsPerPixel)
        );
    }

    @Override
    protected void addContents() {
        super.addContents();
        addSliders();
    }

    protected void addSliders() {
        progressSlider1 = new Slider();
        progressSlider2 = new Slider();

        progressSlider1.setMax(TRACK_LENGTH);
        progressSlider2.setMax(TRACK_LENGTH);
        getChildren().add(progressSlider1);
        getChildren().add(progressSlider2);
    }

    @Override
    protected void addToolbars() {
        super.addToolbars();
        addPlayControlToolbar();
        addSplitToolbar();
    }

    private void addPlayControlToolbar() {
        Separator sep = new Separator();
        HBox.setHgrow(sep, Priority.ALWAYS);
        ToolBar playControlToolbar = new ToolBar(
                new Button("|>"),
                new Button("||"),
                new Button("[]")
        );
        toolbars.getChildren().addAll(sep, playControlToolbar);
    }

    private void addSplitToolbar() {
        Separator sep = new Separator();
        ToolBar splitToolbar = new ToolBar(
                new Button("Split"),
                new Button("Join")
        );
        HBox.setHgrow(sep, Priority.ALWAYS);
        toolbars.getChildren().addAll(sep, splitToolbar);
    }

    @Override
    public void zoomIn() {
        super.zoomIn();
        secondsPerPixel = secondsPerPixel * 0.80;
        updateSliders();
    }

    @Override
    public void zoomOut() {
        super.zoomOut();
        secondsPerPixel = secondsPerPixel / 0.80;
        updateSliders();
    }

    protected void updateSliders() {
        progressSlider1.setValue(0);
        progressSlider2.setValue(0);
        progressSlider1.setMax(secondsPerPixel*getWidth());
        progressSlider2.setMax(secondsPerPixel*getWidth());
    }
}
