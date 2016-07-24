package tat.view;

import file_system.Recording;
import file_system.Segment;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import tat.Position;
import tat.PositionListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tate on 23/07/2016.
 * //TODO: Ensure copying new files in with unsaved changes won't result in loss of changes
 */
public class AnnotationDisplay extends VBox implements PositionListener {

    private Recording recording;
    private Position position;

    private Segment activeSegment;

    private List<TextArea> annotationList = new ArrayList<>();

    private final String DEFAULT_TEXT = "Annotation Missing Please Add Annotation";

    public AnnotationDisplay() {
        super();
        setPadding(new Insets(0,0,0,0));
    };

    public void setPosition(Position position) {
        this.position = position;
        position.addSelectedListener(this);
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
        activeSegment = recording.getSegment(1);
        buildAnnotations();
        getChildren().addAll(annotationList);
    }

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

            TextArea text = createTextArea(annotationString, s);
            setColours();
            annotationList.add(text);
        }
    }

    public TextArea createTextArea(String annotation, Segment s) {
        TextArea t = new TextArea(annotation);
        t.setPadding(new Insets(0,0,0,0));
        t.setEditable(true);
        t.setWrapText(true);
        t.setPrefRowCount(1);
        t.textProperty().addListener((ov, prevText, currText) -> {
            Platform.runLater(() -> {
                s.getAnnotationFile().setString(currText);
            });
        });
        t.setOnMouseClicked(event -> {
            position.setSelected(s, 0, t);
        });
        return t;
    }

    @Override
    public void positionChanged(Segment segment, double frame, Object initiator) {
        if(segment != activeSegment) {
            activeSegment = segment;
            setColours();
        }
    }

    public void setColours() {
        for(TextArea t : annotationList) {
            if (annotationList.indexOf(t) == activeSegment.getSegmentNumber() - 1) {
                t.setStyle("-fx-text-fill: #ff7c00;");
            } else {
                if ((annotationList.indexOf(t)+1) % 2 == 0) {
                    t.setStyle("-fx-text-fill: #5c5a67;");
                } else {
                    t.setStyle("-fx-text-fill: #e4e1f0;");
                }
            }
        }
    }
}
