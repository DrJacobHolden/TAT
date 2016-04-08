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
    public void setActiveSegment(Annotation a) {
        activeSegment = a;
    }
    public Annotation getActiveSegment() {
        return activeSegment;
    }

    private String text;

    public static final String SPLIT_CHAR = "\\|";
    public static final double TEXT_WIDTH = 5.7;

    public AnnotationArea(String text) {
        this.text = text;
        VBox.setVgrow(this, Priority.ALWAYS);
        createSegments();
    }

    public void split() {

        //Check the active segment exists
        if(getActiveSegment() == null) {
            System.out.println("You must select where to split your text.");
            return;
        }

        String[] halfs = getActiveSegment().getText().split(SPLIT_CHAR);
        if(halfs.length < 2) {
            //Do nothing if there aren't two halves in the active text field
            return;
        }
        int oldIndex = segments.indexOf(getActiveSegment());
        segments.remove(getActiveSegment());
        segments.add(oldIndex, new Annotation(this, halfs[0]));
        segments.add(oldIndex+1, new Annotation(this, halfs[1]));

        //Set a new active segment
        setActiveSegment(segments.get(oldIndex));

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
            Annotation t = new Annotation(this, s);
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
        //TODO: Fix for speed
        getChildren().removeAll(getChildren());
        getChildren().addAll(segments);
    }

    public List<Annotation> getSegments() {
        return segments;
    }

    public interface ActiveAnnotationListener {
        void onChange(Annotation active);
    }

    protected List<ActiveAnnotationListener> changeListeners = new ArrayList<>();

    protected void notifyChange() {
        for (ActiveAnnotationListener listener : changeListeners) {
            listener.onChange(getActiveSegment());
        }
    }

    public void addChangeListener(ActiveAnnotationListener listener) {
        changeListeners.add(listener);
    }

}
