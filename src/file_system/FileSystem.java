package file_system;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Created by Tate on 21/05/2016.
 */
public class FileSystem {

    /**
     * The root directory of corpus
     */
    private String rootDir = "C:/TestingDir/";

    private String config = "config.txt";

    /**
     * The storage rules for the different FileSystemElements
     */
    private String audioStorageRule = "%n/-%d-%s";
    private String annotationStorageRule = "%n-%d-%s";
    private String alignmentStorageRule = "%n-%d-%s";

    /**
     * All the files in the corpus. Files are a collection of segments.
     */
    private Segment[][] segments;

    /**
     * Constructor used on first launch
     */
    public FileSystem() throws IOException {
        Files.walk(Paths.get(rootDir)).forEach(filePath -> {
            if (Files.isRegularFile(filePath) && filePath.endsWith(config)) {
                //TODO: Parse config, read in rules
                System.out.println(filePath);
            }
        });
        Path startingDir = Paths.get(rootDir);
        PrintFiles pf = new PrintFiles(this);
        try {
            Files.walkFileTree(startingDir, pf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void processFile(Path path) {
        String file = path.toString().substring(rootDir.length());
        //Audio
        if(file.endsWith(".wav")) {
            parseFile(file, audioStorageRule);
        }
        //Annotation
        else if (file.endsWith(".txt")) {
            parseFile(file, annotationStorageRule);
        }
        //Alignment
        else {
            //TODO: Exception
            System.out.println("A strange file has occurred.");
        }
    }

    private void parseFile(String file, String rule) {
        
    }

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
        //TODO: Should this pass the path where the file needs to be saved?
        element.save();
    }

}

class PrintFiles extends SimpleFileVisitor<Path> {

    FileSystem fs;

    protected PrintFiles(FileSystem fs) {
        this.fs = fs;
    }

    /**
     * Get all the regular files
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        if (attr.isRegularFile()) {
            if(!file.endsWith("config.txt")) {
                fs.processFile(file);
            }
        }
        return CONTINUE;
    }
}
