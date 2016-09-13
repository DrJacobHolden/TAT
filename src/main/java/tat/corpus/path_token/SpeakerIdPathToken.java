package tat.corpus.path_token;

import tat.corpus.Segment;
import tat.corpus.path_token.PathToken;

/**
 * Created by kalda on 25/06/2016.
 */
public class SpeakerIdPathToken implements PathToken {
    public String getToken() {
        return "%s";
    }

    public String getValue(Segment segment) {
        return "" + segment.getSpeakerId();
    }

    public void setValue(Segment segment, String val) {
        segment.setSpeakerId(Integer.parseInt(val));
    }

    public String getRegex() {
        return "[0-9]+";
    }
}
