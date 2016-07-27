package tat;

import file_system.FileSystem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tat.view.MainMenuController;
import tat.view.icon.IconLoader;

import java.io.IOException;

import static java.awt.SystemColor.info;

public class Main extends Application {

    public VBox rootLayout;
    public FileSystem fileSystem;
    public static Stage p;

    @Override
    public void start(Stage primaryStage) throws Exception {

        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClassLoader.getSystemResource("fxml/MainMenu.fxml"));
            rootLayout = loader.load();
            MainMenuController controller = loader.getController();
            controller.setup(this, primaryStage);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            scene.getStylesheets().add(ClassLoader.getSystemResource("css/textarea.css").toExternalForm());
            p = primaryStage;
            primaryStage.setScene(scene);
            //Set the program title
            primaryStage.setTitle("Transcription Assistance Toolkit");
            //Set the program logo
            primaryStage.getIcons().add(IconLoader.getInstance().logoIcon);

            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    TimerHandler.getInstance().shutdownTimers();
                    Platform.exit();
                }
            });

            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Alert createInfoDialog(String title, String info, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(info);
        alert.setGraphic(null);

        alert.initOwner(p);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(ClassLoader.getSystemResource("css/dialog.css").toExternalForm());

        alert.showAndWait();
        return alert;
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
