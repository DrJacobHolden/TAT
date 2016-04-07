package ui.text_box;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tate on 5/04/2016.
 */
public class AnnotationArea extends TextFlow {

    private List<TextField> segments = new ArrayList<>();
    private String text;

    private final String SPLIT_CHAR = "\\|";
    private final double TEXT_WIDTH = 5.5;

    public AnnotationArea(String text) {
        this.text = text;
        VBox.setVgrow(this, Priority.ALWAYS);
        getSegments();
    }

    /**
     * Populates the current segments list based on the contents of the given string. Additional segments are
     * created for each instance of the split character.
     */
    private void getSegments() {
        String[] splits = text.split(SPLIT_CHAR);
        for (String s : splits) {
            TextField t = new TextField(s);
            t.setPrefWidth(t.getText().length() * TEXT_WIDTH);
            t.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    t.setPrefWidth(t.getText().length() * TEXT_WIDTH);
                }
            });
            segments.add(t);
        }
        update();
    }

    /**
     * Updates the segments displayed currently
     */
    private void update() {
        getChildren().removeAll();
        getChildren().addAll(segments);
    }

}
