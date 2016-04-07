package sample;

import audio_player.AudioPlayer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ui.AudioEditor;
import ui.AudioToolBar;
import ui.text_box.AnnotationBox;
import undo.UndoRedoController;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.scene.input.KeyCode.T;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        UndoRedoController undoRedoController = new UndoRedoController();

        //This is the audio file that will be used throughout the application
        File audioFile = new File(this.getClass().getResource("recording.wav").getFile());

        //This is what is used for editing audio
        AudioEditor editor = new AudioEditor();
        editor.setUndoRedoController(undoRedoController);

        VBox vbox = new VBox();
        vbox.getChildren().add(editor);

        //TODO: Prevent height hardcoding, possibly configurable?
        editor.setMinHeight(256);

        vbox.getChildren().add(new AudioToolBar(editor));

        vbox.getChildren().add(new AnnotationBox());

        //Create a scene
        Scene scene = new Scene(vbox);

        primaryStage.setTitle("Transcription Assistance Toolkit");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();

        editor.setAudioFile(audioFile);

        KeyCodeCombination ctrlz = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
        KeyCodeCombination ctrly = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (ctrlz.match(event)) {
                undoRedoController.undo();
            } else if (ctrly.match(event)) {
                undoRedoController.redo();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
