package sample;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ui.AudioEditor;
import ui.AudioToolBar;
import ui.text_box.AnnotationArea;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        //This is the audio file that will be used throughout the application
        File audioFile = new File(this.getClass().getResource("recording.wav").getFile());
        String text = "Hello Max. This is Tate. | I'm just leaving you a voicemail message to test the voicemail functionality. | Complicated words. Made up of many vowels and sylables. And letters. Thank you. Please call me back on oh m two one, four five six, seven, eight, nine. Thanks. Thanks. Thanks. Poos and wees. Bye. This is a run on sentence.\n";

        //This is what is used for editing audio
        AudioEditor editor = new AudioEditor();

        VBox vbox = new VBox();
        vbox.getChildren().add(editor);

        //TODO: Prevent height hardcoding, possibly configurable?
        editor.setMinHeight(256);

        vbox.getChildren().add(new AudioToolBar(editor));

        vbox.getChildren().add(new AnnotationArea(text));

        //Create a scene
        Scene scene = new Scene(vbox);

        primaryStage.setTitle("Transcription Assistance Toolkit");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();

        editor.setAudioFile(audioFile);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
