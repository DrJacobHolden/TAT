package file_system;

import file_system.attribute.CustomAttribute;
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
import java.util.*;
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
    private static final String CONFIG_FILE = "config.xml";
    private static final PathToken[] defaultPathTokens = {
            new NamePathToken(),
            new SegmentPathToken(),
            new SpeakerIdPathToken()
    };

    public List<PathToken> getPathTokens() {
        List<PathToken> tokens = Arrays.asList(defaultPathTokens);
        tokens.addAll(config.customAttributes);
        return tokens;
    }

    private final Path rootDir;
    private final Config config;

    public String getAudioStorageRule() {
        return config.audioStorageRule;
    }

    public String getAnnotationStorageRule() {
        return config.annotationStorageRule;
    }

    public String getAlignmentStorageRule() {
        return config.alignmentStorageRule;
    }

    /**
     * Generate a path for a segment from a given rule.
     * @param segment The segment to generate a path for
     * @param rule The rule to use
     * @return The path
     */
    public Path pathFromString(Segment segment, String rule) {
        String path = rule;

        for (PathToken token : getPathTokens()) {
            path = path.replace(token.getToken(), token.getValue(segment));
        }
        //Join paths
        return rootDir.relativize(Paths.get(path));
    }

    public Map<String, Recording> recordings;

    /**
     * Builds a list of segments from a list of audio files
     * @param files
     * @return The found segments
     */
    private List<Segment> segmentsFromAudioFiles(List<Path> files) {
        List<Segment> segments = new ArrayList<>();

        //From http://stackoverflow.com/questions/10664434/escaping-special-characters-in-java-regular-expressions
        Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
        String audioString = SPECIAL_REGEX_CHARS.matcher(getAudioStorageRule()).replaceAll("\\$0");

        //Find positions of tokens in the string. We have to do this because we can't have multiple named groups with
        //the same name.
        List<TokenMatch> matches = new ArrayList<>();

        for (PathToken token : getPathTokens()) {
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
            String relativePath = rootDir.relativize(filePath).toString().replace('\\', '/');

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
        return segments;
    }

    /**
     * Construct a list of segments from the root directory
     * @return a list of segments
     * @throws IOException
     */
    private List<Segment> loadSegments() throws IOException {
        List<Path> filePaths = Files.walk(rootDir)
                .filter(Files::isRegularFile)
                //Is audio file
                .filter(path -> Arrays.stream(AudioFile.FILE_EXTENSIONS).anyMatch(ext -> path.toString().endsWith(ext)))
                .collect(Collectors.toList());
        //TODO: Filter to only be audio files
        return segmentsFromAudioFiles(filePaths);
    }

    private void importRecordings() throws IOException {
        recordings = Recording.groupSegments(loadSegments());
        checkForMissingSegments();
    }

    /**
     * Checks that all required segments exist
     */
    private void checkForMissingSegments() {
        recordings.values().stream().filter(recording -> recording.missingSegments()).forEach(recording -> {
            //TODO: Alert user
        });
    }

    /**
     * Does the corpus already exist?
     * @param path
     * @return
     */
    public static boolean corpusExists(Path path) {
        return path.resolve(CONFIG_FILE).toFile().exists();
    }

    /**
     * When the corpus already exists
     * @param rootDir The root directory of the corpus
     * @throws IOException
     */
    public FileSystem(Path rootDir) throws IOException {
        this.rootDir = rootDir;
        config = Config.load(rootDir.resolve(CONFIG_FILE));
        loadSegments();
    }

    /**
     * Construct a new corpus
     * @param rootDir
     * @param audioStorageRule
     * @param annotationStorageRule
     * @param alignmentStorageRule
     */
    public FileSystem(Path rootDir, String audioStorageRule, String annotationStorageRule, String alignmentStorageRule) throws IOException {
        this.rootDir = rootDir;
        config = new Config(audioStorageRule, annotationStorageRule, alignmentStorageRule);
        config.save(rootDir.resolve(CONFIG_FILE));

        //In case files aready exist
        loadSegments();
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