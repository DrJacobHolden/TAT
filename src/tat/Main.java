package tat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tat.view.EditorMenuController;
import tat.view.MainMenuController;
import ui.icon.IconLoader;
import undo.UndoRedoController;

import java.io.IOException;

public class Main extends Application {

    final String text = "Hello Max. This is Tate. " +
            "I'm just leaving you a voicemail message to test the voicemail functionality. " +
            "Complicated words. Made up of many vowels and sylables. And letters. Thank you. " +
            "Please call me back on oh m two one, four five six, seven, eight, nine. Thanks. " +
            "Thanks. Thanks. Poos and wees. Bye. This is a run on sentence.\n";

    @Override
    public void start(Stage primaryStage) throws Exception {

       UndoRedoController undoRedoController = new UndoRedoController();

        /*//This is the audio file that will be used throughout the application
        File audioFile = new File(this.getClass().getResource("recording.wav").getFile());
        AnnotationArea annotation = new AnnotationArea(text);
        AudioEditor editor = new AudioEditor(undoRedoController, annotation);
        VBox vbox = new VBox();
        vbox.getChildren().add(editor);
        //TODO: Prevent height hardcoding, possibly configurable?
        editor.setMinHeight(256);
        vbox.getChildren().add(new AudioToolBar(editor, annotation));
        vbox.getChildren().add(annotation);
        editor.setAudioFile(audioFile);*/

        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/EditorMenu.fxml"));
            VBox rootLayout = loader.load();

            EditorMenuController controller = loader.getController();
            controller.setMainApp(this);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            //Set the program title
            primaryStage.setTitle("Transcription Assistance Toolkit");
            //Set the program logo
            primaryStage.getIcons().add(IconLoader.getInstance().logoIcon);

            KeyCodeCombination ctrlz = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
            KeyCodeCombination ctrly = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);
            scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if (ctrlz.match(event)) {
                    undoRedoController.undo();
                } else if (ctrly.match(event)) {
                    undoRedoController.redo();
                }
            });

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
