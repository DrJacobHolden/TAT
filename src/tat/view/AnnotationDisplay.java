package tat.view;

import file_system.Recording;
import file_system.Segment;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.text.TextFlow;
import tat.Position;
import tat.PositionListener;

import java.util.ArrayList;
import java.util.List;

import static com.sun.jmx.snmp.EnumRowStatus.active;

/**
 * Created by Tate on 23/07/2016.
 * //TODO: Ensure copying new files in with unsaved changes won't result in loss of changes
 */
public class AnnotationDisplay extends TextFlow implements PositionListener {

    private Recording recording;
    private Segment activeSegment;
    private Position position;

    private List<TextField> annotationList = new ArrayList<>();

    private final String DEFAULT_TEXT = "Annotation Missing";


    public AnnotationDisplay() {
        super();
    };

    public void setPosition(Position position) {
        this.position = position;
        position.addSelectedListener(this);
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
        buildAnnotationList();
        setupAnnotationListeners();
        display();
    }

    private void buildAnnotationList() {
        if(recording == null) {
            //TODO: Throw error
            return;
        }
        for(int i = 1; i <= recording.getSegments().size(); i++) {
            Segment s = recording.getSegments().get(i);
            String annotationString = "";
            if(s == null || s.getAnnotationFile().getString().equals("")) {
                annotationString = DEFAULT_TEXT;
            }
            TextField text = new TextField(annotationString);
            annotationList.add(text);
        }
    }

    private void setupAnnotationListeners() {
        for(TextField t : annotationList) {
            t.setOnMouseClicked(event -> {
                position.setSelected(recording.getSegment(annotationList.indexOf(t)+1), 0, this);
            });
        }
    }

    private void display() {
        getChildren().removeAll();
        for(TextField t : annotationList) {
            setSegmentColours(t);
            t.setFont(Font.font("Levenim MT", 24));

            t.textProperty().addListener((ov, prevText, currText) -> {
                Platform.runLater(() -> {
                    autosizeTextField(t);
                });
            });
            getChildren().add(t);
            Platform.runLater(() -> {
                autosizeTextField(t);
            });
        }
    }

    private void autosizeTextField(TextField t) {
        //Source: http://stackoverflow.com/questions/12737829/javafx-textfield-resize-to-text-length
        Text text = new Text(t.getText());
        text.setFont(t.getFont()); // Set the same font, so the size is the same
        double width = text.getLayoutBounds().getWidth() // This big is the Text in the TextField
                + t.getPadding().getLeft() + t.getPadding().getRight() // Add the padding of the TextField
                + 2d; // Add some spacing
        t.setPrefWidth(width); // Set the width
        t.positionCaret(t.getCaretPosition()); // If you remove this line, it flashes a little bit
    }

    @Override
    public void positionChanged(Segment segment, double frame, Object initiator) {
        if(activeSegment != segment) {
            if(activeSegment != null) {
                setSegmentColours(annotationList.get(activeSegment.getSegmentNumber() -1));
            }
            this.activeSegment = segment;
            annotationList.get(activeSegment.getSegmentNumber() -1).setStyle("-fx-text-fill: #ff7c00; -fx-background-color: #2b2934;");
        }
    }

    public void setSegmentColours(TextField t) {
        if(annotationList.indexOf(t) %2  == 0) {
            t.setStyle("-fx-text-fill: #e4e1f0; -fx-background-color: #2b2934;");
        } else {
            t.setStyle("-fx-text-fill: #5c5a67; -fx-background-color: #2b2934;");
        }
    }
}
