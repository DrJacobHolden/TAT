package tat.view;

import file_system.Recording;
import file_system.Segment;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import ui.waveform.WaveformImageView;

import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Created by Tate on 4/07/2016.
 */
public class WaveformDisplay extends HBox {

    public Recording recording;

    public void setRecording(Recording recording) {
        this.recording = recording;
    }

    public void drawWaveform() {
        for (Segment segment : recording.getSegments().values()) {
            WaveformImageView iv = new WaveformImageView();
            try {
                iv.setAudioStream(segment.getAudioFile().getStream());
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }
    }
}
