package tat.ui.element.annotation;

import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import tat.corpus.Recording;
import tat.corpus.Segment;
import tat.ui.PositionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyCombination.SHIFT_ANY;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;


/**
 * Created by Tate on 23/07/2016.
 */
public class AnnotationDisplay extends StyleClassedTextArea implements PositionListener {

    public static final String DEFAULT_TEXT = "Annotation Missing Please Add Annotation";
    private final Clipboard clipboard = Clipboard.getSystemClipboard();
    private tat.ui.Position position;
    private Stack<TextState> undoStates = new Stack<>();
    private Stack<TextState> redoStates = new Stack<>();
    private TextState currentState;

    private IndexRange previousSegRange;
    private boolean initialised = false;
    private boolean updatingText = false;
    private boolean firstSelect = true;
    private Recording recording;

    public AnnotationDisplay() {
        super();
        setPadding(new Insets(0, 0, 0, 0));
        setWrapText(true);

        setInputBindings();

        selectionProperty().addListener((observable) -> {
            if (initialised && !updatingText && getText().length() > 0) {
                checkSelectEntireAnnotation();

                IndexRange currentSelection = getSelection();
                Segment segmentForSelection = getSegmentForSelection(currentSelection);

                //Check if you have switched segment
                if (currentSelection.getLength() == 0 && segmentForSelection != position.getSegment()) {
                    position.setSelected(segmentForSelection, 0, this);
                } else {
                    validateSelection();
                }

                //Select whole segment if segment contains default text AND we are not in part of a triple click
                // (or else the first segment will be selected).
                if (getActiveAnnotation().isEmpty() && previousSegRange == null) {
                    selectRange(getActiveAnnotation().range.getStart(), getActiveAnnotation().range.getEnd());
                }
            }
        });

        setShowCaret(CaretVisibility.ON);
    }

    public void setContextMenu(ContextMenu c) {
        //TODO: Make this work better. It does not go away yet.
        setOnContextMenuRequested((a) -> {
            c.show(this, a.getScreenX(), a.getScreenY());
        });
    }

    private void setInputBindings() {
        //Handle regular key typed
        Nodes.addInputMap(this, InputMap.consume((KeyEvent.KEY_TYPED), e -> typedCharacter(e.getCharacter())));
        //Handle backspace
        Nodes.addInputMap(this, InputMap.consume(keyPressed(BACK_SPACE), e -> backspaceCharacter()));
        //Handle delete
        Nodes.addInputMap(this, InputMap.consume(keyPressed(DELETE), e -> deletedCharacter()));

        //Handle undo
        Nodes.addInputMap(this, InputMap.consume(keyPressed(Z, SHORTCUT_DOWN), e -> undo()));
        //Handle redo
        Nodes.addInputMap(this, InputMap.consume(keyPressed(Y, SHORTCUT_DOWN), e -> redo()));

        //Handle tab
        Nodes.addInputMap(this, InputMap.consume(keyPressed(TAB), e -> maybeMoveCursorToSegmentOffset(+1, true)));


        //Allow moving segments when entire segment is selected when pressing left or right
        Nodes.addInputMap(this, InputMap.consumeWhen(keyPressed(LEFT), this::entireAnnotationSelected,
                e -> maybeMoveCursorToSegmentOffset(-1, false)));
        Nodes.addInputMap(this, InputMap.consumeWhen(keyPressed(RIGHT), this::entireAnnotationSelected,
                e -> maybeMoveCursorToSegmentOffset(+1, false)));

        //Disable enter, shift or no shift
        Nodes.addInputMap(this, InputMap.consume(keyPressed(ENTER, SHIFT_ANY)));
        //Do not allow dragging text
        setOnSelectionDrop(a -> {
        });
    }

    private boolean entireAnnotationSelected() {
        return getActiveAnnotation().range.equals(getSelection());
    }

    /**
     * Move the cursor to either the next segment, or previous, specified by offset
     */
    private void maybeMoveCursorToSegmentOffset(int offset, boolean loopAround) {
        int segmentNum = getActiveAnnotation().segment.getSegmentNumber() + offset;
        if (loopAround) {
            segmentNum = ((segmentNum - 1) % currentState.annotations.size()) + 1;
        }

        Segment nextSegment = recording.getSegment(segmentNum);
        if (nextSegment != null) {
            position.setSelected(nextSegment, 0, nextSegment);
        }
    }

    public void undo() {
        if (!undoStates.empty()) {
            redoStates.push(currentState);
            setState(undoStates.pop());
            selectRange(currentState.selection.getStart(), currentState.selection.getEnd());
        }
    }

    public void redo() {
        if (!redoStates.empty()) {
            undoStates.push(currentState);
            setState(redoStates.pop());
            selectRange(currentState.selection.getStart(), currentState.selection.getEnd());
        }
    }

    @Override
    public void selectRange(int anchor, int endPos) {
        //Only set length of 0
        currentState.selection = new IndexRange(anchor, anchor);
        super.selectRange(anchor, endPos);
    }

    @Override
    public void paste() {
        Object data = clipboard.getContent(DataFormat.PLAIN_TEXT);
        if (data instanceof String) {
            //Filter newline characters
            String str = ((String) data).replaceAll("[\n\r]", "");
            insertTextAtCurrentPosition(str);
        }
    }

    @Override
    public void cut() {
        copy();
        backspaceCharacter();
    }

    @Override
    public void copy() {
        IndexRange selection = getSelection();
        String content = getText().substring(selection.getStart(), selection.getEnd());
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        clipboard.setContent(clipboardContent);
    }

