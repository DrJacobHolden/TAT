package tat.view;

import file_system.Recording;
import file_system.Segment;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.StyledTextArea;
import tat.Position;
import tat.PositionListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Text;
import static java.awt.SystemColor.text;
import static javax.swing.UIManager.get;


/**
 * Created by Tate on 23/07/2016.
 */
public class AnnotationDisplay extends StyleClassedTextArea implements PositionListener {

    private Recording recording;
    private tat.Position position;

    private Segment activeSegment;
    private IndexRange activeRange = new IndexRange(0,0);

    private IndexRange previousSegRange;

    private List<IndexRange> annotations = new ArrayList<>();
    private String fullString = "";

    private boolean textUpdated = false;

    private final String DEFAULT_TEXT = "Annotation Missing Please Add Annotation";

    public AnnotationDisplay() {
        super();
        setPadding(new Insets(0,0,0,0));
    };

    public void setPosition(tat.Position position) {
        this.position = position;
        position.addSelectedListener(this);
        selectionProperty().addListener((observable) -> {
            if(previousSegRange != null) {
                if(getSelection().getStart() == 0 && getSelection().getEnd() == fullString.length()) {
                    selectRange(previousSegRange.getStart(), previousSegRange.getEnd());
                    previousSegRange = null;
                    return;
                }
                previousSegRange = null;
            }
            if(getSelection().getLength() == 0) {
                if (getSelection().getStart() == 0 && getSelection().getEnd() == 0) {
                    previousSegRange = annotations.get(activeSegment.getSegmentNumber() - 1);
                }
            }
            if(getSelection().getStart() < activeRange.getStart() || getSelection().getEnd() > activeRange.getEnd()) {
                checkActiveSegment();
            }
            if(getSelection().getLength() == 0) {
               return;
            }
            if(getSelection().getEnd() > activeRange.getEnd()) {
                selectRange(getSelection().getStart(), activeRange.getEnd());
            }
            if(getSelection().getStart() < activeRange.getStart()) {
                selectRange(activeRange.getStart(), getSelection().getEnd());
            }
        });
    }

    private void checkActiveSegment() {
        for(IndexRange i : annotations) {
            if (getSelection().getStart() >= i.getStart() && getSelection().getEnd() <= i.getEnd()) {
                position.setSelected(recording.getSegment(annotations.indexOf(i)+1), 0, this);
            }
        }
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
        buildAnnotations();
    }

    private void buildAnnotations() {
        if(recording == null) {
            //TODO: Throw error
            return;
        }
        int lastIndex = 0;
        for(int i = 1; i <= recording.getSegments().size(); i++) {
            Segment s = recording.getSegments().get(i);
            String annotationString = "";
            if(s == null || s.getAnnotationFile().getString().trim().equals("")) {
                annotationString = DEFAULT_TEXT;
            } else {
                annotationString = s.getAnnotationFile().getString().trim();
            }
            IndexRange range;
            if(i == 1) {
                fullString = annotationString;
                range = new IndexRange(lastIndex, lastIndex + annotationString.length());
            } else {
                fullString = fullString + " " + annotationString;
                range = new IndexRange(lastIndex, lastIndex + annotationString.length());
            }
            lastIndex = lastIndex + annotationString.length()+1;
            annotations.add(range);
        }
        this.appendText(fullString);
        this.setWrapText(true);
        textProperty().addListener((observable, oldValue, newValue) -> {
            int diff = newValue.length() - oldValue.length();

            //Check if they have deleted a character before start
            if(diff == -1) {
                if(getCaretPosition() == activeRange.getEnd()) {
                    textUpdated = true;
                    this.insertText(getCaretPosition(), " ");
                    return;
                }
            }
            if(textUpdated) {
                textUpdated = false;
                return;
            }
            updateIndexRanges(activeRange.getStart(), diff);
        });
    }

    private void updateIndexRanges(int start, int diff) {
        boolean updating = false;
        for(int j = 0; j < annotations.size(); j++) {
            IndexRange i = annotations.get(j);
            if(i.getStart() == start) {
                updating = true;
                annotations.set(annotations.indexOf(i), new IndexRange(i.getStart(), i.getEnd()+diff));
                activeSegment.getAnnotationFile().setString(getText(annotations.get(j).getStart(),
                        annotations.get(j).getEnd()));
            } else if(updating) {
                annotations.set(annotations.indexOf(i), new IndexRange(i.getStart()+diff, i.getEnd()+diff));
            }
        }
    }

    private void setActiveSegment(Segment s) {
        activeSegment = s;
        activeRange = annotations.get(activeSegment.getSegmentNumber()-1);
    }

    @Override
    public void positionChanged(Segment segment, double frame, Object initiator) {
        if(segment != activeSegment) {
            setActiveSegment(segment);
            Platform.runLater(() -> {
                setColours();
            });
        }
    }

    public void setColours() {
        for(IndexRange i : annotations) {
            if (annotations.indexOf(i) == activeSegment.getSegmentNumber() - 1) {
                setStyleClass(i.getStart(), i.getEnd(), "orange");
            } else {
                if ((annotations.indexOf(i)+1) % 2 == 0) {
                    setStyleClass(i.getStart(), i.getEnd(), "grey");
                } else {
                    setStyleClass(i.getStart(), i.getEnd(), "white");
                }
            }
        }
    }
}
