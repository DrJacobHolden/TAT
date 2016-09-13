package tat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

/**
 * Created by Tate on 22/04/2016.
 */
public class GlobalConfiguration {

    private static final String CORPUS_PATH_KEY = "LastUsedCorpus";
    private static GlobalConfiguration instance;
    private final Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

    public static GlobalConfiguration getInstance() {
        if (instance == null)
            instance = new GlobalConfiguration();
        return instance;
    }

    public Path getCorpusPath() {
        String stringPath = prefs.get(CORPUS_PATH_KEY, "");
        if (stringPath.length() == 0) {
            return null;
        } else {
            return Paths.get(stringPath);
        }
    }

    public void setCorpusPath(Path path) {
        prefs.put(CORPUS_PATH_KEY, path.toString());
    }
}
