package sample;

import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Created by max on 29/03/16.
 * GUI for splitting, joining and playing back audio
 */
public class AudioEditor extends SelectableWaveformPane {

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
        Separator sep = new Separator();
        HBox.setHgrow(sep, Priority.ALWAYS);
        ToolBar playControlToolbar = new ToolBar(
                new Button("|>"),
                new Button("||"),
                new Button("[]")
        );
        toolbars.getChildren().addAll(sep, playControlToolbar);
    }

    /**
     * Add controls for splitting and joining audio
     */
    private void addSplitToolbar() {
        Separator sep = new Separator();
        ToolBar splitToolbar = new ToolBar(
                new Button("Split"),
                new Button("Join")
        );
        HBox.setHgrow(sep, Priority.ALWAYS);
        toolbars.getChildren().addAll(sep, splitToolbar);
    }
}
