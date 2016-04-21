package alignment;

import alignment.formats.AlignmentFormat;
import sun.audio.AudioStream;

import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by kalda on 8/04/2016.
 */
public interface AlignmentProvider {
    public void setLanguage(Language language);
    public void setOutFormat(Class<? extends AlignmentFormat> alignmentFormat);
    //TODO: Change return type
    public Object generateAlignment(String transcription, AudioInputStream audioFile) throws IOException, AlignmentException;
}
