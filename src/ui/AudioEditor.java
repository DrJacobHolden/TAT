package ui;

import audio_player.AudioPlayer;
import javafx.scene.paint.Color;
import ui.waveform.SelectableWaveformPane;
import undo.UndoRedoController;
import undo.UndoableAction;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by max on 29/03/16.
 * GUI for splitting, joining and playing back audio
 */
public class AudioEditor extends SelectableWaveformPane {

    /**
     * This is number of frames that act as a buffer zone so that
     * you can use the skip buttons to move forwards and backwards
     * between sections while the audio is playing.
     */
    long PREV_BUFFER = 6000;

    //All undoable actions should be performed on this. Must be set.
    protected UndoRedoController undoRedoController;

    protected WaveformTime playPosition = new WaveformTime(){{
        setStroke(Color.BLUE);
    }};

    private Set<WaveformTime> splitTimes = new TreeSet<>();

    /**
     * Called to update the active segment to reflect the annotation selection
     */
    public void syncActiveSegment(int pos) {
        if (pos < splitTimes.size()) {
            Iterator<WaveformTime> time = splitTimes.iterator();
            long frame = 0;
            for (int i = 0; i < pos; i++) {
                frame = time.next().getFrame();
            }
            cursorPosition.setFrame(frame, false);
        } else {
            //TODO: Notify user
        }
    }

    /**
     * Returns the currently active WaveSegment.
     */
    public int getActiveSegment() {
        int i = 0;
        Iterator<WaveformTime> iterator = splitTimes.iterator();
        while (iterator.hasNext() && iterator.next().getFrame() < cursorPosition.getFrame()) {
            i++;
        }
        return i;
    }

    private AudioPlayer audioPlayer;

    public AudioEditor() {
        super();
        addWaveformTime(playPosition);

        //Add a listener to the cursor position to check if we have changed segment
        cursorPosition.addChangeListener(new WaveformTimeListener() {
            @Override
            public void onChange(WaveformTime time) {
                notifyChange();
            }
        });
    }

    public void setUndoRedoController(UndoRedoController c) {
        undoRedoController = c;
    }

    public void play() {
        audioPlayer.play(cursorPosition.getFrame());
    }

    public void pause() {
        audioPlayer.pause();
        cursorPosition.setFrame(audioPlayer.getCurrentFrame());
    }

    public void stop() {
        audioPlayer.stop();
    }

    public void splitAudio() {
        long frame;
        if (audioPlayer.isPlaying()) {
            frame = audioPlayer.getCurrentFrame();
        } else {
            frame = cursorPosition.getFrame();
        }

        //Prevent duplicate splits
        for(WaveformTime wf : splitTimes) {
            if(wf.getFrame() == frame)
                return;
        }

        SplitAudioAction split = new SplitAudioAction(frame);
        undoRedoController.performAction(split);
    }

    public void setAudioFile(File audioFile) throws IOException, UnsupportedAudioFileException {
        setAudioStream(audioFile);
        audioPlayer = new AudioPlayer(audioFile);

        audioPlayer.addAudioPositionListener(frame -> {
            playPosition.setFrame(frame);
        });
    }

    public void goToPrevSection() {
        long currentTime = 0;
        for (WaveformTime time : splitTimes) {
            if (time.getFrame() < playPosition.getFrame() - PREV_BUFFER) {
                currentTime = time.getFrame();
            } else {
                break;
            }
        }
        jumpTopPosition(currentTime);
    }

    public void jumpTopPosition(long pos) {
        //Move audio player to position, needed if currently playing
        audioPlayer.goToSection(pos);
        //Move cursor to position, needed if currently paused
        cursorPosition.setFrame(pos);
    }

    public void goToNextSection() {
        for (WaveformTime time : splitTimes) {
            if (time.getFrame() > playPosition.getFrame()) {
                jumpTopPosition(time.getFrame());
                break;
            }
        }
    }

    private class SplitAudioAction implements UndoableAction {

        private SelectableWaveformPane.WaveformTime split;

        public SplitAudioAction(long frame) {
            split = new SelectableWaveformPane.WaveformTime();
            split.setFrame(frame);
        }

        @Override
        public void doAction() {
            addWaveformTime(split);
            splitTimes.add(split);
        }

        @Override
        public void undoAction() {
            removeWaveformTime(split);
            splitTimes.remove(split);
        }

        public String toString() {
            return "Split at " + split;
        }
    }

    protected List<ActiveWaveSegmentListener> changeListeners = new ArrayList<>();

    protected void notifyChange() {
        for (ActiveWaveSegmentListener listener : changeListeners) {
            listener.onChange(getActiveSegment());
        }
    }

    public void addChangeListener(ActiveWaveSegmentListener listener) {
        changeListeners.add(listener);
    }

    public interface ActiveWaveSegmentListener {
        void onChange(int segIndex);
    }
}
