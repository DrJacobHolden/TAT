package alignment.maus;

import alignment.AlignmentException;
import alignment.AlignmentProvider;
import alignment.Language;
import alignment.formats.AlignmentFormat;
import alignment.formats.TextGrid;

import javax.sound.sampled.AudioInputStream;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.AllPermission;

/**
 * Created by kalda on 8/04/2016.
 */
public class WebMaus implements AlignmentProvider {

    private Language language = Language.EN_NZ;
    private Class<? extends AlignmentFormat> outFormat = TextGrid.class;

    @Override
    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public void setOutFormat(Class<? extends AlignmentFormat> alignmentFormat) {
        this.outFormat = outFormat;
    }

    @Override
    public AlignmentFormat generateAlignment(String transcription, AudioInputStream audioStream) throws IOException, AlignmentException {
        String url = "http://clarin.phonetik.uni-muenchen.de/BASWebServices/services/runMAUS";
        String charset = "UTF-8";

        MultipartUtility multipart = new MultipartUtility(url, charset);

        multipart.addFormField("OUTFORMAT", getMausAlignmentFormat(outFormat));
        //Can contain trailing and ending silence
        multipart.addFormField("NOINITIALFINALSILENCE", "false");
        multipart.addFormField("LANGUAGE", language.isoLanguage);
        //Pretend transcription is a file
        multipart.addFilePart("TEXT", "transcription.txt", new ByteArrayInputStream(transcription.getBytes(StandardCharsets.UTF_8)));
        multipart.addAudioPart("SIGNAL", "audio.wav", audioStream);

        try {
            MausResponse response = new MausResponse(multipart.finish());
            if (response.getSuccess()) {
                return response.getResult();
            } else {
                throw new AlignmentException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AlignmentException();
        }
    }

    private String getMausAlignmentFormat(Class<? extends AlignmentFormat> format){
        if (format == TextGrid.class){
            return "TextGrid";
        }
        return "";
    }
}
