package ui;

import audio_player.AudioPlayer;
import javafx.scene.paint.Color;
import ui.waveform.SelectableWaveformPane;
import ui.waveform.WaveSegment;
import undo.UndoRedoController;
import undo.UndoableAction;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 29/03/16.
 * GUI for splitting, joining and playing back audio
 */
public class AudioEditor extends SelectableWaveformPane {

    //All undoable actions should be performed on this. Must be set.
    protected UndoRedoController undoRedoController;

    protected WaveformTime playPosition = new WaveformTime(){{
        setStroke(Color.BLUE);
    }};

    private List<WaveformTime> splitTimes = new ArrayList<>();

    /**
     * The currently active segment. This is highlighted and corresponds to an Annotation.
     */
    private WaveSegment activeSegment;

    /**
     * This sets the currently active WaveSegment.
     */
    public void setActiveSegmentForTime(WaveformTime wf) {
        for(WaveSegment w : getSegments()) {
            if(w.contains(wf)) {
                activeSegment = w;
                return;
            }
        }
    }

    /**
     * Returns the currently active WaveSegment.
     */
    public WaveSegment getActiveSegment() {
        return activeSegment;
    }

    private AudioPlayer audioPlayer;

    /**
     * This is number of frames that act as a buffer zone so that
     * you can use the skip buttons to move forwards and backwards
     * between sections while the audio is playing.
     */
    private final int SKIP_OFFSET = 1000;

    public AudioEditor() {
        super();
        addWaveformTime(playPosition);

        //Add a listener to the cursor position to check if we have changed segment
        cursorPosition.addChangeListener(new WaveformTimeListener() {
            @Override
            public void onChange(WaveformTime time) {
                setActiveSegmentForTime(time);
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
            WaveformTime wf = new WaveformTime();
            wf.setFrame(frame);
            setActiveSegmentForTime(wf);
        });
    }

    public void goToPrevSection() {
        int closestMatch = 0;
        for (SelectableWaveformPane.WaveformTime t : splitTimes) {
            if(t.getFrame() < audioPlayer.getCurrentFrame() - SKIP_OFFSET && t.getFrame() > closestMatch) {
                closestMatch = (int)t.getFrame();
            }
        }
        audioPlayer.goToSection(closestMatch);
    }

    public void goToNextSection() {
        int closestMatch = (int)audioPlayer.getEndFrame();
        for (SelectableWaveformPane.WaveformTime t : splitTimes) {
            if(t.getFrame() > audioPlayer.getCurrentFrame() + SKIP_OFFSET && t.getFrame() < closestMatch) {
                closestMatch = (int)t.getFrame();
            }
        }
        audioPlayer.goToSection(closestMatch);
    }

    /**
     * Gets the segments in this wave as defined by the split positions
     */
    public List<WaveSegment> getSegments() {
        List<WaveSegment> segments = new ArrayList<>();

        //Create the start of the file
        WaveformTime start = new WaveformTime();
        start.setFrame(0);

        for(int i = 0; i < splitTimes.size(); i++) {
            segments.add(new WaveSegment(start, splitTimes.get(i)));
            start = splitTimes.get(i);
        }

        //Create the end of the file
        WaveformTime end = new WaveformTime();
        end.setFrame(audioPlayer.getEndFrame());

        //Get the last segment
        segments.add(new WaveSegment(start, end));
        return segments;
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

    public interface ActiveWaveSegmentListener {
        void onChange(WaveSegment active);
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
}
