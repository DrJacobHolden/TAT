package undo;

import java.util.Stack;

/**
 * Created by Max Lay on 8/04/2016.
 */
public class UndoRedoController {
    private Stack<UndoableAction> undoStack = new Stack<>();
    private Stack<UndoableAction> redoStack = new Stack<>();

    public UndoableAction getNextUndo() {
        if (undoStack.empty()) {
            return null;
        } else {
            return undoStack.peek();
        }
    }

    public UndoableAction undo() {
        UndoableAction action = null;
        if (!undoStack.empty()) {
             action = undoStack.pop();
            action.undoAction();
            redoStack.push(action);
        }
        return action;
    }

    public UndoableAction getNextRedo() {
        if (redoStack.empty()) {
            return null;
        } else {
            return redoStack.peek();
        }
    }

    public UndoableAction redo() {
        UndoableAction action = null;
        if (!redoStack.empty()) {
            action = redoStack.pop();
            action.doAction();
            undoStack.push(action);
        }
        return action;
    }

    public void performAction(UndoableAction action) {
        action.doAction();
        undoStack.push(action);
        redoStack.clear();
    }
}
