package ui;

import audio_player.AudioPlayer;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import ui.text_box.Annotation;
import ui.text_box.AnnotationArea;
import ui.waveform.SelectableWaveformPane;
import ui.waveform.WaveformTime;
import undo.UndoRedoController;
import undo.UndoableAction;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class AudioEditor extends StackPane {
    /**
     * This is number of frames that act as a buffer zone so that
     * you can use the skip buttons to move forwards and backwards
     * between sections while the audio is playing.
     */
    long PREV_BUFFER = 9000;

    /**
     * A sorted set of all splits.
     */
    private Set<WaveformTime> splitTimes = new TreeSet<>();

    /**
     * All undoable actions should be performed on this. Must be set.
     */
    protected UndoRedoController undoRedoController = new UndoRedoController();

    /**
     * The enclosed waveform pane
     */
    SelectableWaveformPane waveformPane = new SelectableWaveformPane();

    /**
     * Cursor for current playback time
     */
    protected WaveformTime playPosition = new WaveformTime(waveformPane){{
        setStroke(Color.BLUE);
    }};

    /**
     * Used for playing audio files
     */
    private AudioPlayer audioPlayer;

    /**
     *
     */
    private AnnotationArea annotationArea;

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
            waveformPane.getCursorPosition().setFrame(frame, false);
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
        while (iterator.hasNext() && iterator.next().getFrame() < waveformPane.getCursorPosition().getFrame()) {
            i++;
        }
        return i;
    }

    public AudioEditor(AnnotationArea annotationArea) {
        super();
        this.getChildren().add(waveformPane);
        this.annotationArea = annotationArea;
        waveformPane.addWaveformTime(playPosition);

        waveformPane.getCursorPosition().addChangeListener(time ->
            annotationArea.setActiveSegment(getActiveSegment())
        );
    }

    public void play() {
        audioPlayer.play(waveformPane.getCursorPosition().getFrame());
    }

    public void pause() {
        audioPlayer.pause();
        waveformPane.getCursorPosition().setFrame(audioPlayer.getCurrentFrame());
    }

    public void stop() {
        audioPlayer.stop();
    }

    public void splitAudio() {
        long frame;
        if (audioPlayer.isPlaying()) {
            frame = audioPlayer.getCurrentFrame();
        } else {
            frame = waveformPane.getCursorPosition().getFrame();
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
        waveformPane.setAudioStream(audioFile);
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

    public void goToNextSection() {
        for (WaveformTime time : splitTimes) {
            if (time.getFrame() > playPosition.getFrame()) {
                jumpTopPosition(time.getFrame());
                break;
            }
        }
    }

    public void jumpTopPosition(long pos) {
        //Move audio player to position, needed if currently playing
        audioPlayer.goToSection(pos);
        //Move cursor to position, needed if currently paused
        waveformPane.getCursorPosition().setFrame(pos);
    }

    private class SplitAudioAction implements UndoableAction {

        private WaveformTime split;

        public SplitAudioAction(long frame) {
            split = new WaveformTime(waveformPane);
            split.setFrame(frame);
        }

        @Override
        public void doAction() {
            waveformPane.addWaveformTime(split);
            splitTimes.add(split);
            annotationArea.split();
        }

        @Override
        public void undoAction() {
            waveformPane.removeWaveformTime(split);
            splitTimes.remove(split);
        }

        public String toString() {
            return "Split at " + split;
        }
    }
}
