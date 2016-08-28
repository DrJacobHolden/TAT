package audio_player;

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

    private Clip clip;
    private Timer positionListenerTimer;

    private int stopPosition = 0;
    private boolean paused = false;

    private final long AUDIO_POSITION_UPDATE_INTERVAL = 20;

    /**
     * Sets up an audio player to play the specified sound file.
     */
    public AudioPlayer(Position position) {
        this.position = position;
        DataLine.Info dataInfo = new DataLine.Info(Clip.class, null);
        Mixer mixer = AudioSystem.getMixer(AudioSystem.getMixerInfo()[0]);
        try {
            clip = (Clip) mixer.getLine(dataInfo);
            setUpListenerEvents();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        position.addSelectedListener(this);
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
                if (!paused) {
                    positionChanged(segment, stopPosition, null);
                    Platform.runLater(() -> notifyPositionChanged());
                }
                positionListenerTimer.cancel();
            }
        });
    }

    public void play() {
        paused = false;
        //This is the case after saving
        if (!clip.isOpen()) {
            try {
                clip.open(segment.getAudioFile().getAudioStream());
                clip.setFramePosition(position.getFrame());
            } catch (LineUnavailableException | IOException e) {
                e.printStackTrace();
            }
        }
        clip.setFramePosition(stopPosition);
        clip.start();
    }

    private void notifyPositionChanged() {
        position.setSelected(segment, getCurrentFrame(), this);
    }

    public void pause() {
        stopPosition = clip.getFramePosition();
        paused = true;
        clip.stop();
        notifyPositionChanged();
    }

    public void stop() {
        paused = false;
        clip.stop();
        clip.setFramePosition(stopPosition);
        notifyPositionChanged();
    }

    public boolean isPlaying() {
        if (clip == null) {
            return false;
        }
        return clip.isRunning();
    }

    private int getCurrentFrame() {
        return clip.getFramePosition();
    }

    @Override
    public void positionChanged(Segment segment, int frame, Object initiator) {
        if (initiator != this) {
            boolean isPlaying = isPlaying();
            try {
                if (this.segment != segment) {
                    if (isPlaying) {
                        clip.stop();
                    }
                    loadSegment(segment);
                }
                stopPosition = frame;
                clip.setFramePosition(frame);

                if (isPlaying) {
                    play();
                }
            } catch (IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadSegment(Segment segment) throws IOException, LineUnavailableException {
        stopPosition = 0;
        this.segment = segment;
        clip.close();
        clip.open(segment.getAudioFile().getAudioStream());
    }
}
