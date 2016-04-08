package alignment.maus;

import alignment.formats.AlignmentFormat;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by kalda on 8/04/2016.
 */
public class MausResponse {

    private Element responseXml;

    public MausResponse(InputStreamReader reader) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        responseXml = builder.parse(new InputSource(new BufferedReader(reader))).getDocumentElement();
    }

    public boolean getSuccess() {
        try {
            return responseXml.getElementsByTagName("success").item(0).getNodeValue().equals("true");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getResultLink() {
        return responseXml.getElementsByTagName("downloadLink").item(0).getNodeName();
    }

    public Object getResult(Class<? extends AlignmentFormat> format) throws IOException {
        URL url = new URL(getResultLink());
        URLConnection connection = url.openConnection();
        InputStream in = connection.getInputStream();

        return null;
    }
}
