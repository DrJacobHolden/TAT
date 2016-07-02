package tat.view;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;
import tat.Main;
import ui.icon.Icon;
import ui.icon.IconLoader;

/**
 * Created by Tate on 29/06/2016.
 */
public class EditorMenuController {

    @FXML
    private IconButton splitButton;

    @FXML
    private IconButton joinButton;

    @FXML
    private IconButton alignButton;

    @FXML
    private IconButton zoomInButton;

    @FXML
    private IconButton zoomOutButton;

    @FXML
    private Pane alignmentPane;

    @FXML
    private IconButton openFileSelectorButton;

    @FXML
    private IconButton saveButton;

    @FXML
    private IconButton prevSegmentButton;

    @FXML
    private IconButton playButton;

    @FXML
    private IconButton pauseButton;

    @FXML
    private IconButton stopButton;

    @FXML
    private IconButton nextSegmentButton;

    @FXML
    private IconButton settingsButton;

    @FXML
    private TextFlow textArea;

    private Main main;

    private void loadIcons() {
        System.out.println("Icons loaded");
        splitButton.setIcons(new Icon(IconLoader.getInstance().splitIcon), new Icon(IconLoader.getInstance().splitIconPressed));
        joinButton.setIcons(new Icon(IconLoader.getInstance().joinIcon), new Icon(IconLoader.getInstance().joinIconPressed));
        alignButton.setIcons(new Icon(IconLoader.getInstance().alignIcon), new Icon(IconLoader.getInstance().alignIconPressed));
        zoomInButton.setIcons(new Icon(IconLoader.getInstance().zoomInIcon), new Icon(IconLoader.getInstance().zoomInIconPressed));
        zoomOutButton.setIcons(new Icon(IconLoader.getInstance().zoomOutIcon), new Icon(IconLoader.getInstance().zoomOutIconPressed));
        openFileSelectorButton.setIcons(new Icon(IconLoader.getInstance().mainFileIcon), new Icon(IconLoader.getInstance().mainFileIconPressed));
        saveButton.setIcons(new Icon(IconLoader.getInstance().saveIcon), new Icon(IconLoader.getInstance().saveIconPressed));
        prevSegmentButton.setIcons(new Icon(IconLoader.getInstance().prevIcon), new Icon(IconLoader.getInstance().prevIconPressed));
        playButton.setIcons(new Icon(IconLoader.getInstance().playIcon), new Icon(IconLoader.getInstance().playIconPressed));
        pauseButton.setIcons(new Icon(IconLoader.getInstance().pauseIcon), new Icon(IconLoader.getInstance().pauseIconPressed));
        stopButton.setIcons(new Icon(IconLoader.getInstance().stopIcon), new Icon(IconLoader.getInstance().stopIconPressed));
        nextSegmentButton.setIcons(new Icon(IconLoader.getInstance().nextIcon), new Icon(IconLoader.getInstance().nextIconPressed));
        settingsButton.setIcons(new Icon(IconLoader.getInstance().settingsIcon), new Icon(IconLoader.getInstance().settingsIconPressed));
    }

    /**
     * The constructor is called before the initialize() method.
     */
    public EditorMenuController() {}

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