    private void insertTextAtCurrentPosition(String text) {
        StringBuffer newText = new StringBuffer(getActiveAnnotation().text);
        IndexRange selection = getSelection();
        IndexRange localSelection = getLocalSelection();

        newText.replace(localSelection.getStart(), localSelection.getEnd(), text);
        updateState(new TextState(currentState, getActiveAnnotation(), newText.toString()));

        selectRange(selection.getStart() + text.length(), selection.getStart() + text.length());
    }

    private void typedCharacter(String character) {
        //Do not register non printable characters
        if (character.charAt(0) < 32 || character.charAt(0) > 126)
            return;
        insertTextAtCurrentPosition(character);
    }

    private void backspaceCharacter() {

        if (getSelection().getLength() == 0 &&
                (getSelection().getStart() == getActiveAnnotation().range.getStart())) {
            return;
        }

        StringBuffer newText = new StringBuffer(getActiveAnnotation().text);

        IndexRange selection = getSelection();
        IndexRange localSelection = getLocalSelection();

        boolean single = localSelection.getLength() == 0;

        if (single) {
            localSelection = new IndexRange(localSelection.getStart() - 1, localSelection.getEnd());
        }

        newText.replace(localSelection.getStart(), localSelection.getEnd(), "");
        updateState(new TextState(currentState, getActiveAnnotation(), newText.toString()));

        if (single) {
            selectRange(selection.getStart() - 1, selection.getStart() - 1);
        } else {
            selectRange(selection.getStart(), selection.getStart());
        }
    }

    private void deletedCharacter() {
        if (getSelection().getLength() == 0 &&
                (getSelection().getEnd() == getActiveAnnotation().range.getEnd())) {
            return;
        }

        StringBuffer newText = new StringBuffer(getActiveAnnotation().text);

        IndexRange selection = getSelection();
        IndexRange localSelection = getLocalSelection();

        if (localSelection.getLength() == 0) {
            localSelection = new IndexRange(localSelection.getStart(), localSelection.getEnd() + 1);
        }

        newText.replace(localSelection.getStart(), localSelection.getEnd(), "");
        updateState(new TextState(currentState, getActiveAnnotation(), newText.toString()));

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
        if (!newRange.equals(getSelection())) {
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

    public void initialise(Recording recording, tat.ui.Position position) {
        undoStates.empty();
        redoStates.empty();

        this.recording = recording;
        this.position = position;
        position.addSelectedListener(this);

        setState(new TextState(recording));
        initialised = true;
        firstSelect = true;
    }

    private void updateState(TextState state) {
        undoStates.push(currentState);
        redoStates.clear();
        setState(state);
    }

    private void setState(TextState state) {
        this.currentState = state;
        state.updateRecording();

        updatingText = true;
        this.replaceText("");
        this.appendText(state.fullString);
        updatingText = false;

        //Still better than Skype - Potentially causes performance issues - Could be removed if someone
        //discovered a potential off by one error or something weird that may or may not exist.
        setColours();
    }

    @Override
    public void positionChanged(Segment segment, int frame, Object initiator) {
        //We should always update our position in the text if this is the first positionChanged we recieve
        if ((initiator != this && segment != getSegmentForSelection(getSelection())) || firstSelect) {
            firstSelect = false;

            Annotation active = getActiveAnnotation();
            selectRange(active.range.getStart(), active.isEmpty() ? active.range.getEnd() : active.range.getStart());
        }
        setColours();
    }

    public void setColours() {
        for (Annotation a : currentState.annotations) {
            if (a.segment == position.getSegment()) {
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
        for (Annotation a : currentState.annotations) {
            if (isWithinRange(selection, a.range))
                return a;
        }
        return null;
    }

    private Annotation getActiveAnnotation() {
        for (Annotation a : currentState.annotations) {
            if (a.segment == position.getSegment()) {
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
        for (Annotation a : currentState.annotations) {
            if (isWithinRange(selection, a.range)) {
                return a.segment;
            }
        }
        return null;
    }

    private class TextState {

        public final List<Annotation> annotations = new ArrayList<>();
        public final String fullString;
        //Need to update this
        public IndexRange selection;

        protected TextState(Recording r) {
            int index = 0;
            for (Segment s : r) {
                Annotation a = new Annotation(s, index);
                index = a.range.getEnd() + 1;//FOR THE SPACE
                annotations.add(a);
            }
            fullString = generateFullString();
            selection = new IndexRange(0, 0);
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
                    //In case text is empty string
                    diff = updatedAnnotation.text.length() - changedAnnotation.text.length();
                } else if (start) {
                    updatedAnnotation = new Annotation(a, a.range.getStart() + diff, a.text);
                }
                annotations.add(updatedAnnotation);
            }
            fullString = generateFullString();
            selection = new IndexRange(0, 0);
        }

        private String generateFullString() {
            String intermediateString = "";
            for (Annotation a : annotations) {
                intermediateString = intermediateString + " " + a.text;
            }
            //Remove first space
            return intermediateString.replaceAll("^ ", "");
        }

        private void updateRecording() {
            annotations.forEach(Annotation::updateSegment);
        }
    }


    private class Annotation {
        public final IndexRange range;
        public final Segment segment;
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
            } else {
            }

            this.text = text;
            range = new IndexRange(start, start + text.length());
        }

        private boolean isEmpty() {
            return text.equals(DEFAULT_TEXT);
        }

        private void updateSegment() {
            segment.getAnnotationFile().setString(text);
        }
    }
}

