package ui.text_box;

import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Created by Tate on 5/04/2016.
 */
public class AnnotationBox extends TextArea {

    public AnnotationBox() {
        VBox.setVgrow(this, Priority.ALWAYS);
        setWrapText(true);
    }
}
