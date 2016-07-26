package file_system.path_token;

import file_system.Segment;

/**
 * Created by kalda on 25/06/2016.
 */
public interface PathToken {
    String getToken();
    String getValue(Segment segment);
    String getRegex();
    void setValue(Segment segment, String val);
}
