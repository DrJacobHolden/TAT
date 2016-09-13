package tat.corpus.attribute;

/**
 * Created by max on 7/2/16.
 */
public class CustomAttributeInstance {

    public final CustomAttribute customAttribute;
    String value;

    public CustomAttributeInstance(CustomAttribute attribute) {
        customAttribute = attribute;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        if (value == null) {
            value = customAttribute.getDefaultValue();
        }
        return value;
    }

    public String getName() {
        return customAttribute.getName();
    }

}
