package tat.ui.element.dialog;

import javafx.geometry.Pos;
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

    public LoadingDialog(String text) {
        stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setWidth(400);
        stage.setHeight(150);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setOnCloseRequest(event -> event.consume());

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setBorder(null);
        hbox.setStyle("-fx-background-color: #2b2934;");
        Label label = new Label(text);
        label.setStyle("-fx-font-family:'Levenim MT';\n" +
                "    -fx-font-size: 32;" +
                "-fx-text-fill: #e4e1f0;");
        hbox.getChildren().add(label);
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
