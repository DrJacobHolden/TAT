package ui;

import audio_player.AudioPlayer;
import javafx.scene.paint.Color;
import ui.icon.Icon;
import ui.icon.IconLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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

    private boolean playing = false;

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

    @Override
    protected void addToolbars() {
        super.addToolbars();
        addPlayControlToolbar();
        addSplitToolbar();
    }

    /**
     * Add controls for manipulating audio playback
     */
    private void addPlayControlToolbar() {
        ToolBar sep = new ToolBar();
        sep.setMaxHeight(43);
        sep.setMinHeight(43);
        HBox.setHgrow(sep, Priority.ALWAYS);
        Button prevButton = new Button("", new Icon(IconLoader.getInstance().prevIcon));
        prevButton.setOnAction(event -> goToPrevSection());
        Button playButton = new Button("", new Icon(IconLoader.getInstance().playIcon));
        playButton.setOnAction(event -> play());
        Button pauseButton = new Button("", new Icon(IconLoader.getInstance().pauseIcon));
        pauseButton.setOnAction(event -> pause());
        Button stopButton = new Button("", new Icon(IconLoader.getInstance().stopIcon));
        stopButton.setOnAction(event -> stop());
        Button nextButton = new Button("", new Icon(IconLoader.getInstance().nextIcon));
        nextButton.setOnAction(event -> goToNextSection());
        ToolBar playControlToolbar = new ToolBar(
                prevButton,
                playButton,
                pauseButton,
                stopButton,
                nextButton
        );
        toolbars.getChildren().addAll(sep, playControlToolbar);
    }

    private void play() {
        audioPlayer.play(cursorPosition.getFrame());
        playing = true;
    }
    private void pause() {
        audioPlayer.pause();
        playing = false;
    }
    private void stop() {
        audioPlayer.stop();
        playing = false;
    }

    private void goToPrevSection() {
        int closestMatch = 0;
        for (WaveformTime t : splitTimes) {
            if(t.getFrame() < audioPlayer.getCurrentFrame() - SKIP_OFFSET && t.getFrame() > closestMatch) {
                closestMatch = (int)t.getFrame();
            }
        }
        audioPlayer.goToSection(closestMatch);
    }

    private void goToNextSection() {
        int closestMatch = (int)audioPlayer.getEndFrame();
        for (WaveformTime t : splitTimes) {
            if(t.getFrame() > audioPlayer.getCurrentFrame() + SKIP_OFFSET && t.getFrame() < closestMatch) {
                closestMatch = (int)t.getFrame();
            }
        }
        audioPlayer.goToSection(closestMatch);
    }

    private void splitAudio() {
        WaveformTime split = new WaveformTime();
        split.setFrame(cursorPosition.getFrame());
        addWaveformTime(split);
        splitTimes.add(split);
    }

    /**
     * Add controls for splitting and joining audio
     */
    private void addSplitToolbar() {
        ToolBar sep = new ToolBar();
        sep.setMaxHeight(43);
        sep.setMinHeight(43);
        Button splitButton = new Button("", new Icon(IconLoader.getInstance().splitIcon));
        Button joinButton = new Button("", new Icon(IconLoader.getInstance().joinIcon));
        ToolBar splitToolbar = new ToolBar(
                splitButton,
                joinButton
        );

        splitButton.setOnAction(e -> splitAudio());

        HBox.setHgrow(sep, Priority.ALWAYS);
        toolbars.getChildren().addAll(sep, splitToolbar);
    }

    public void setAudioFile(File audioFile) throws IOException, UnsupportedAudioFileException {
        setAudioStream(audioFile);
        audioPlayer = new AudioPlayer(audioFile);

        audioPlayer.addAudioPositionListener(frame -> {
            playPosition.setFrame(frame);
        });
    }
}
