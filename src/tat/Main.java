package tat;

import file_system.FileSystem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tat.view.MainMenuController;
import ui.icon.IconLoader;

import java.io.IOException;

public class Main extends Application {

    public VBox rootLayout;
    public FileSystem fileSystem;

    @Override
    public void start(Stage primaryStage) throws Exception {

        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/MainMenu.fxml"));
            rootLayout = loader.load();

            MainMenuController controller = loader.getController();
            controller.setup(this, primaryStage);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            //Set the program title
            primaryStage.setTitle("Transcription Assistance Toolkit");
            //Set the program logo
            primaryStage.getIcons().add(IconLoader.getInstance().logoIcon);

            primaryStage.setOnCloseRequest(t -> {
                TimerHandler.getInstance().shutdownTimers();
                Platform.exit();
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
