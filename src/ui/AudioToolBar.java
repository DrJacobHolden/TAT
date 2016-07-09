package ui;

import alignment.maus.WebMaus;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import tat.GlobalConfiguration;
import ui.icon.Icon;
import ui.icon.IconLoader;
import ui.text_box.AnnotationArea;

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

    private AnnotationArea annotationArea;

    public AudioToolBar(AudioEditor audioEditor, AnnotationArea annotationArea) {
        this.audioEditor = audioEditor;
        this.annotationArea = annotationArea;
        addMenuControls();
        addPlayControls();
        addSplitControls();
    }

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
            super("", new Icon(IconLoader.getInstance().mainFileIcon));
        }
    }

    protected class saveButton extends Button {
        public saveButton() {
            super("", new Icon(IconLoader.getInstance().saveIcon));
        }
    }

    protected class configButton extends Button {
        public configButton() {
            super("", new Icon(IconLoader.getInstance().settingsIcon));
        }
    }

    protected class uploadButton extends Button {
        public uploadButton() {
            super("", new Icon(IconLoader.getInstance().alignIcon));
            setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    //TODO: Make perform for list of files and transcriptions
                    try {
                        new WebMaus().generateAlignment(annotationArea.getSegments().get(0).getText(), audioEditor.getAudioSegments().get(0));
                    } catch(Exception a) {
                        a.printStackTrace();
                    }
                }
            });
        }
    }

}
