package file_system;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Created by max on 7/2/16.
 */
public class Config {
    private static Logger LOGGER = Logger.getLogger(Config.class.getName());

    public static final String DEFAULT_AUDIO_STORAGE_RULE = "%n/%d-%s";
    public static final String DEFAULT_ANNOTATION_STORAGE_RULE = "%n/%d-%s";
    public static final String DEFAULT_ALIGNMENT_STORAGE_RULE = "%n/%d-%s";


    private static final String ROOT_ELEMENT_NAME = "config";

    private static final String AUDIO_RULE_TAG_NAME = "audioRule";
    private static final String ANNOTATION_RULE_TAG_NAME = "annotationRule";
    private static final String ALIGNMENT_RULE_TAG_NAME = "alignmentRule";

    /**
     * The storage rules for the different FileSystemElements
     */
    public final String audioStorageRule;
    public final String annotationStorageRule;
    public final String alignmentStorageRule;

    public Config() {
        this(DEFAULT_AUDIO_STORAGE_RULE, DEFAULT_ANNOTATION_STORAGE_RULE, DEFAULT_ALIGNMENT_STORAGE_RULE);
    }

    public Config(String audioStorageRule, String annotationStorageRule, String alignmentStorageRule) {
        this.audioStorageRule = audioStorageRule;
        this.alignmentStorageRule = alignmentStorageRule;
        this.annotationStorageRule = annotationStorageRule;
    }

    public void save(Path configPath) throws IOException {
        try {
            LOGGER.info("Saving config to path " + configPath.toString());

            //Build XML document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.newDocument();
            Element root = document.createElement(ROOT_ELEMENT_NAME);
            document.appendChild(root);

            root.appendChild(createXmlTagElement(document, AUDIO_RULE_TAG_NAME, audioStorageRule));
            root.appendChild(createXmlTagElement(document, ANNOTATION_RULE_TAG_NAME, annotationStorageRule));
            root.appendChild(createXmlTagElement(document, ALIGNMENT_RULE_TAG_NAME, alignmentStorageRule));

            //Write to file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            StreamResult result = new StreamResult(configPath.toFile());
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(document), result);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private Element createXmlTagElement(Document document, String tagName, String value) {
        Element element = document.createElement(tagName);
        element.setTextContent(value);
        return element;
    }

    public static Config load(Path configPath) throws IOException {
        try {
            LOGGER.info("Loading config from path " + configPath.toString());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document xmlDoc = builder.parse(configPath.toFile());
            String audioRule = xmlDoc.getElementsByTagName(AUDIO_RULE_TAG_NAME).item(0).getTextContent();
            String annotationRule = xmlDoc.getElementsByTagName(ANNOTATION_RULE_TAG_NAME).item(0).getTextContent();
            String alignmentRule = xmlDoc.getElementsByTagName(ALIGNMENT_RULE_TAG_NAME).item(0).getTextContent();

            return new Config(audioRule, annotationRule, alignmentRule);
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException();
        }
    }

}
