package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;

public class Main extends Application {

    //The total length of the displayed waveform
    final double TRACK_LENGTH = 60;

    Image wave;

    //The total length of the displayed waveform
    final double TRACK_LENGTH = 60;

    Image wave;
    ImageView iv1 = new ImageView();
    Slider progressSlider1 = new Slider();
    Slider progressSlider2 = new Slider();
    final int WIDTH = 1024;
    final int HEIGHT = 768;
    final double ZOOM = 1.2;

    double secondsPerPixel = TRACK_LENGTH / WIDTH;

    @Override
    public void start(Stage primaryStage) throws Exception{
        File audioFile = new File(this.getClass().getResource("recording.wav").getFile());
        WaveformGenerator gen = new WaveformGenerator(audioFile);
        wave = gen.getWaveformImage(50);

        progressSlider1.setMax(TRACK_LENGTH);
        progressSlider2.setMax(TRACK_LENGTH);

        Pane root = new StackPane();

        ToolBar toolBar = new ToolBar(
                new Button("File")
        );
        VBox vbox = new VBox();
        root.getChildren().add(vbox);
        vbox.getChildren().add(toolBar);

        //Create a scene
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        StackPane waveFormStack = new StackPane();
        drawWaveformBox(waveFormStack);

        vbox.getChildren().add(waveFormStack);

        waveFormStack.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                progressSlider1.setValue(mouseEvent.getSceneX()*secondsPerPixel);
            }
        });

        waveFormStack.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                progressSlider2.setValue(mouseEvent.getSceneX()*secondsPerPixel);
            }
        });

        vbox.getChildren().add(progressSlider1);
        vbox.getChildren().add(progressSlider2);

        ToolBar zoomToolbar = new ToolBar(
                plusButton(),
                minusButton()
        );
        Separator sep = new Separator();
        ToolBar playbackToolbar = new ToolBar(
                new Button("|>"),
                new Button("||"),
                new Button("[]")
        );
        Separator sep2 = new Separator();
        ToolBar splitAndJoinToolbar = new ToolBar(
                new Button("Split"),
                new Button("Join")
        );
        HBox.setHgrow(sep, Priority.ALWAYS);
        HBox.setHgrow(sep2, Priority.ALWAYS);
        HBox toolbars = new HBox();
        toolbars.getChildren().addAll(zoomToolbar, sep, playbackToolbar, sep2, splitAndJoinToolbar);
        toolbars.setPrefWidth(WIDTH);

        vbox.getChildren().add(toolbars);
        vbox.getChildren().add(annotationBox());

        primaryStage.setTitle("Transcription Assistance Toolkit");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();

    }

    public void updateSliders() {
        progressSlider1.setValue(0);
        progressSlider2.setValue(0);
        progressSlider1.setMax(secondsPerPixel*WIDTH);
        progressSlider2.setMax(secondsPerPixel*WIDTH);
    }

    public void zoomIn() {
        iv1.setFitWidth(iv1.getFitWidth() * ZOOM);
        secondsPerPixel = secondsPerPixel * 0.80;
        updateSliders();

    }

    public void zoomOut() {
        if (iv1.getFitWidth() / ZOOM >= WIDTH) {
            iv1.setFitWidth(iv1.getFitWidth() / ZOOM);
            secondsPerPixel = secondsPerPixel / 0.80;
            updateSliders();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void drawWaveformBox(Pane root) {
        //Load image and resize
        iv1.setImage(wave);
        iv1.setFitHeight(200);
        iv1.setFitWidth(WIDTH);

        HBox box = new HBox();
        box.getChildren().add(iv1);

        ScrollPane sPane = new ScrollPane(box);
        sPane.setPrefViewportHeight(iv1.getFitHeight());
        sPane.setFitToHeight(true);
        sPane.setFitToWidth(true);

        root.getChildren().add(new BorderPane(sPane));
    }

        public Button plusButton() {

            Button zoom = new Button("+");

            zoom.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    zoomIn();
                }
            });

            return zoom;
        }

    public Button minusButton() {
        Button zoomMinus = new Button("-");

        zoomMinus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                zoomOut();
            }
        });

        return zoomMinus;
    }

    private TextArea annotationBox() {
        TextArea text = new TextArea();
        text.setPrefWidth(WIDTH);
        text.setPrefHeight(200);
        text.setWrapText(true);
        return text;
    }
}
