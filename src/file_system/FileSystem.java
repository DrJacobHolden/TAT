package file_system;

import javafx.stage.FileChooser;

/**
 * Created by Tate on 21/05/2016.
 */
public class FileSystem {

    /**
     * The root directory of corpus
     */
    private String rootDir = "C:/TestingDir";

    /**
     * The storage rules for the different FileSystemElements
     */
    private String audioStorageRule = "%n-%d-%s";
    private String annotationStorageRule = "%n-%d-%s";
    private String alignmentStorageRule = "%n-%d-%s";

    public FileSystem(String root) {
        //ASK USER FOR ROOT DIRECTORY

        //---- Existing Dir w Config -----//

        //Import config file
        //Import files
        //Start program

        //---- Existing Dir wo Config ----//

        //"No config file was found, are you creating a new corpus?"

        //---- Yes ----//
        //Dir is empty?

        //---- Yes ----//
        //setupNewCorpus();

        //---- No ----//
        //Out of scope
        //"This directory is not empty. Please create your new corpus in an empty directory."

        //---- No ----//
        //Out of scope
        //"Please relocate your config file or reimport your corpus into a new corpus."

        //---- Non-existing Dir ----//
        //setupNewCorpus();
        loadConfig();
    }

    private void loadConfig() {
        //TODO: Read storage rules and other settings from a file
    }

    /**
     * Saves a FileSystemElement to the FileSystem
     */
    public void save(FileSystemElement element) {
        element.save();
    }

}
