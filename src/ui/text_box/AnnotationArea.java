package ui.text_box;

import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tate on 5/04/2016.
 */
public class AnnotationArea extends TextFlow {

    private List<Annotation> segments = new ArrayList<>();
    private Annotation activeSegment;

    private String text;

    public static final String SPLIT_CHAR = "\\|";
    public static final double TEXT_WIDTH = 5.7;

    /**
     * Called to change the active segment
     */
    public void setActiveSegment(int i) {
        if (i < segments.size()) {
            activeSegment = segments.get(i);
        }
    }

    /**
     * Called to change the active segment
     */
    public void setActiveSegment(Annotation ann) {
        activeSegment = ann;
    }

    public int getActiveSegment() {
        return segments.indexOf(activeSegment);
    }

    public AnnotationArea(String text) {
        this.text = text;
        VBox.setVgrow(this, Priority.ALWAYS);
        createSegments();
    }

    public void split() {
        //Check the active segment exists
        //TODO: confirm it is never null
        if(activeSegment == null) {
            System.out.println("You must select where to split your text.");
            return;
        }

        String[] halfs = activeSegment.getText().split(SPLIT_CHAR);
        if(halfs.length < 2) {
            //Do nothing if there aren't two halves in the active text field
            return;
        }
        int oldIndex = getActiveSegment();
        segments.remove(getActiveSegment());
        segments.add(oldIndex, new Annotation(this, halfs[0]));
        segments.add(oldIndex+1, new Annotation(this, halfs[1]));

        //Set a new active segment
        setActiveSegment(oldIndex);
        update();
        resizeAll();
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
            Annotation t = new Annotation(this, s);
            segments.add(t);
        }
        resizeAll();

        //Draw the next segments
        update();
    }

    private void resizeAll() {
        for (Annotation t : segments) {
            t.setPrefWidth(t.getText().length() * TEXT_WIDTH);
        }
    }

    /**
     * Updates the segments displayed currently
     */
    private void update() {
        //TODO: Fix for speed
        getChildren().removeAll(getChildren());
        getChildren().addAll(segments);
    }

    public List<Annotation> getSegments() {
        return segments;
    }
}
