package tat.view;

import file_system.Config;
import file_system.Corpus;
import file_system.Recording;
import file_system.element.AlignmentFile;
import file_system.element.AnnotationFile;
import file_system.element.AudioFile;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tat.GlobalConfiguration;
import tat.Main;
import tat.TimerHandler;
import tat.view.icon.Icon;
import tat.view.icon.IconLoader;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static tat.Main.p;

/**
 * Created by Tate on 29/06/2016.
 */
public class MainMenuController implements FileSelectedHandler {

    @FXML
    private VBox window;

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
    private boolean menuFlash = false;
    private boolean menuFlashing = false;
    private EditorMenuController editor = null;

    //Used if annotation file is dragged in before audio
    private File annotationFile;

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
        settingsButton.setOnAction(event -> showSettingsMenu());
        soundFileArea.setOnMouseReleased(event -> showAudioSelector());
        textArea.setOnMouseReleased(event -> showAnnotationSelector());
    }

    private void showAudioSelector() {
        FileChooser audioChooser = getFileChooser("Audio", AudioFile.FILE_EXTENSIONS);
        File file = audioChooser.showOpenDialog(primaryStage);
        if(file != null)
            addAudioFile(file, null, null);
    }

    private void showAnnotationSelector() {
        FileChooser annotationChooser = getFileChooser("Annotation", AnnotationFile.FILE_EXTENSIONS);
        File file = annotationChooser.showOpenDialog(primaryStage);
        if(file != null)
            addAnnotationFile(file);
    }

    public FileChooser getFileChooser(String type, String[] extensions) {
        final FileChooser chooser = new FileChooser();

        extensions = Arrays.stream(extensions).map(ext -> "*" + ext).toArray(String[]::new);

        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter(type + " Files", extensions);
        chooser.getExtensionFilters().add(extFilter);
        chooser.setTitle("Select " + type + " Files");

        return chooser;
    }

    private void corpusSelect() {
        try {
            final DirectoryChooser rootDirChooser = new DirectoryChooser();
            rootDirChooser.setTitle("Select Corpus Root Directory");
            File file = rootDirChooser.showDialog(primaryStage);
            if (file != null) {
                setCorpus(file);
            }
        } catch(IOException e) {
            Main.createInfoDialog("Error", "Failed to load corpus.", Alert.AlertType.INFORMATION);
        }
    }

    private void showSettingsMenu() {
        //TODO: Create settings menu and display
        settingsButton.setFlashing(false);
        menuFlash();
    }


    //TODO: Generic or specific error messages?
    public void setCorpus(File file) throws IOException {
        Path path = Paths.get(file.getAbsolutePath());
        if (!Corpus.corpusExists(path)) {
            main.corpus = new Corpus(path, Config.DEFAULT_AUDIO_STORAGE_RULE,
                    Config.DEFAULT_ANNOTATION_STORAGE_RULE, Config.DEFAULT_ALIGNMENT_STORAGE_RULE);
        }
        engageCorpus(path);
    }

    public void fileSelected(String file) {
        if(editor != null) {
            if(!editor.createSaveDialog()) {
                //User chose cancel or saving failed
                return;
            }
        }
        moveToEditorScene(file);
    }

    private void moveToEditorScene(String file) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ClassLoader.getSystemResource("fxml/EditorMenu.fxml"));
        try {
            main.rootLayout = loader.load();
        } catch(IOException e) {
            Main.createInfoDialog("Error", "Program error, please reinstall.", Alert.AlertType.INFORMATION);
        }

        editor = loader.getController();
        editor.setup(main, primaryStage, file, this);

        // Show the scene containing the root layout.
        Scene scene = new Scene(main.rootLayout);
        scene.getStylesheets().add(ClassLoader.getSystemResource("css/textarea.css").toExternalForm());
        scene.getStylesheets().add(ClassLoader.getSystemResource("css/dialog.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private void engageCorpus(Path path) throws IOException {
        GlobalConfiguration.getInstance().setCorpusPath(path);
        main.corpus = new Corpus(path);
        corpus.setText("Corpus: " + path);
        settingsButton.setDisable(false);
        settingsButton.setFlashing(true);
        textArea.setDisable(false);
        soundFileArea.setDisable(false);
        initialiseDragAndDrop();
        EditorMenuController.populateFileMenu(main.corpus, fileMenu, null);
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

    private void loadLastUsedCorpus() {
        Path path = GlobalConfiguration.getInstance().getCorpusPath();
        if (path != null) {
            try {
                engageCorpus(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param main
     */
    public void setup(Main main, Stage primaryStage) {
        this.main = main;
        this.primaryStage = primaryStage;

        //Main must be set before setting main.filesystem
        loadLastUsedCorpus();
        EditorMenuController.setupMenu(fileMenu);
        EditorMenuController.setFileSelectedHandler(this);
    }

    public void initialiseDragAndDrop() {
            window.setOnDragOver(new EventHandler <DragEvent>()  {
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
                                invalidFileFound = !isValidExtension(file, acceptedTypes);
                            } else {
                                //Only accept a single item if the item is a directory
                                if (db.getFiles().size() != 1) {
                                    invalidFileFound = true;
                                }
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
                if (f.isDirectory()) {
                    if(Corpus.corpusExists(f.toPath())) {
                        try {
                            setCorpus(f);
                        } catch (IOException e) {
                            //TODO: Error handling
                            e.printStackTrace();
                        }
                    } else {
                        //Make corpus selector flash
                        Main.createInfoDialog("Information", "To create a new corpus please use the file selector button.", Alert.AlertType.INFORMATION);
                    }
                } else if (isValidExtension(f, AudioFile.FILE_EXTENSIONS)) {
                    audioFiles.add(f);
                } else if (isValidExtension(f, AnnotationFile.FILE_EXTENSIONS)) {
                    annotationFiles.put(getBasename(f), f);
                } else if (isValidExtension(f, AlignmentFile.FILE_EXTENSIONS)) {
                    alignmentFiles.put(getBasename(f), f);
                }
            }

            //Loop through audioFiles
            for(File f : audioFiles) {
                String name = getBasename(f);
                addAudioFile(f, annotationFiles.remove(name), alignmentFiles.remove(name));
            }

            //Handle remaining files
            for (File f: annotationFiles.values()) {
                addAnnotationFile(f);
            }
            for (File f: alignmentFiles.values()) {
                addAlignmentFile(f);
            }
        });

    }

    public void addAudioFile(File f, File annotationFile, File alignmentFile) {
        String name = getBasename(f);
        Recording r = main.corpus.recordings.get(name);
        if(r != null) {
            Main.createInfoDialog("Error", "A duplicate audio file called " + name + " already exists. Please rename" +
                    " this file before reimporting.", Alert.AlertType.INFORMATION);
            return;
        }
        main.corpus.importExternalRecording(f, annotationFile, alignmentFile);
        EditorMenuController.populateFileMenu(main.corpus, fileMenu, null);
    }

    public void addAnnotationFile(File f) {
        String name = getBasename(f);
        Recording r = main.corpus.recordings.get(name);
        if(r != null) {
            int segmentId = 0;
            if (r.getSegments().size() != 1) {
                int id = createSegmentSelectorDialog("Annotation", name, r.getSegments().size());
                if(id == 0) //Cancel/Window close
                    return;
                segmentId = id;
            }
            main.corpus.importExternalAnnotation(r.getSegment(segmentId), f);
        } else {
            Main.createInfoDialog("Information", "Annotation files cannot be imported without a matching audio file.",
                    Alert.AlertType.INFORMATION);
        }
    }

    public void addAlignmentFile(File f) {
        String name = getBasename(f);
        Recording r = main.corpus.recordings.get(name);
        if(r != null) {
            int segmentId = 0;
            if (r.getSegments().size() != 1) {
                int id = createSegmentSelectorDialog("Alignment", name, r.getSegments().size());
                if(id == 0) //Cancel/Window close
                    return;
                segmentId = id;
            }
            main.corpus.importExternalAlignment(r.getSegment(segmentId), f);
        } else {
            Main.createInfoDialog("Information", "Alignment files cannot be imported without a matching audio file.",
                    Alert.AlertType.INFORMATION);
        }
    }

    /**
     * Tells the menu button to start flashing
     * This does not need to be disabled as using the file menu causes a transition
     * to the editorMenu and currently there is no way to return to the mainMenu. As
     * a result the user will never see the button flashing forever.
     */
    private void menuFlash() {
        if(!menuFlashing) {
            menuFlashing = true;
            TimerHandler.getInstance().newTimer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    if (menuFlash) {
                                        fileMenu.setStyle("-fx-background-color: #1c1b22; -fx-mark-color: #e4e1f0;");
                                        fileMenu.setTextFill(Colours.WHITE);
                                        menuFlash = false;
                                    } else {
                                        fileMenu.setStyle("-fx-background-color: #1c1b22; -fx-mark-color: #ff7c00;");
                                        fileMenu.setTextFill(Colours.ORANGE);
                                        menuFlash = true;
                                    }
                                }
                            });
                        }
                    }, 0, 500);
        }
    }

    public static String getBasename(File f) {
        return f.getName().substring(0, f.getName().lastIndexOf('.'));
    }

    public boolean isValidExtension(File file, String[] extensions) {
        return Arrays.stream(extensions).anyMatch(e -> file.toString().endsWith(e));
    }

    public static int createSegmentSelectorDialog(String type, String filename, int count) {
        ChoiceDialog<Integer> alert = new ChoiceDialog(1, IntStream.range(1, count+1).boxed().
                collect(Collectors.toList()));
        alert.setTitle("Assign " + type);
        alert.setHeaderText(filename + " matched recording " + filename + " which segment\n" +
                " would you like to assign" +
                " this " + type.toLowerCase() + " to?");
        alert.setContentText("Segment: ");
        alert.setGraphic(null);

        alert.initOwner(p);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(ClassLoader.getSystemResource("css/dialog.css").toExternalForm());

        Optional<Integer> result = alert.showAndWait();
        if(result.isPresent())
            return result.get();
        return 0;
    }
}

