package tat.view;

import file_system.Recording;
import file_system.Segment;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.StyledTextArea;
import tat.Position;
import tat.PositionListener;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Text;
import static java.awt.SystemColor.text;
import static javax.swing.Action.DEFAULT;
import static javax.swing.UIManager.get;


/**
 * Created by Tate on 23/07/2016.
 */
public class AnnotationDisplay extends StyleClassedTextArea implements PositionListener {

    private Recording recording;
    private tat.Position position;

    private Stack<TextState> states = new Stack();

    private Segment activeSegment;
    private IndexRange previousSegRange;
    private boolean textUpdated = false;
    private boolean initialised = false;

    /*private void textChanged() {
        if (textUpdated) {
            textUpdated = false;
            return;
        }

        int diff = fullString.length() - getText().length();
        updateIndexRanges(activeRange.getStart(), diff);
    }*/

    public AnnotationDisplay() {
        super();
        setPadding(new Insets(0,0,0,0));
        setWrapText(true);

        /*textProperty().addListener((obs, oldStr, newStr) -> {
            if(initialised) {
                validateTextChange(fullString, getText());
                textChanged();
            }
        });*/

        selectionProperty().addListener((observable) -> {
            if (initialised) {

                checkSelectEntireAnnotation();
                //validateTextChange(fullString, getText());

                //Check if you have switched segment
                if (getSelection().getLength() == 0) {
                    position.setSelected(getSegmentForSelection(getSelection()), 0, this);
                } else {
                    validateSelection();
                }
            }
        });

        focusedProperty().addListener((observable, oldVal, newVal) -> {
            if (initialised) {
                if (newVal) {
                    //TODO: Start blinking
                } else {
                    //TODO: Stop blinking
                }
            }
        });

        setShowCaret(CaretVisibility.ON);
    }

    private void validateSelection() {
        IndexRange newRange = new IndexRange(getSelection());

        //Modify invalid selections to be valid
        if (getSelection().getEnd() > getActiveAnnotation().range.getEnd()) {
            newRange = new IndexRange(newRange.getStart(), getActiveAnnotation().range.getEnd());
        }
        if (getSelection().getStart() < getActiveAnnotation().range.getStart()) {
            newRange = new IndexRange(getActiveAnnotation().range.getStart(), newRange.getEnd());
        }
        if(!newRange.equals(getSelection())) {
            selectRange(newRange.getStart(), newRange.getEnd());
        }
    }

    private void checkSelectEntireAnnotation() {
        //Check if you have selected entire string after selecting start position
        //this case only occurs when double clicking within a range, attempting to select
        //the entire annotation for a segment
        if (previousSegRange != null) {
            if (getSelection().getStart() == 0 && getSelection().getEnd() == getLength()) {
                position.setSelected(getSegmentForSelection(previousSegRange), 0, this);
                selectRange(previousSegRange.getStart(), previousSegRange.getEnd());
                previousSegRange = null;
                return;
            }
            previousSegRange = null;
        }

        //Just clicked a single place. This must be after previous check, otherwise this condition is
        //met when the previous check should be met.
        if (getSelection().getLength() == 0) {
            if (getSelection().getStart() == 0 && getSelection().getEnd() == 0) {
                //Set condition for selecting entire annotation
                previousSegRange = getActiveAnnotation().range;
            }
        }
    }

    public void setPosition(tat.Position position) {
        this.position = position;
        position.addSelectedListener(this);
    }

    /*

    private void validateTextChange(String oldStr, String newStr) {
        int diff = newStr.length() - oldStr.length();
        //CHECK ISEMPTY
        System.out.println("diff: " + diff);
        System.out.println("activeRangeEnd: " + activeRange.getEnd());
        System.out.println("caretPosition: " + getCaretPosition());

        //Check if they have deleted a character before start
        if (diff == -1) {
            if (getCaretPosition() == activeRange.getEnd()) {
                textUpdated = true;
                this.insertText(getCaretPosition(), " ");
                return;
            }
        }
    }
    private void updateIndexRanges(int start, int diff) {
        boolean updating = false;
        fullString = "";
        //Loop through all annotations
        for (int annotationIndex = 0; annotationIndex < annotations.size(); annotationIndex++) {
            IndexRange indexRange = annotations.get(annotationIndex);
            //Start of the annotation matches the start of the changed area
            if(indexRange.getStart() == start) {
                //Update annotation text to match modified text
                updating = true;
                annotations.set(annotations.indexOf(indexRange), new IndexRange(indexRange.getStart(), indexRange.getEnd()+diff));
                activeSegment.getAnnotationFile().setString(getText(annotations.get(annotationIndex).getStart(),
                        annotations.get(annotationIndex).getEnd()));
            } else if(updating) {
                annotations.set(annotations.indexOf(indexRange), new IndexRange(indexRange.getStart()+diff, indexRange.getEnd()+diff));
            }
            String next = recording.getSegment(annotationIndex + 1).getAnnotationFile().getString().trim();
            if(annotationIndex == 0) {
                fullString = next;
            } else {
                fullString = fullString + " " + next;
            }
        }
    }*/

