package tat.view;

import file_system.Recording;
import file_system.Segment;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import tat.Position;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tate on 23/07/2016.
 * //TODO: Ensure copying new files in with unsaved changes won't result in loss of changes
 */
public class AnnotationDisplay extends TextFlow {

    private Recording recording;
    private Position position;

    private List<Annotation> annotationList = new ArrayList<>();

    private final String DEFAULT_TEXT = "Annotation Missing Please Add Annotation";
    public static final Font DEFAULT_FONT = Font.font("Levenim MT", 24);


    public AnnotationDisplay() {
        super();
        setPadding(new Insets(0,0,0,0));
        widthProperty().addListener((ob) -> {
            display();
        });
    };

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
        buildAnnotations();
        display();
    }

    /**
     * Must be called again on width resizing to ensure textfields are appropriately
     * sized
     */
    private void buildAnnotations() {
        if(recording == null) {
            //TODO: Throw error
            return;
        }

        for(int i = 1; i <= recording.getSegments().size(); i++) {
            Segment s = recording.getSegments().get(i);
            String annotationString = "";
            if(s == null || s.getAnnotationFile().getString().equals("")) {
                annotationString = DEFAULT_TEXT;
            } else {
                annotationString = s.getAnnotationFile().getString();
            }

            Annotation text = new Annotation(annotationString, s, recording, position, this);
            annotationList.add(text);
        }
    }

    public void display() {
        getChildren().clear();

        //The local x coordinates of the last textfield
        double lastX = 0;

        for(Annotation a : annotationList) {
            TextInfoTuple info = getSizesForString(a.getAnnotation(), lastX);
            lastX = info.getLastWidth();
            a.refreshAnnotations(info.getFirstLength(), info.getWidth());
            for(TextField t : a.getTextFields()) {
                getChildren().add(t);
                a.resizeAnnotation();
            }
        }
    }

    private TextInfoTuple getSizesForString(String s, double lastX) {
        Text text = new Text(s);
        text.setFont(DEFAULT_FONT); // Set the same font, so the size is the same
        double textLength = text.getLayoutBounds().getWidth() // This big is the Text in the TextField
                + 2d; // Add some spacing
        double width = getWidth();
        if(width == 0) {
            width = 1870; //Initial width
        }
        double lastWidth = textLength;
        double firstLength = width - lastX;
        textLength = textLength - firstLength;
        if(width < textLength) {
            lastWidth = textLength%width;
        }
        return new TextInfoTuple(lastWidth, width, firstLength);
    }

    class TextInfoTuple {
        //Length of text spilling onto next line
        double lastWidth;

        //Width of other lines
        double width;

        //The length of the first line
        double firstLength;

        public TextInfoTuple(double lastWidth, double width, double firstLength) {
            this.lastWidth = lastWidth;
            this.width = width;
            this.firstLength = firstLength;
        }

        public double getLastWidth() {
            return lastWidth;
        }

        public double getWidth() {
            return width;
        }

        public double getFirstLength() {
            return firstLength;
        }
    }
}
