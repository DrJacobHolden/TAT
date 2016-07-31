package tat.view;

import alignment.AlignmentException;
import audio_player.AudioPlayer;
import file_system.FileSystem;
import file_system.Recording;
import file_system.Segment;
import file_system.element.AudioFile;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tat.LoadingDialog;
import tat.Main;
import tat.Position;
import tat.view.icon.Icon;
import tat.view.icon.IconLoader;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static tat.Main.p;

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
        bindAlignButton();
    }

    private void bindAlignButton() {
        alignButton.setOnAction(event -> {
            LoadingDialog dialog = new LoadingDialog();
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Platform.runLater(dialog::show);
                    for (Segment segment : getActiveRecording()) {
                        try {
                            segment.generateAlignment();
                        } catch (AlignmentException | IOException e) {
                            Platform.runLater(() -> Main.createInfoDialog("Alignment", "Failed to generate alignment", Alert.AlertType.ERROR));
                        }
                    }
                    Platform.runLater(dialog::hide);
                    return null;
                }
            };
            new Thread(task).start();
        });
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
        setupMenu(fileMenu);
        populateFileMenu(main.fileSystem, fileMenu, activeRecording);
        fileSelected(activeRecording);
    }

    public static void setupMenu(MenuButton menu) {
        menu.showingProperty().addListener((a) -> {
            if(menu.isShowing()) {
                menu.setTextFill(Colours.ORANGE);
                menu.setStyle("-fx-background-color: #1c1b22; -fx-mark-color: #ff7c00; " +
                        "-fx-background-radius: 0 0 0 0, 0 0 0 0, 0 0 0 0;");
            } else {
                menu.setTextFill(Colours.WHITE);
                menu.setStyle("-fx-background-color: #1c1b22; -fx-mark-color: #e4e1f0; " +
                        "-fx-background-radius: 0 0 0 0, 0 0 0 0, 0 0 0 0;");
            }
        });
    }

    private static FileSelectedHandler fileHandler = null;
    public static void setFileSelectedHandler(FileSelectedHandler handler) {
        fileHandler = handler;
    }

    /**
     * Method supplied by Erickson @ stackoverflow
     * Source: http://stackoverflow.com/questions/740299/how-do-i-sort-a-set-to-a-list-in-java
     */
    public static
    <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }

    public static void populateFileMenu(FileSystem fileSystem, MenuButton fileMenu, String activeRecording) {
        ObservableList<MenuItem> menuItems = fileMenu.getItems();
        menuItems.clear();
        Set<String> recordings = fileSystem.recordings.keySet();
        List<String> recordingsSorted = asSortedList(recordings);
        for(String name : recordingsSorted) {
            SizingMenuItem mu;
            if(activeRecording != null && activeRecording.equals(name)) {
                mu = new SizingMenuItem(fileMenu, name, true);
            } else {
                mu = new SizingMenuItem(fileMenu, name, false);
            }

            menuItems.add(mu);
            mu.setOnAction(event -> fileHandler.fileSelected(name));
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
                //Allow files to be overridden
                player.closeOpenFiles();
                getActiveRecording().save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void bindSplitAndJoinButtons() {
        splitButton.setOnAction(event -> {
            int frame = position.getFrame();
            if (frame > AudioFile.MIN_SPLIT_FRAMES) {
                try {
                    Segment segment2 = getActiveRecording().split(position.getSegment(), frame, textArea.getCursorPosInCurrentSegment());
                    waveformDisplay.onSplit(position.getSegment(), segment2, frame);
                    //Reset textarea with updated recording
                    textArea.setRecording(getActiveRecording());
                    //Select the second split segment
                    maybeChangeSegment(+1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        joinButton.setOnAction(event -> {
            Segment nextSegment = getActiveRecording().getSegment(position.getSegment().getSegmentNumber()+1);
            if (nextSegment == null) {
                return;
            }
            try {
                getActiveRecording().join(position.getSegment(), nextSegment);
                waveformDisplay.onJoin(position.getSegment(), nextSegment);
                //Reset textarea with updated recording
                textArea.setRecording(getActiveRecording());
                //Reselect the current segment
                maybeChangeSegment(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean createSaveDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Recording?");
        alert.setHeaderText(null);
        alert.setContentText("Would you like to save your recording? Unsaved changes will be lost.");
        alert.setGraphic(null);

        alert.initOwner(p);

        ButtonType buttonYes = new ButtonType("Yes");
        ButtonType buttonNo = new ButtonType("No");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonYes, buttonNo, buttonTypeCancel);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(ClassLoader.getSystemResource("css/dialog.css").toExternalForm());

        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == buttonNo) {
            //Don't save
            return true;
        } else if(result.get() == buttonYes) {
            //Save
            //Allow files to be overridden
            try {
                player.closeOpenFiles();
                getActiveRecording().save();
            } catch (Exception e) {
                Main.createInfoDialog("Error: Saving Files", "Files were unable to be saved due to an unknown error." +
                        " Sorry.", Alert.AlertType.INFORMATION);
                //Cancel file switch so that changes are not lost
                return false;
            }
            return true;
        }
        return false;
    }
}
