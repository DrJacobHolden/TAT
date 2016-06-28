package file_system;

import file_system.element.AlignmentFile;
import file_system.element.AnnotationFile;
import file_system.element.AudioFile;
import file_system.element.FileSystemElement;
import file_system.path_token.PathToken;
import file_system.path_token.tokens.NamePathToken;
import file_system.path_token.tokens.SegmentPathToken;
import file_system.path_token.tokens.SpeakerIdPathToken;
import javafx.collections.transformation.SortedList;
import sun.rmi.runtime.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Created by Tate and Max on 21/05/2016.
 *
 * PATHS MUST BE IN UNIX FORMAT. / INSTEAD OF \
 */
public class FileSystem {

    private static Logger LOGGER = Logger.getLogger(FileSystem.class.getName());

    private final PathToken[] pathTokens = {
            new NamePathToken(),
            new SegmentPathToken(),
            new SpeakerIdPathToken()
    };

    /**
     * The root directory of corpus
     */
    private String rootDir;

    private String config = "config.txt";

    /**
     * The storage rules for the different FileSystemElements
     */
    private String audioStorageRule = "%n/%d-%s";
    private String annotationStorageRule = "%n/%d-%s";
    private String alignmentStorageRule = "%n/%d-%s";

    public String getAudioStorageRule() {
        return audioStorageRule;
    }

    public String getAnnotationStorageRule() {
        return annotationStorageRule;
    }

    public String getAlignmentStorageRule() {
        return alignmentStorageRule;
    }

    public Path pathFromString(Segment segment, String rule) {
        String path = rule;

        for (PathToken token : pathTokens) {
            path = path.replace(token.getToken(), token.getValue(segment));
        }
        //Join paths
        return new File(new File(rootDir), path).toPath();
    }

    /**
     * All the files in the corpus. Files are a collection of segments.
     */
    public List<Segment> segments = new ArrayList<>();

    private void segmentsFromAudioFiles(List<Path> files) {
        //From http://stackoverflow.com/questions/10664434/escaping-special-characters-in-java-regular-expressions
        Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
        String audioString = SPECIAL_REGEX_CHARS.matcher(audioStorageRule).replaceAll("\\$0");

        //Find positions of tokens in the string. We have to do this because we can't have multiple named groups with
        //the same name.
        List<TokenMatch> matches = new ArrayList<>();

        for (PathToken token : pathTokens) {
            //Tokens are two digits long
            int index = -2;
            //Find each index of token
            while ((index=audioString.indexOf(token.getToken(), index+token.getToken().length())) != -1) {
                LOGGER.info("Matched token "+token.getToken()+" in rule: "+audioString);
                matches.add(new TokenMatch(index, token));
            }
        }
        //Sort by position
        Collections.sort(matches);

        //Build regex
        for (TokenMatch tokenMatch : matches) {
            PathToken token = tokenMatch.token;
            //Create regex group
            audioString = audioString.replace(token.getToken(), "("+token.getRegex()+")");
        }
        //Three or four character file extension
        audioString += "\\.[a-z]{3,4}";
        LOGGER.info("Built audio regex: "+audioString);
        Pattern audioRegex = Pattern.compile(audioString);

        //See if matches. Build segment if it does.
        for (Path filePath : files) {
            //Convert \ to / for unix like paths
            String relativePath = Paths.get(rootDir).relativize(filePath).toString().replace('\\', '/');

            Matcher matcher = audioRegex.matcher(relativePath);
            if (matcher.matches()) {
                LOGGER.info(relativePath+" matches "+audioString);
                Segment segment = new Segment(this);
                //Sorted, so groups should be in correct order
                for (int i=0; i<matches.size(); i++) {
                    PathToken token = matches.get(i).token;
                    String textMatch = matcher.group(i+1);
                    //TODO: find inconsistency, throw error
                    token.setValue(segment, textMatch);
                }
                segments.add(segment);
            }
        }
    }

    private void importFiles() throws IOException {
        List<Path> filePaths = Files.walk(Paths.get(rootDir))
                .filter(Files::isRegularFile)
                //Is audio file
                .filter(path -> Arrays.stream(AudioFile.FILE_EXTENSIONS).anyMatch(ext -> path.toString().endsWith(ext)))
                .collect(Collectors.toList());
        //TODO: Filter to only be audio files
        segmentsFromAudioFiles(filePaths);
    }

    public FileSystem(String rootDir) throws IOException {
        this.rootDir = rootDir;
        //ASK USER FOR ROOT DIRECTORY

        //---- Existing Dir w Config -----//

        //Import config file
        loadConfig();
        //Import files
        importFiles();
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
    }

    private void loadConfig() {
        //TODO: Read storage rules and other settings from a file
    }
}

class TokenMatch implements Comparable<TokenMatch> {
    public int stringPos;
    public PathToken token;

    public TokenMatch(int stringPos, PathToken token) {
        this.stringPos = stringPos;
        this.token = token;
    }

    @Override
    public int compareTo(TokenMatch o) {
        return Integer.compare(this.stringPos, o.stringPos);
    }
}