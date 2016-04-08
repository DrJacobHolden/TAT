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
import java.util.*;

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

    private Set<WaveformTime> splitTimes = new TreeSet<>();

    /**
     * Called to update the active segment to reflect the annotation selection
     */
    public void syncActiveSegment(int pos) {
        Iterator<WaveformTime> time = splitTimes.iterator();
        long frame = 0;
        for (int i=0; i<pos-1; i++){
            frame = time.next().getFrame();
        }
        cursorPosition.setFrame(frame);
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

    /**
     * This is number of frames that act as a buffer zone so that
     * you can use the skip buttons to move forwards and backwards
     * between sections while the audio is playing.
     */
    private final int SKIP_OFFSET = 1000;

    public AudioEditor() {
        super();
        addWaveformTime(playPosition);

//        //Add a listener to the cursor position to check if we have changed segment
//        cursorPosition.addChangeListener(new WaveformTimeListener() {
//            @Override
//            public void onChange(WaveformTime time) {
//                setActiveSegmentForTime(time);
//            }
//        });
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
        void onChange(int segIndex);
    }
}
