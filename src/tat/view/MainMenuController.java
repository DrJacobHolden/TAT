package tat.view;

import file_system.Config;
import file_system.FileSystem;
import file_system.Recording;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import tat.Main;
import ui.icon.Icon;
import ui.icon.IconLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Created by Tate on 29/06/2016.
 */
public class MainMenuController implements FileSelectedHandler {

    @FXML
    private IconButton fileButton;

    @FXML
    private IconButton settingsButton;

    @FXML
    private AnchorPane textArea;

    @FXML
    private AnchorPane soundFileArea;

    @FXML
    private MenuButton fileMenu;

    @FXML
    private Label corpus;

    private Main main;
    private Stage primaryStage;

    private void loadIcons() {
        System.out.println("Icons loaded");
        fileButton.setIcons(new Icon(IconLoader.getInstance().mainFileIcon), new Icon(IconLoader.getInstance().mainFileIconPressed));
        settingsButton.setIcons(new Icon(IconLoader.getInstance().settingsIcon), new Icon(IconLoader.getInstance().settingsIconPressed));
    }

    private void loadTooltips() {
        System.out.println("Tooltips Loaded");
        fileButton.setTooltip(new Tooltip("Open the file/directory selector.\n"));
        settingsButton.setTooltip(new Tooltip("Configure Corpus-wide settings.\n"));
    }

    private void loadFunctionality() {
        fileButton.setOnAction(event -> corpusSelect());
    }

    private void corpusSelect() {
        try {
            final DirectoryChooser rootDirChooser = new DirectoryChooser();
            rootDirChooser.setTitle("Select Corpus Root Directory");
            File file = rootDirChooser.showDialog(primaryStage);
            if (file != null) {
                Path path = Paths.get(file.getAbsolutePath());
                if (!FileSystem.corpusExists(path)) {
                    main.fileSystem = new FileSystem(path, Config.DEFAULT_AUDIO_STORAGE_RULE,
                            Config.DEFAULT_ANNOTATION_STORAGE_RULE, Config.DEFAULT_ALIGNMENT_STORAGE_RULE);
                }
                engageCorpus(path);
            }
        } catch(IOException e) {
            //TODO: Create error message
            //ErrorHandler.getInstance().showError("Failed to load corpus.");
            e.printStackTrace();
        }
    }

    public void fileSelected(String file) {
        moveToEditorScene(file);
    }

    private void moveToEditorScene(String file) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/EditorMenu.fxml"));
        try {
            main.rootLayout = loader.load();
        } catch(IOException e) {
            //TODO: Handle error PLEASE REINSTALL
            e.printStackTrace();
        }

        EditorMenuController controller = loader.getController();
        controller.setup(main, primaryStage, file);

        // Show the scene containing the root layout.
        Scene scene = new Scene(main.rootLayout);
        primaryStage.setScene(scene);
    }

    private void engageCorpus(Path path) throws IOException {
        main.fileSystem = new FileSystem(path);
        corpus.setText("Corpus: " + path);
        settingsButton.setDisable(false);
        //TODO: Make settings button highlighted to the user
        textArea.setDisable(false);
        soundFileArea.setDisable(false);
        EditorMenuController.
        populateFileMenu(this, main.fileSystem, fileMenu);
    }

    /**
     * The constructor is called before the initialize() method.
     */
    public MainMenuController() {}

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        loadIcons();
        loadTooltips();
        loadFunctionality();
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param main
     */
    public void setup(Main main, Stage primaryStage) {
        this.main = main;
        this.primaryStage = primaryStage;
    }


}

