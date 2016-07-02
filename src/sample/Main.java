package sample;

import file_system.Config;
import file_system.FileSystem;
import file_system.Recording;
import file_system.Segment;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import ui.AudioEditor;
import ui.AudioToolBar;
import ui.icon.IconLoader;
import undo.UndoRedoController;
import ui.text_box.AnnotationArea;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        //TODO: The program should remember what you were doing last time

        //Set the program title
        primaryStage.setTitle("Transcription Assistance Toolkit");
        //Set the program logo
        primaryStage.getIcons().add(IconLoader.getInstance().logoIcon);

        //Create the undoRedoController
        UndoRedoController undoRedoController = new UndoRedoController();


        FileSystem fileSystem = null;
        final DirectoryChooser rootDirChooser = new DirectoryChooser();
        while (fileSystem == null) {
            File file = rootDirChooser.showDialog(primaryStage);
            if (file != null) {
                Path path = Paths.get(file.getAbsolutePath());
                if (FileSystem.corpusExists(path)) {
                    fileSystem = new FileSystem(path);
                } else {
                    //TODO: Ask user about settings
                    fileSystem = new FileSystem(path, Config.DEFAULT_AUDIO_STORAGE_RULE,
                            Config.DEFAULT_ANNOTATION_STORAGE_RULE, Config.DEFAULT_ALIGNMENT_STORAGE_RULE);
                }
            }
        }

        Recording defaultRecording = fileSystem.recordings.get("tate1");
        Segment defaultSegment = defaultRecording.getSegment(1);

        AnnotationArea annotation = new AnnotationArea(defaultSegment.getAnnotationFile().getString());
        AudioEditor editor = new AudioEditor(undoRedoController, annotation);

        VBox vbox = new VBox();
        vbox.getChildren().add(editor);

        //TODO: Prevent height hardcoding, possibly configurable?
        editor.setMinHeight(256);

        vbox.getChildren().add(new AudioToolBar(editor, annotation));

        vbox.getChildren().add(annotation);

        //Create a scene
        Scene scene = new Scene(vbox);

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();

        editor.setAudioFile(defaultSegment.getAudioFile().getFile());

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
