package audio_player;

import file_system.Recording;
import file_system.Segment;
import javafx.application.Platform;
import tat.Position;
import tat.PositionListener;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Tate on 1/04/2016.
 */
public class AudioPlayer implements PositionListener {

    private Position position;
    private Segment segment;

    public Clip clip;

    public Mixer mixer;
    private Timer positionListenerTimer;

    private final long AUDIO_POSITION_UPDATE_INTERVAL = 20;

    /**
     * Sets up an audio player to play the specified sound file.
     */
    public AudioPlayer(Position position) {
        this.position = position;
        position.addSelectedListener(this);
        mixer = AudioSystem.getMixer(AudioSystem.getMixerInfo()[0]);
    }

    private void setUpListenerEvents() {
        clip.addLineListener(event -> {
            //Give regular updates of position if started
            if (event.getType() == LineEvent.Type.START) {
                notifyPositionChanged();
                //Create new timer to notify position listeners
                positionListenerTimer = new Timer();
                positionListenerTimer.schedule(new TimerTask() {
                    public void run() {
                        Platform.runLater(() -> notifyPositionChanged());
                    }
                }, AUDIO_POSITION_UPDATE_INTERVAL, AUDIO_POSITION_UPDATE_INTERVAL);
            }
            //Stop giving regular updates of position if stopped
            else if (event.getType() == LineEvent.Type.STOP) {
                notifyPositionChanged();
                positionListenerTimer.cancel();
            }
        });
    }

    public void play() {
        clip.start();
    }

    private void notifyPositionChanged() {
        position.setSelected(segment, getCurrentFrame(), this);
    }

    public void pause() {
        clip.stop();
        notifyPositionChanged();
    }

    public void stop() {
        clip.stop();
        clip.setFramePosition(0);
        notifyPositionChanged();
    }

    public boolean isPlaying() {
        if (clip == null) {
            return false;
        }
        return clip.isRunning();
    }

    public int getCurrentFrame() {
        return clip.getFramePosition();
    }

    public int getEndFrame() {
        return clip.getFrameLength();
    }

    @Override
    public void positionChanged(Segment segment, int frame, Object initiator) {
        System.out.println("B");
        if (initiator != this) {
            boolean isPlaying = isPlaying();
            try {
                if (this.segment != segment) {
                    if (isPlaying) {
                        clip.stop();
                    }
                    loadSegment(segment);
                }
                clip.setFramePosition((int) frame);

                if (isPlaying) {
                    play();
                }
            } catch (IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadSegment(Segment segment) throws IOException, LineUnavailableException {
        closeOpenFiles();
        this.segment = segment;

        DataLine.Info dataInfo = new DataLine.Info(Clip.class, null);
        clip = (Clip) mixer.getLine(dataInfo);
        AudioInputStream audioStream = segment.getAudioFile().getStream();
        clip.open(audioStream);
        setUpListenerEvents();
    }

    public void closeOpenFiles() throws IOException {
        if (clip != null) {
            clip.close();
        }
    }
}
