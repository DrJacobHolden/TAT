package tat.view;

import alignment.AlignmentException;
import audio_player.AudioPlayer;
import file_system.FileSystem;
import file_system.Recording;
import file_system.Segment;
import file_system.element.AlignmentFile;
import file_system.element.AnnotationFile;
import file_system.element.AudioFile;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.wellbehaved.event.*;
import org.fxmisc.wellbehaved.event.InputMap;
import javafx.scene.control.*;
import tat.LoadingDialog;
import tat.Main;
import tat.Position;
import tat.view.icon.Icon;
import tat.view.icon.IconLoader;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import static java.awt.Color.red;
import static java.awt.SystemColor.window;
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
    private IconButton openCorpusButton;

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

    @FXML
    private VBox window;

    private Main main;
    private Stage primaryStage;
    private String activeRecording;
    private MainMenuController mainMenu;

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
        openCorpusButton.setIcons(new Icon(IconLoader.getInstance().openCorpusIcon), new Icon(IconLoader.getInstance().openCorpusIconPressed));
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
        openFileSelectorButton.setTooltip(new Tooltip("Add a new recording to the corpus.\n"));
        openCorpusButton.setTooltip(new Tooltip("Open the corpus directory in the file explorer.\n"));
        saveButton.setTooltip(new Tooltip("Save the recording in the current state.\n"));
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
        initialiseDragAndDrop();
        bindZoomButtons();
        bindPlayerButtons();
        bindSaveButton();
        bindOpenCorpusButton();
        bindSplitAndJoinButtons();
        bindAlignButton();
        bindOpenFileSelectorButton();
    }

    private void bindOpenFileSelectorButton() {
        openFileSelectorButton.setOnAction(event -> {
            FileChooser annotationChooser = mainMenu.getFileChooser("Add New Recording", AudioFile.FILE_EXTENSIONS);
            File file = annotationChooser.showOpenDialog(primaryStage);
            if(file != null) {
                if (mainMenu.isValidExtension(file, AudioFile.FILE_EXTENSIONS)) {
                    mainMenu.addAudioFile(file, null, null);
                }
            }
        });
    }

    private void bindAlignButton() {
        alignButton.setOnAction(event -> {
            LoadingDialog dialog = new LoadingDialog("Generating Alignment");
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
    public void setup(Main main, Stage ps, String activeRecording, MainMenuController mainMenuController) {
        this.main = main;
        this.primaryStage = ps;
        this.activeRecording = activeRecording;
        this.mainMenu = mainMenuController;
        setupMenu(fileMenu);
        populateFileMenu(main.fileSystem, fileMenu, activeRecording);
        fileSelected(activeRecording);
    }

    private void setupRightClickMenu() {
        textArea.setContextMenu(new RightClickMenu(getActiveRecording(), this, position));
        waveformDisplay.setContextMenu(new RightClickMenu(getActiveRecording(), this, position));
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
        int size = 0;
        for(Recording recording : fileSystem) {
            size++;
            String displayName = (recording.hasNoAnnotation() ? "* " : "") + recording.getBaseName();
            SizingMenuItem mu = new SizingMenuItem(fileMenu, displayName, recording.getBaseName().equals(activeRecording));

            menuItems.add(mu);
            mu.setOnAction(event -> fileHandler.fileSelected(recording.getBaseName()));
        }
        if (size > 0) {
            fileMenu.setText("" + size + " File(s)");
        } else {
            fileMenu.setText("No files");
        }
    }

    @Override
    public void fileSelected(String file) {
        position = new Position();

        fileMenu.setText(activeRecording);
        waveformDisplay.setRecording(getActiveRecording());
        textArea.initialise(getActiveRecording(), position);
        waveformDisplay.drawWaveform();
        waveformDisplay.setPosition(position);
        setupRightClickMenu();

        player = new AudioPlayer(position);

        position.setSelected(getActiveRecording().getSegment(1), 0, this);
        bindKeyEvents();
    }

    private void bindKeyEvents() {
        main.rootLayout.getChildrenUnmodifiable().stream().forEach(a ->
            Nodes.addInputMap(a, InputMap.consume((javafx.scene.input.KeyEvent.ANY), e -> {
                if (e.getCode().equals(KeyCode.P) && e.isControlDown() && e.getEventType().equals(KeyEvent.KEY_PRESSED)) {
                    if (!player.isPlaying()) {
                        playButton.fire();
                    } else {
                        pauseButton.fire();
                    }
                    e.consume();
                } else if (e.getCode().equals(KeyCode.I) && e.isControlDown() && e.getEventType().equals(KeyEvent.KEY_PRESSED)) {
                    prevSegmentButton.fire();
                    e.consume();
                } else if (e.getCode().equals(KeyCode.O) && e.isControlDown() && e.getEventType().equals(KeyEvent.KEY_PRESSED)) {
                    nextSegmentButton.fire();
                    e.consume();
                } else if (e.getCode().equals(KeyCode.H) && e.isControlDown() && e.getEventType().equals(KeyEvent.KEY_PRESSED)) {
                    splitButton.fire();
                    e.consume();
                } else if (e.getCode().equals(KeyCode.J) && e.isControlDown() && e.getEventType().equals(KeyEvent.KEY_PRESSED)) {
                    joinButton.fire();
                    e.consume();
                } else if (a != textArea) {
                    //Text area should be focused. Do no consume.
                    textArea.requestFocus();
                }
            })));
    }

    private void bindPlayerButtons() {
        playButton.setOnAction(event -> player.play());
        pauseButton.setOnAction(event -> player.pause());
        stopButton.setOnAction(event -> player.stop());
        nextSegmentButton.setOnAction(event -> maybeChangeSegment(+1));
        prevSegmentButton.setOnAction(event -> maybeChangeSegment(-1));
    }

    private void maybeChangeSegment(int offset) {
        //Jump to start of current if not at the beginning
        if (offset < 0 && position.getFrame() != 0) {
            offset++;
        }
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

    private void bindOpenCorpusButton() {
        openCorpusButton.setOnAction(event -> {
            try {
                File file = new File(main.fileSystem.getRootDir().toUri());
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
            } catch(IOException e) {
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
                    textArea.initialise(getActiveRecording(), position);
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
                textArea.initialise(getActiveRecording(), position);
                //Reselect the current segment
                maybeChangeSegment(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean createSaveDialog() {
        if (getActiveRecording().saveIsUpToDate()) {
            return true;
        }

        DialogBox dialog = new DialogBox("Save Recording?", "Would you like to save your recording? Unsaved changes will be lost.",
                new DialogBox.DialogOption[]{DialogBox.DialogOption.YES, DialogBox.DialogOption.NO, DialogBox.DialogOption.CANCEL});

        DialogBox.DialogOption result = dialog.showAndGetResult();
        if(result == DialogBox.DialogOption.NO) {
            //Don't save
            return true;
        } else if(result == DialogBox.DialogOption.YES) {
            //Save
            //Allow files to be overridden
            try {
                getActiveRecording().save();
            } catch (Exception e) {
                new DialogBox("Error: Saving Files", "Files were unable to be saved due to an unknown error. Sorry.").showAndGetResult();
                //Cancel file switch so that changes are not lost
                return false;
            }
            return true;
        }
        return false;
    }

    public void removeSegment(Segment toRemove) {
        if (getActiveRecording().size() > 1) {
            DialogBox dialog = new DialogBox("Delete Segment?", "Are you sure you wish to delete the selected segment?",
                    new DialogBox.DialogOption[]{DialogBox.DialogOption.YES, DialogBox.DialogOption.NO});
            if (dialog.showAndGetResult() == DialogBox.DialogOption.YES) {

                boolean wasLastSegment = toRemove.getSegmentNumber() == getActiveRecording().size();

                getActiveRecording().removeSegment(toRemove);
                textArea.initialise(getActiveRecording(), position);

                waveformDisplay.onRemove(toRemove);
                textArea.initialise(getActiveRecording(), position);

                if (wasLastSegment) {
                    //One fewer active recording
                    maybeChangeSegment(-2);
                } else {
                    maybeChangeSegment(0);
                }
            }
        } else {
            new DialogBox("Error: Cannot remove", "A recording must contain at least one segment").showAndGetResult();
        }
    }

    public void initialiseDragAndDrop() {
        window.setOnDragOver(new EventHandler<DragEvent>()  {
            public String[] acceptedTypes = Stream.concat(
                    Arrays.stream(AudioFile.FILE_EXTENSIONS),
                    Stream.concat(Arrays.stream(AnnotationFile.FILE_EXTENSIONS),
                            Arrays.stream(AlignmentFile.FILE_EXTENSIONS)))
                    .toArray(String[]::new);

            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean invalidFileFound = false;
                if (db.hasFiles()) {
                    for(File file : db.getFiles()) {
                        String absolutePath = file.getAbsolutePath();
                        if (!file.isDirectory() && !invalidFileFound) {
                            invalidFileFound = !mainMenu.isValidExtension(file, acceptedTypes);
                        } else {
                            //Don't accept directories
                            invalidFileFound = true;
                        }
                    }
                    if (!invalidFileFound)
                        event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });

        window.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            List<File> audioFiles = new ArrayList<File>();
            Map<String, File> annotationFiles = new HashMap<String, File>();
            Map<String, File> alignmentFiles = new HashMap<String, File>();

            for(File f : db.getFiles()) {
                if (mainMenu.isValidExtension(f, AudioFile.FILE_EXTENSIONS)) {
                    audioFiles.add(f);
                } else if (mainMenu.isValidExtension(f, AnnotationFile.FILE_EXTENSIONS)) {
                    annotationFiles.put(MainMenuController.getBasename(f), f);
                } else if (mainMenu.isValidExtension(f, AlignmentFile.FILE_EXTENSIONS)) {
                    alignmentFiles.put(MainMenuController.getBasename(f), f);
                }
            }

            //Loop through audioFiles
            for(File f : audioFiles) {
                String name = MainMenuController.getBasename(f);
                mainMenu.addAudioFile(f, annotationFiles.remove(name), alignmentFiles.remove(name));
            }

            //Handle remaining files
            for (File f: annotationFiles.values()) {
                mainMenu.addAnnotationFile(f);
            }
            for (File f: alignmentFiles.values()) {
                mainMenu.addAlignmentFile(f);
            }
            textArea.initialise(getActiveRecording(), position);
        });

    }
}
