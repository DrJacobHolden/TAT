package sample;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    int WIDTH = 1024;
    int HEIGHT = 768;

    @Override
    public void start(Stage primaryStage) throws Exception{
        File audioFile = new File(this.getClass().getResource("recording.wav").getFile());
        AudioEditor wf = new AudioEditor();
        wf.setAudioStream(audioFile);

        Pane root = new StackPane();

        ToolBar toolBar = new ToolBar(
                new Button("File")
        );
        VBox vbox = new VBox();
        root.getChildren().add(vbox);
        vbox.getChildren().add(wf);

        vbox.getChildren().add(toolBar);

        //Create a scene
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        vbox.getChildren().add(annotationBox());

        primaryStage.setTitle("Transcription Assistance Toolkit");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();

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
