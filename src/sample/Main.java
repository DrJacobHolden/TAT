package sample;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ui.AudioEditor;

import java.io.File;

public class Main extends Application {

    int WIDTH = 1024;
    int HEIGHT = 768;

    @Override
    public void start(Stage primaryStage) throws Exception {

        //This is the audio file that will be used throughout the application
        File audioFile = new File(this.getClass().getResource("recording.wav").getFile());

        //This is what is used for editing audio
        AudioEditor editor = new AudioEditor();

        Pane root = new StackPane();

        HBox mainPane = new HBox();
        VBox vbox = new VBox();
        root.getChildren().add(mainPane);
        mainPane.getChildren().add(new MenuToolbar());
        mainPane.getChildren().add(vbox);
        vbox.getChildren().add(editor);
        vbox.getChildren().add(annotationBox());

        //Create a scene
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        primaryStage.setTitle("Transcription Assistance Toolkit");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();

        editor.setAudioStream(audioFile);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private TextArea annotationBox() {
        TextArea text = new TextArea();
        text.setPrefWidth(WIDTH);
        text.setPrefHeight(200);
        text.setWrapText(true);
        return text;
    }
}
