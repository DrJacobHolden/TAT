package audio_player;

import sample.Main;

import javax.sound.sampled.*;
import java.net.URL;

/**
 * Created by Tate on 1/04/2016.
 */
public class PlayAudio {

    public static Clip clip;
    public static Mixer mixer;

    public void play() {
        Mixer.Info[] mixInfos = AudioSystem.getMixerInfo();
        for(Mixer.Info info : mixInfos) {
            System.out.println(info.getName());
        }
        mixer = AudioSystem.getMixer(mixInfos[0]);

        DataLine.Info dataInfo = new DataLine.Info(Clip.class, null);
        try { clip = (Clip)mixer.getLine(dataInfo); }
        catch(LineUnavailableException lue) { lue.printStackTrace(); }

        try {
            URL soundUrl = Main.class.getResource("/sample/recording.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundUrl);
            clip.open(audioStream);
        } catch(Exception e) { e.printStackTrace(); }

        clip.start();

        do {
            try { Thread.sleep(50); }
            catch(Exception e) { e.printStackTrace(); }
        } while (clip.isActive());
    }

}
