package tat;

import alignment.AlignmentProvider;
import alignment.maus.WebMaus;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

/**
 * Created by Tate on 22/04/2016.
 */
public class GlobalConfiguration {

    private static GlobalConfiguration instance;
    public static GlobalConfiguration getInstance() {
        if(instance == null)
            instance = new GlobalConfiguration();
        return instance;
    }

    private final Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

    private static final String CORPUS_PATH_KEY = "LastUsedCorpus";

    public Path getCorpusPath() {
        String stringPath =  prefs.get(CORPUS_PATH_KEY, "");
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
