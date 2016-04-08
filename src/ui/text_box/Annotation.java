package ui.text_box;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * Created by Tate on 8/04/2016.
 */
public class Annotation extends TextField {

    public Annotation(String s) {
        super(s);
        unhighlight();
        textProperty().addListener(new TextFieldTextListener());
        focusedProperty().addListener(new TextFieldFocusListener());
    }

    private boolean isActive = false;
    public boolean isActive() {
        return isActive;
    }

    public void highlight() {
        setStyle("-fx-text-inner-color: green;");
        isActive = true;
        for (Annotation a : AnnotationArea.getInstance().getSegments()) {
            if(!a.equals(this))
                a.unhighlight();
        }
    }

    public void unhighlight() {
        isActive = false;
        setStyle("-fx-text-inner-color: red;");
    }

    public void addCaret(int position) {
        setText(getText(0, position) + "|" + getText(position, getText().length()));
    }

    public void removeCaret() {
        setText(getText().replaceAll(AnnotationArea.SPLIT_CHAR, ""));
    }

    private class TextFieldTextListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            //Update textfield width
            setPrefWidth(getText().length() * AnnotationArea.TEXT_WIDTH);
        }
    }

    private class TextFieldFocusListener implements ChangeListener<Boolean> {
        @Override
        public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
            //Place the splitchar at the caret position when focus is lost
            if (!newPropertyValue) {
                addCaret(getCaretPosition());
            } else {
                removeCaret();
                highlight();
            }
        }
    }

}
