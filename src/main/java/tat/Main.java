package tat;

import file_system.FileSystem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tat.view.MainMenuController;
import tat.view.icon.IconLoader;
import java.io.IOException;

public class Main extends Application {

    public VBox rootLayout;
    public FileSystem fileSystem;

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
