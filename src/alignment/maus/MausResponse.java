package alignment.maus;

import alignment.formats.AlignmentFormat;
import alignment.formats.TextGrid;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;

/**
 * Created by kalda on 8/04/2016.
 */
public class MausResponse {

    private Element responseXml;
    private XPath xPath = XPathFactory.newInstance().newXPath();

    public MausResponse(InputStreamReader reader) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        responseXml = builder.parse(new InputSource(new BufferedReader(reader))).getDocumentElement();
    }

    public boolean getSuccess() {
        try {
            return (boolean) xPath.evaluate("/WebServiceResponseLink/success", responseXml, XPathConstants.BOOLEAN);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getResultLink() {
        try {
            return (String) xPath.evaluate("/WebServiceResponseLink/downloadLink", responseXml, XPathConstants.STRING);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AlignmentFormat getResult() throws IOException, ParseException {
        System.out.println(getResultLink());
        URL url = new URL(getResultLink());
        URLConnection connection = url.openConnection();
        InputStream in = connection.getInputStream();

        //TODO: Should depend on something
        AlignmentFormat format = TextGrid.load(in);

        in.close();

        return format;
    }
}
