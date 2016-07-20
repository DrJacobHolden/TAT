package tat.view;

import file_system.FileSystem;
import file_system.Recording;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import tat.Main;
import ui.icon.Icon;
import ui.icon.IconLoader;

import java.util.Set;

/**
 * Created by Tate on 29/06/2016.
 */
public class EditorMenuController implements FileSelectedHandler {

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

    @FXML
    private MenuButton fileMenu;

    @FXML
    private WaveformDisplay waveformDisplay;

    private Main main;
    private Stage primaryStage;
    private String activeRecording;

    public Recording getActiveRecording() {
        return main.fileSystem.recordings.get(activeRecording);
    }

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

    private void loadTooltips() {
        System.out.println("Tooltips Loaded");
        splitButton.setTooltip(new Tooltip("Splits the audio and annotation at the setColourSelected point.\n"));
        joinButton.setTooltip(new Tooltip("Joins the audio and annotation at the setColourSelected point.\n"));
        alignButton.setTooltip(new Tooltip("Sends the audio and transcription to the alignment\n" +
                "system. When complete the results will be displayed."));
        zoomInButton.setTooltip(new Tooltip("Zoom in.\n"));
        zoomOutButton.setTooltip(new Tooltip("Zoom out.\n"));
        openFileSelectorButton.setTooltip(new Tooltip("Open a new file.\n"));
        saveButton.setTooltip(new Tooltip("Save the corpus in the current state.\n"));
        prevSegmentButton.setTooltip(new Tooltip("Previous segment.\n"));
        playButton.setTooltip(new Tooltip("Begin playback.\n"));
        pauseButton.setTooltip(new Tooltip("Pause at current point.\n"));
        stopButton.setTooltip(new Tooltip("Stop playback.\n"));
        nextSegmentButton.setTooltip(new Tooltip("Next segment.\n"));
        settingsButton.setTooltip(new Tooltip("Open the configuration menu.\n"));
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
        loadTooltips();
        bindZoomButtons();
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param main
     */
    public void setup(Main main, Stage ps, String activeRecording) {
        this.main = main;
        this.primaryStage = ps;
        this.activeRecording = activeRecording;
        populateFileMenu(this, main.fileSystem, fileMenu);
        fileSelected(activeRecording);
    }

    public static void populateFileMenu(FileSelectedHandler handler, FileSystem fileSystem, MenuButton fileMenu) {
        ObservableList<MenuItem> menuItems = fileMenu.getItems();
        menuItems.clear();
        Set<String> recordings = fileSystem.recordings.keySet();
        for(String name : recordings) {
            MenuItem mu = new MenuItem(name);
            menuItems.add(mu);
            mu.setOnAction(event -> handler.fileSelected(mu.getText()));
        }
        if(recordings.size() > 0) {
            fileMenu.setText(recordings.size() + " File(s)");
        }
    }

    @Override
    public void fileSelected(String file) {
        fileMenu.setText(activeRecording);
        waveformDisplay.setRecording(getActiveRecording());
        waveformDisplay.drawWaveform();
    }

    private void bindZoomButtons() {
        double zoomFactor = 1.2;
        zoomInButton.setOnAction(event -> {
            waveformDisplay.setZoomFactor(waveformDisplay.getZoomFactor()*zoomFactor);
        });
        zoomOutButton.setOnAction(event -> {
            waveformDisplay.setZoomFactor(waveformDisplay.getZoomFactor()/zoomFactor);
        });
    }
}
