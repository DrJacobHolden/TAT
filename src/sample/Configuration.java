package sample;

import alignment.AlignmentProvider;
import alignment.maus.WebMaus;

/**
 * Created by Tate on 22/04/2016.
 */
public class Configuration {

    private static Configuration instance;
    public static Configuration getInstance() {
        if(instance == null)
            instance = new Configuration();
        return instance;
    }
    private Configuration() {    }

    public static AlignmentProvider alignmentProvider = new WebMaus();

}
