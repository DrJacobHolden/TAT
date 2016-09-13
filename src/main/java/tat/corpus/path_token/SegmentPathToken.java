package tat.corpus.path_token;

import tat.corpus.Segment;
import tat.corpus.path_token.PathToken;

/**
 * Created by kalda on 25/06/2016.
 */
public class SegmentPathToken implements PathToken {
    public String getToken() {
        return "%d";
    }

    public String getValue(Segment segment) {
        return "" + segment.getSegmentNumber();
    }

    public void setValue(Segment segment, String val) {
        segment.setSegmentNumber(Integer.parseInt(val));
    }

    public String getRegex() {
        return "[0-9]+";
    }
}

