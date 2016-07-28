package tat;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by kalda on 28/07/2016.
 */
public class LoadingDialog {
    private final Stage stage;

    public LoadingDialog() {
        stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setWidth(300);
        stage.setHeight(100);
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setOnCloseRequest(event -> event.consume());

        HBox hbox = new HBox();
        hbox.getChildren().add(new Label("Loading"));
        Scene scene = new Scene(hbox);
        stage.setScene(scene);
    }

    public void show()  {
        stage.show();
    }

    public void hide() {
        stage.hide();
    }
}
