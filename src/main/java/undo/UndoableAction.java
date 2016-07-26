package undo;

/**
 * Created by kalda on 8/04/2016.
 */
public interface UndoableAction {
    public void doAction();
    public void undoAction();
    public String toString();
}
