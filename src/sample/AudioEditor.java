package sample;

import icon.Icon;
import icon.IconLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 29/03/16.
 * GUI for splitting, joining and playing back audio
 */
public class AudioEditor extends SelectableWaveformPane {

    private List<WaveformTime> splitTimes = new ArrayList<>();

    public AudioEditor() {
        super();
    }

    @Override
    protected void addToolbars() {
        super.addToolbars();
        addPlayControlToolbar();
        addSplitToolbar();
    }

    /**
     * Add controls for manipulating audio playback
     */
    private void addPlayControlToolbar() {
        ToolBar sep = new ToolBar();
        sep.setMaxHeight(43);
        sep.setMinHeight(43);
        HBox.setHgrow(sep, Priority.ALWAYS);
        Button prevButton = new Button("", new Icon(IconLoader.getInstance().prevIcon));
        Button playButton = new Button("", new Icon(IconLoader.getInstance().playIcon));
        Button pauseButton = new Button("", new Icon(IconLoader.getInstance().pauseIcon));
        Button stopButton = new Button("", new Icon(IconLoader.getInstance().stopIcon));
        Button nextButton = new Button("", new Icon(IconLoader.getInstance().nextIcon));
        ToolBar playControlToolbar = new ToolBar(
                prevButton,
                playButton,
                pauseButton,
                stopButton,
                nextButton
        );
        toolbars.getChildren().addAll(sep, playControlToolbar);
    }

    private void splitAudio() {
        WaveformTime split = new WaveformTime();
        split.setFrame(cursorPosition.getFrame());
        addWaveformTime(split);
    }

    /**
     * Add controls for splitting and joining audio
     */
    private void addSplitToolbar() {
        ToolBar sep = new ToolBar();
        sep.setMaxHeight(43);
        sep.setMinHeight(43);
        Button splitButton = new Button("", new Icon(IconLoader.getInstance().splitIcon));
        Button joinButton = new Button("", new Icon(IconLoader.getInstance().joinIcon));
        ToolBar splitToolbar = new ToolBar(
                splitButton,
                joinButton
        );

        splitButton.setOnAction(e -> splitAudio());

        HBox.setHgrow(sep, Priority.ALWAYS);
        toolbars.getChildren().addAll(sep, splitToolbar);
    }
}
