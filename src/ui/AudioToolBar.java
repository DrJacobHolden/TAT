package ui;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ui.icon.Icon;
import ui.icon.IconLoader;

/**
 * Created by Tate on 5/04/2016.
 *
 * Represents the Toolbar containing the playback buttons, the menu buttons and the split/join options.
 */
public class AudioToolBar extends HBox {

    /**
     * The AudioEditor which can be controlled from this AudioToolbar
     */
    private AudioEditor audioEditor;

    public AudioToolBar(AudioEditor audioEditor) {
        this.audioEditor = audioEditor;
        addMenuControls();
        addPlayControls();
        addSplitControls();
    }

    //TODO: The menu controls section should be more OO
    private void addMenuControls() {
        this.getChildren().add(new ToolBar(new fileButton(), new saveButton(), new configButton(), new uploadButton()));
    }

    /**
     * Add controls for manipulating audio playback
     */
    private void addPlayControls() {
        ToolBar sep = new ToolBar();
        //TODO: Shouldn't be hardcoded
        sep.setMaxHeight(43);
        sep.setMinHeight(43);
        HBox.setHgrow(sep, Priority.ALWAYS);
        Button prevButton = new Button("", new Icon(IconLoader.getInstance().prevIcon));
        prevButton.setOnAction(event -> audioEditor.goToPrevSection());
        Button playButton = new Button("", new Icon(IconLoader.getInstance().playIcon));
        playButton.setOnAction(event -> audioEditor.play());
        Button pauseButton = new Button("", new Icon(IconLoader.getInstance().pauseIcon));
        pauseButton.setOnAction(event -> audioEditor.pause());
        Button stopButton = new Button("", new Icon(IconLoader.getInstance().stopIcon));
        stopButton.setOnAction(event -> audioEditor.stop());
        Button nextButton = new Button("", new Icon(IconLoader.getInstance().nextIcon));
        nextButton.setOnAction(event -> audioEditor.goToNextSection());
        ToolBar playControlToolbar = new ToolBar(
                prevButton,
                playButton,
                pauseButton,
                stopButton,
                nextButton
        );

        this.getChildren().addAll(sep, playControlToolbar);
    }

    /**
     * Add controls for splitting and joining audio
     */
    private void addSplitControls() {
        ToolBar sep = new ToolBar();
        //TODO: Shouldn't be hardcoded
        sep.setMaxHeight(43);
        sep.setMinHeight(43);
        Button splitButton = new Button("", new Icon(IconLoader.getInstance().splitIcon));
        Button joinButton = new Button("", new Icon(IconLoader.getInstance().joinIcon));
        ToolBar splitToolbar = new ToolBar(
                splitButton,
                joinButton
        );

        splitButton.setOnAction(e -> audioEditor.splitAudio());

        HBox.setHgrow(sep, Priority.ALWAYS);

        this.getChildren().addAll(sep, splitToolbar);
    }

    protected class fileButton extends Button {
        public fileButton() {
            super("", new Icon(IconLoader.getInstance().fileIcon));
        }
    }

    protected class saveButton extends Button {
        public saveButton() {
            super("", new Icon(IconLoader.getInstance().saveIcon));
        }
    }

    protected class configButton extends Button {
        public configButton() {
            super("", new Icon(IconLoader.getInstance().configIcon));
        }
    }

    protected class uploadButton extends Button {
        public uploadButton() {
            super("", new Icon(IconLoader.getInstance().uploadIcon));
        }
    }

}