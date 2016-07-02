package file_system.attribute;

import file_system.Segment;
import file_system.path_token.PathToken;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 7/2/16.
 */
public class CustomAttribute implements PathToken {

    //XML stuff
    public static final String CUSTOM_ATTRIBUTES_TAG_NAME = "tokens";
    public static final String CUSTOM_ATTRIBUTE_TAG_NAME = "token";

    public static final String NAME_ATTR_NAME = "name";
    public static final String DEFAULT_VALUE_ATTR_NAME = "default";
    public static final String TOKEN_ATTR_NAME = "default";

    private final String name;
    private String defaultValue;
    private String token;

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    @Override
    public String getToken() {
        return token;
    }

    private CustomAttributeInstance findOrCreateAttributeInstance(Segment segment) {
        CustomAttributeInstance[] instances = segment.customAttributes.stream()
                .filter(attr -> attr.customAttribute == this)
                .toArray(size -> new CustomAttributeInstance[size]);
        if (instances.length > 1) {
            throw new  IllegalStateException("Segment contains more than one instance of attribute " + this.name);
        } else if (instances.length == 1) {
            return instances[0];
        } else {
            CustomAttributeInstance instance = newCustomAttributeInstance();
            segment.customAttributes.add(instance);
            return instance;
        }
    }

    @Override
    public String getValue(Segment segment) {
        return findOrCreateAttributeInstance(segment).getValue();
    }

    @Override
    public void setValue(Segment segment, String val) {
        findOrCreateAttributeInstance(segment).setValue(val);
    }

    @Override
    public String getRegex() {
        return "[a-zA-Z1-9]+";
    }

    public CustomAttributeInstance newCustomAttributeInstance() {
        return new CustomAttributeInstance(this);
    }

    public CustomAttribute(String name, String defaultValue, String token) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.token = token;
    }

    //XML stuff
    public Element toXMLElement(Document document) {
        Element element = document.createElement(CUSTOM_ATTRIBUTE_TAG_NAME);
        element.setAttribute(NAME_ATTR_NAME, name);
        element.setAttribute(DEFAULT_VALUE_ATTR_NAME, defaultValue);
        element.setAttribute(TOKEN_ATTR_NAME, defaultValue);
        return element;
    }

    public static CustomAttribute fromXMLElement(Element element) {
        String name = element.getAttribute(NAME_ATTR_NAME);
        String defaultValue = element.getAttribute(DEFAULT_VALUE_ATTR_NAME);
        String token = element.getAttribute(DEFAULT_VALUE_ATTR_NAME);
        return new CustomAttribute(name, defaultValue, token);
    }

    public static Element listToXMLElement(Document document, List<CustomAttribute> attributes) {
        Element attributesElement = document.createElement(CUSTOM_ATTRIBUTES_TAG_NAME);
        for (CustomAttribute attribute : attributes) {
            attributesElement.appendChild(attribute.toXMLElement(document));
        }
        return attributesElement;
    }

    public static List<CustomAttribute> XMLElementsToList(Element element) {
        List<CustomAttribute> customAttributes = new ArrayList<>();

        NodeList elements = element.getElementsByTagName(CUSTOM_ATTRIBUTE_TAG_NAME);
        for (int i=0; i<elements.getLength(); i++) {
            //Cast seems safe
            CustomAttribute attr = CustomAttribute.fromXMLElement((Element) elements.item(i));
            customAttributes.add(attr);
        }
        return customAttributes;
    }
}
