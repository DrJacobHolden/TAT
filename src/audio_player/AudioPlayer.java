package audio_player;

import javafx.application.Platform;

import javax.sound.sampled.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Tate on 1/04/2016.
 */
public class AudioPlayer {

    public static Clip clip;
    public static Mixer mixer;
    private Timer positionListenerTimer;
    private List<AudioPositionListener> audioPositionListeners = new ArrayList<>();

    private final long AUDIO_POSITION_UPDATE_INTERVAL = 20;

    /**
     * Sets up an audio player to play the specified sound file.
     */
    public AudioPlayer(File soundFile) {
        Mixer.Info[] mixInfos = AudioSystem.getMixerInfo();
        mixer = AudioSystem.getMixer(mixInfos[0]);

        DataLine.Info dataInfo = new DataLine.Info(Clip.class, null);
        try {
            clip = (Clip) mixer.getLine(dataInfo);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            clip.open(audioStream);
            setUpListenerEvents();
        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addAudioPositionListener(AudioPositionListener l) {
        audioPositionListeners.add(l);
    }

    private void setUpListenerEvents() {
        clip.addLineListener(event -> {
            //Give regular updates of position if started
            if (event.getType() == LineEvent.Type.START) {
                notifyPositionListeners();
                //Create new timer to notify position listeners
                positionListenerTimer = new Timer();
                positionListenerTimer.schedule(new TimerTask() {
                    public void run() {
                        Platform.runLater(() -> notifyPositionListeners());
                    }
                }, AUDIO_POSITION_UPDATE_INTERVAL, AUDIO_POSITION_UPDATE_INTERVAL);
            }
            //Stop giving regular updates of position if stopped
            else if (event.getType() == LineEvent.Type.STOP) {
                notifyPositionListeners();
                positionListenerTimer.cancel();
            }
        });
    }

    public void play(long position) {
        clip.setFramePosition((int)position);
        clip.start();
    }

    private void notifyPositionListeners() {
        for (AudioPositionListener l : audioPositionListeners){
            l.frameUpdate(getCurrentFrame());
        }
    }

    public void pause() {
        clip.stop();
    }

    public void stop() {
        clip.stop();
        clip.setFramePosition(0);
    }

    public void goToSection(long position) {
        clip.setFramePosition((int) position);
        notifyPositionListeners();
    }

    public long getCurrentFrame() {
        return clip.getFramePosition();
    }

    public long getEndFrame() {
        return clip.getFrameLength();
    }

    public interface AudioPositionListener {
        void frameUpdate(long frame);
    }
}
