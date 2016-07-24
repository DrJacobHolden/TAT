package tat.view;

import audio_player.AudioPlayer;
import file_system.FileSystem;
import file_system.Recording;
import file_system.Segment;
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
import tat.Position;
import tat.PositionListener;
import ui.icon.Icon;
import ui.icon.IconLoader;

import java.io.IOException;
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
    private AnnotationDisplay textArea;

    @FXML
    private MenuButton fileMenu;

    @FXML
    private WaveformDisplay waveformDisplay;

    private Main main;
    private Stage primaryStage;
    private String activeRecording;

    private AudioPlayer player;
    private Position position;

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
        splitButton.setTooltip(new Tooltip("Splits the audio and annotation at the selected point.\n"));
        joinButton.setTooltip(new Tooltip("Joins the audio and annotation at the selected point.\n"));
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
        bindPlayerButtons();
        bindSaveButton();
        bindSplitAndJoinButtons();
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
        position = new Position();

        fileMenu.setText(activeRecording);
        waveformDisplay.setRecording(getActiveRecording());
        textArea.setRecording(getActiveRecording());
        textArea.setPosition(position);
        waveformDisplay.drawWaveform();
        waveformDisplay.setPosition(position);

        player = new AudioPlayer(position);

        position.setSelected(getActiveRecording().getSegment(1), 0, this);
    }

    private void bindPlayerButtons() {
        playButton.setOnAction(event -> player.play());
        pauseButton.setOnAction(event -> player.pause());
        stopButton.setOnAction(event -> player.stop());
        nextSegmentButton.setOnAction(event -> maybeChangeSegment(+1));
        prevSegmentButton.setOnAction(event -> maybeChangeSegment(-1));
    }

    private void maybeChangeSegment(int offset) {
        Segment newSegment = getActiveRecording().getSegment(position.getSegment().getSegmentNumber() + offset);
        if (newSegment != null) {
            position.setSelected(newSegment, 0, this);
        }
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

    private void bindSaveButton() {
        saveButton.setOnAction(event -> {
            try {
                getActiveRecording().save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void bindSplitAndJoinButtons() {
        splitButton.setOnAction(event -> {
            try {
                //Allow files to be overridden
                player.closeOpenFiles();
                Segment segment2 = getActiveRecording().split(position.getSegment(), position.getFrame(), 0);
                //waveformDisplay.onSplit(currentSegment, segment2, currentFrame);
                //Select the second split segment
                //maybeChangeSegment(+1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        joinButton.setOnAction(event -> {
            Segment nextSegment = getActiveRecording().getSegment(position.getSegment().getSegmentNumber()+1);
            if (nextSegment == null) {
                return;
            }
            try {
                player.closeOpenFiles();
                getActiveRecording().join(position.getSegment(), nextSegment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