    public void setRecording(Recording recording) {
        this.recording = recording;
        updateState(new TextState(recording));
        initialised = true;
    }

    private void updateState(TextState state) {
        states.push(state);

        clear();
        this.appendText(state.fullString);
    }



    private void setActiveSegment(Segment s) {
        activeSegment = s;
    }

    @Override
    public void positionChanged(Segment segment, int frame, Object initiator) {
        if(segment != activeSegment) {
            setActiveSegment(segment);
            if(initiator != this) {
                selectRange(getActiveAnnotation().range.getStart(), getActiveAnnotation().range.getStart());//Moves caret to start of selected annotation
            }
            setColours();
        }
    }

    public void setColours() {
        for(Annotation a : states.peek().annotations) {
            if (a.segment == activeSegment) {
                setStyleClass(a.range.getStart(), a.range.getEnd(), "orange");
            } else {
                if (a.segment.getSegmentNumber() % 2 == 0) {
                    setStyleClass(a.range.getStart(), a.range.getEnd(), "grey");
                } else {
                    setStyleClass(a.range.getStart(), a.range.getEnd(), "white");
                }
            }
        }
    }

    public int getCursorPosInCurrentSegment() {
        return getSelection().getStart() - getAnnotationForSelection(getSelection()).range.getStart();
    }

    private Annotation getAnnotationForSelection(IndexRange selection) {
        for(Annotation a: states.peek().annotations) {
            if(isWithinRange(selection, a.range))
                return a;
        }
        return null;
    }

    private Annotation getActiveAnnotation() {
        for(Annotation a: states.peek().annotations) {
            if(a.segment == activeSegment) {
                return a;
            }
        }
        return null;
    }

    private boolean isWithinRange(IndexRange innerRange, IndexRange outerRange) {
        return innerRange.getStart() >= outerRange.getStart() &&
                innerRange.getEnd() <= outerRange.getEnd();
    }

    private Segment getSegmentForSelection(IndexRange selection) {
        for(Annotation a: states.peek().annotations) {
            if(isWithinRange(selection, a.range)) {
                return a.segment;
            }
        }
        return null;
    }

    //CASES:
        /*
        TextState:
            currentText - fullString
            indexRange[]
            selection

            User Typed Valid Text within Range
            User pushed backspace
                Valid: Inside range, not at start
                Invalid: At start of range
                       sets prevUndoAction to top of undo stack or whatever unless prevUndoAction is !null
            User pushed delete
                Valid: Inside range, not at end
                Invalid: At end of range
                        sets prevUndoAction to top of undo stack or whatever unless prevUndoAction is !null
            Enter:
                Consume
            Cut:
                Same as delete
            Drag:
                Consume
            Selections:
                ValidateSelections
                Deleted selected text:
            Insert:
                Out of scope


             Ctrl + Z:
                Consume undo redo and implement own
             Ctrl + Y:
                Consume, implement own redo and undo

         */
    private class TextState {

        public final List<Annotation> annotations = new ArrayList<>();

        public final String fullString;

        protected TextState(Recording r) {
            String intermediateString = "";
            int index = 0;
            for(Segment s : r) {
                Annotation a = new Annotation(s, index);
                if(index==0) {
                    intermediateString = a.getText();
                } else {
                    intermediateString = intermediateString + " " + a.getText();
                }
                index = a.range.getEnd()+1;//FOR THE SPACE
                annotations.add(a);
            }
            fullString = intermediateString;
        }

    }

    private class Annotation {

        private final String DEFAULT_TEXT = "Annotation Missing Please Add Annotation";

        public final IndexRange range;
        public final Segment segment;
        public final boolean isEmpty;

        protected Annotation(Segment s, int start) {
            segment = s;
            String annotationString = s.getAnnotationFile().getString().trim();
            if(annotationString.equals("")) {
                annotationString = DEFAULT_TEXT;
                isEmpty = true;
            } else {
                isEmpty = false;
            }
            range = new IndexRange(start, start + annotationString.length());
        }

        public String getText() {
            if(isEmpty)
                return DEFAULT_TEXT;
            return segment.getAnnotationFile().getString().trim();
        }

    }
}

