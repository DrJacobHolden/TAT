package tat.view;

import file_system.Recording;
import file_system.Segment;
import javafx.geometry.Insets;
import javafx.scene.control.IndexRange;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import tat.PositionListener;

import java.util.*;

import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyCombination.SHIFT_ANY;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;
import static org.fxmisc.wellbehaved.event.EventPattern.keyReleased;
import static org.fxmisc.wellbehaved.event.EventPattern.keyTyped;


/**
 * Created by Tate on 23/07/2016.
 */
public class AnnotationDisplay extends StyleClassedTextArea implements PositionListener {

    private Recording recording;
    private tat.Position position;

    private Stack<TextState> states = new Stack();

    private Segment activeSegment;
    private IndexRange previousSegRange;
    private boolean initialised = false;

    public AnnotationDisplay() {
        super();
        setPadding(new Insets(0,0,0,0));
        setWrapText(true);

        Nodes.addInputMap(this, InputMap.consume((KeyEvent.KEY_TYPED), e -> typedCharacter(e.getCharacter())));

        Nodes.addInputMap(this,
                InputMap.consume(keyPressed(Z, SHORTCUT_DOWN)));

        Nodes.addInputMap(this,
                InputMap.consume(keyPressed(Y, SHORTCUT_DOWN)));


        Nodes.addInputMap(this, InputMap.consume(keyPressed(BACK_SPACE), e -> backspaceCharacter()));


        Nodes.addInputMap(this, InputMap.consume(keyPressed(DELETE), e -> deletedCharacter()));

        Nodes.addInputMap(this,
                InputMap.consume(keyPressed(ENTER, SHIFT_ANY)));

        selectionProperty().addListener((observable) -> {
            if (initialised && getText().length() > 0) {

                checkSelectEntireAnnotation();

                if(getSegmentForSelection(getSelection()) != null) {
                    //Check if you have switched segment
                    if (getSelection().getLength() == 0) {
                        position.setSelected(getSegmentForSelection(getSelection()), 0, this);
                    } else {
                        validateSelection();
                    }
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

    private void typedCharacter(String character) {

        if(character.charAt(0) < 32 || character.charAt(0) > 126)
            return;

        StringBuffer newText = new StringBuffer(getActiveAnnotation().text);

        IndexRange selection = getSelection();
        IndexRange localSelection = getLocalSelection();

        newText.replace(localSelection.getStart(), localSelection.getEnd(), character);
        updateState(new TextState(states.peek(), getActiveAnnotation(), newText.toString()));

        selectRange(selection.getStart() + 1, selection.getStart() + 1);
    }

    private void backspaceCharacter() {

        if(getSelection().getLength() == 0 &&
                (getSelection().getStart() == getActiveAnnotation().range.getStart())) {
            return;
        }

        StringBuffer newText = new StringBuffer(getActiveAnnotation().text);

        IndexRange selection = getSelection();
        IndexRange localSelection = getLocalSelection();

        boolean single = localSelection.getLength() == 0;

        if(single) {
            localSelection = new IndexRange(localSelection.getStart()-1, localSelection.getEnd());
        }

        newText.replace(localSelection.getStart(), localSelection.getEnd(), "");
        updateState(new TextState(states.peek(), getActiveAnnotation(), newText.toString()));

        if(single) {
            selectRange(selection.getStart() - 1, selection.getStart() - 1);
        } else {
            selectRange(selection.getStart(), selection.getStart());
        }
    }

    private void deletedCharacter() {
        if(getSelection().getLength() == 0 &&
                (getSelection().getEnd() == getActiveAnnotation().range.getEnd())) {
            return;
        }

        StringBuffer newText = new StringBuffer(getActiveAnnotation().text);

        IndexRange selection = getSelection();
        IndexRange localSelection = getLocalSelection();

        if(localSelection.getLength() == 0) {
            localSelection = new IndexRange(localSelection.getStart(), localSelection.getEnd()+1);
        }

        newText.replace(localSelection.getStart(), localSelection.getEnd(), "");
        updateState(new TextState(states.peek(), getActiveAnnotation(), newText.toString()));

        selectRange(selection.getStart(), selection.getStart());
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

    public void setRecording(Recording recording) {
        this.recording = recording;
        updateState(new TextState(recording));
        initialised = true;
    }

    private void updateState(TextState state) {
        states.push(state);

        this.replaceText("");
        this.appendText(state.fullString);

        setColours();//Still better than Skype - Potentially causes performance issues - Could be removed if someone
        //discovered a potential off by one error or something weird that may or may not exist.
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

    private IndexRange getLocalSelection() {
        int activeRangeStart = getActiveAnnotation().range.getStart();
        IndexRange selection = getSelection();
        return new IndexRange(selection.getStart() - activeRangeStart, selection.getEnd() - activeRangeStart);
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
            User pushed backspace (/)
                Valid: Inside range, not at start
                Invalid: At start of range
                       sets prevUndoAction to top of undo stack or whatever unless prevUndoAction is !null
            User pushed delete (/)
                Valid: Inside range, not at end
                Invalid: At end of range
                        sets prevUndoAction to top of undo stack or whatever unless prevUndoAction is !null
            Enter:
                Consume (/)
            Cut:
                Same as delete
            Drag:
                Consume
            Selections:
                ValidateSelections (/)
                Deleted selected text:
            Insert:
                Out of scope (/)


             Ctrl + Z:
                Consume (/), undo redo and implement own
             Ctrl + Y:
                Consume (/), implement own redo and undo

         */
    private class TextState {

        public final List<Annotation> annotations = new ArrayList<>();

        public final String fullString;

        protected TextState(Recording r) {
            int index = 0;
            for (Segment s : r) {
                Annotation a = new Annotation(s, index);
                index = a.range.getEnd() + 1;//FOR THE SPACE
                annotations.add(a);
            }
            fullString = generateFullString();
        }

        protected TextState(TextState old, Annotation changedAnnotation, String text) {
            boolean start = false;
            int diff = 0;
            Annotation updatedAnnotation;
            for (Annotation a : old.annotations) {
                updatedAnnotation = a;
                if (a == changedAnnotation) {
                    start = true;
                    updatedAnnotation = new Annotation(a, a.range.getStart(), text);
                    //Incase text is empty string
                    diff = updatedAnnotation.text.length() - changedAnnotation.text.length();
                } else if (start) {
                    updatedAnnotation = new Annotation(a, a.range.getStart() + diff, a.text);
                }
                annotations.add(updatedAnnotation);
            }
            fullString = generateFullString();
        }

        private String generateFullString() {
            String intermediateString = "";
            for(Annotation a : annotations) {
                intermediateString = intermediateString + " " + a.text;
            }
            return intermediateString.trim();
        }
    }


    private class Annotation {

        private final String DEFAULT_TEXT = "Annotation Missing Please Add Annotation";

        public final IndexRange range;
        public final Segment segment;
        public final boolean isEmpty;
        public final String text;

        protected Annotation(Segment s, int start) {
            this(start, s, s.getAnnotationFile().getString().trim());
        }

        protected Annotation(Annotation a, int start, String annotation) {
            this(start, a.segment, annotation);
        }

        private Annotation(int start, Segment segment, String text) {
            this.segment = segment;

            if (text.equals("")) {
                text = DEFAULT_TEXT;
                isEmpty = true;
            } else {
                isEmpty = false;
            }

            this.text = text;
            range = new IndexRange(start, start+text.length());
        }
    }
}

