package tat.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import tat.Main;
import ui.icon.Icon;
import ui.icon.IconLoader;

/**
 * Created by Tate on 29/06/2016.
 */
public class MainMenuController {

    @FXML
    private IconButton fileButton;

    @FXML
    private AnchorPane textArea;

    @FXML
    private AnchorPane soundFileArea;

    private Main main;

    private void loadIcons() {
        System.out.println("Icons loaded");
        fileButton.setIcons(new Icon(IconLoader.getInstance().mainFileIcon), new Icon(IconLoader.getInstance().mainFileIconPressed));
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
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param main
     */
    public void setMainApp(Main main) {
        this.main = main;
    }


}

