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

    private static AnnotationArea instance = null;
    public static AnnotationArea getInstance() {
        if (instance == null)
            instance = new AnnotationArea();
        return instance;
    }

    public void setText(String text) {
        this.text = text;
        createSegments();
    }

    private List<Annotation> segments = new ArrayList<>();
    private String text;

    public static final String SPLIT_CHAR = "\\|";
    public static final double TEXT_WIDTH = 5.7;

    private AnnotationArea() {
        VBox.setVgrow(this, Priority.ALWAYS);
    }

    public void split() {

        //Get the currently active text segment
        Annotation active = null;
        for (Annotation s : segments) {
            if(s.isActive())
                active = s;
        }

        //Check the active segment exists
        if(active == null) {
            System.out.println("You must select where to split your text.");
            return;
        }

        String[] halfs = active.getText().split(SPLIT_CHAR);
        if(halfs.length < 2) {
            //Do nothing if there aren't two halves in the active text field
            return;
        }
        int oldIndex = segments.indexOf(active);
        segments.remove(active);
        segments.add(oldIndex, new Annotation(halfs[0]));
        segments.add(oldIndex+1, new Annotation(halfs[1]));
        update();
    }

    public void join() {

    }

    /**
     * Populates the current segments list based on the contents of the given string. Additional segments are
     * created for each instance of the split character.
     */
    private void createSegments() {
        //Clear anything already in the list
        segments.removeAll(segments);

        //Get the segments by splitting on the split character
        String[] splits = text.split(SPLIT_CHAR);
        for (String s : splits) {
            Annotation t = new Annotation(s);
            t.setPrefWidth(t.getText().length() * TEXT_WIDTH);
            segments.add(t);
        }

        //Draw the next segments
        update();
    }

    /**
     * Updates the segments displayed currently
     */
    private void update() {
        getChildren().removeAll(getChildren());
        getChildren().addAll(segments);
    }

    public List<Annotation> getSegments() {
        return segments;
    }

}
