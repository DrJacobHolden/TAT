package audio_player;

import sample.Main;

import javax.sound.sampled.*;
import java.io.File;
import java.net.URL;

/**
 * Created by Tate on 1/04/2016.
 */
public class AudioPlayer {

    public static Clip clip;
    public static Mixer mixer;

    /**
     * Sets up an audio player to play the specified sound file.
     */
    public AudioPlayer(File soundFile) {
        Mixer.Info[] mixInfos = AudioSystem.getMixerInfo();
        mixer = AudioSystem.getMixer(mixInfos[0]);

        DataLine.Info dataInfo = new DataLine.Info(Clip.class, null);
        try {
            clip = (Clip)mixer.getLine(dataInfo);
        } catch(LineUnavailableException lue) { lue.printStackTrace(); }

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            clip.open(audioStream);
        } catch(Exception e) { e.printStackTrace(); }
    }

    public void play(long position) {
        clip.setFramePosition((int)position);
        clip.start();
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
    }

    public long getCurrentFrames() {
        return clip.getFramePosition();
    }

    public long getEndFrame() {
        return clip.getFrameLength();
    }

}
