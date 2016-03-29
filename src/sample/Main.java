package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;

public class Main extends Application {

    Image wave;
    ImageView iv1 = new ImageView();
    final int WIDTH = 1024;
    final int HEIGHT = 768;
    final double ZOOM = 1.2;

    @Override
    public void start(Stage primaryStage) throws Exception{
        File audioFile = new File(this.getClass().getResource("recording.wav").getFile());
        WaveformGenerator gen = new WaveformGenerator(audioFile);
        wave = gen.getWaveformImage(50);

        Pane root = new StackPane();
        VBox vbox = new VBox();
        root.getChildren().add(vbox);

        //Create a scene
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        drawWaveformBox(vbox);
        HBox buttonsBox = new HBox();
        vbox.getChildren().add(buttonsBox);
        vbox.getChildren().add(annotationBox());
        drawZoomButtons(buttonsBox);
        drawPlayerButtons(buttonsBox);

        primaryStage.setTitle("Transcription Assistance Toolkit");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void drawWaveformBox(Pane root) {
        //Load image and resize
        iv1.setImage(wave);
        iv1.setFitHeight(200);
        iv1.setFitWidth(root.getScene().getWidth());

        HBox box = new HBox();
        box.getChildren().add(iv1);

        ScrollPane sPane = new ScrollPane(box);
        sPane.setPrefViewportHeight(iv1.getFitHeight());
        sPane.setFitToHeight(true);
        sPane.setFitToWidth(true);

        root.getChildren().add(new BorderPane(sPane));
    }

    private void drawZoomButtons(Pane root) {
        //Create the buttons
        Button zoomPlus = new Button("+");
        Button zoomMinus = new Button("-");

        zoomPlus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                iv1.setFitWidth(iv1.getFitWidth() * ZOOM);
            }
        });
        zoomMinus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(iv1.getFitWidth() / ZOOM >= WIDTH )
                    iv1.setFitWidth(iv1.getFitWidth() / ZOOM );
            }
        });

        HBox box = new HBox();

        box.getChildren().add(zoomPlus);
        box.getChildren().add(zoomMinus);

        root.getChildren().add(box);

    }

    private void drawPlayerButtons(Pane root) {
        Button play = new Button("   |>   ");
        Button pause = new Button("   ||    ");
        Button stop = new Button("   []    ");

        HBox box = new HBox();

        box.getChildren().add(play);
        box.getChildren().add(pause);
        box.getChildren().add(stop);

        root.getChildren().add(box);
    }

    private TextArea annotationBox() {
        TextArea text = new TextArea();
        text.setPrefWidth(WIDTH);
        text.setPrefHeight(200);
        text.setWrapText(true);
        return text;
    }
}
