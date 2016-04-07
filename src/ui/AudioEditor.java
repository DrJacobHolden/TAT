package ui;

import audio_player.AudioPlayer;
import javafx.scene.paint.Color;
import ui.waveform.SelectableWaveformPane;

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

    protected WaveformTime playPosition = new WaveformTime(){{
        setStroke(Color.BLUE);
    }};

    private List<WaveformTime> splitTimes = new ArrayList<>();
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
        SelectableWaveformPane.WaveformTime split = new SelectableWaveformPane.WaveformTime();
        if (audioPlayer.isPlaying()) {
            split.setFrame(audioPlayer.getCurrentFrame());
        } else {
            split.setFrame(cursorPosition.getFrame());
        }
        addWaveformTime(split);
        splitTimes.add(split);
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
}
