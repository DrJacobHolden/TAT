package file_system.path_token.tokens;

import file_system.Segment;
import file_system.path_token.PathToken;

/**
 * Created by kalda on 25/06/2016.
 */
public class NamePathToken implements PathToken {
    public String getToken() {
        return "%n";
    }

    public String getValue(Segment segment) {
        return segment.getBaseName();
    }

    public void setValue(Segment segment, String val) {
        segment.setBaseName(val);
    }

    public String getRegex() {
        return "[A-Za-z0-9_()\\- ]+";
    }
}
