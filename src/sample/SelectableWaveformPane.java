package sample;

import com.sun.javaws.exceptions.InvalidArgumentException;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * Created by max on 29/03/16.
 */
public class SelectableWaveformPane extends ZoomableWaveformPane {

    protected WaveformTime clickPosition = new WaveformTime();

    public WaveformTime getClickPosition() {
        return clickPosition;
    }

    @Override
    protected void initialize(WaveformImageView wf) {
        super.initialize(wf);

        waveformImageView.setOnMouseClicked(event -> clicked(event));
    }

    protected void clicked(MouseEvent event) {
        event.getSource();
        double x = event.getX();

        double percent = event.getX() / (waveformImageView.getFitWidth());
        clickPosition.setPercent(percent);

        System.out.println(clickPosition);
    }

    protected class WaveformTime {
        long frame = 0;
        double percent = 0;

        public void setFrame(int frame) throws IllegalArgumentException {
            long length = waveformImageView.getAudioStream().getFrameLength();
            if (frame >= length) {
                throw new IllegalArgumentException();
            } else {
                this.frame = frame;
                percent = frame/((double) length);
            }
        }

        public void setPercent(double percent) throws  IllegalArgumentException {
            if (percent > 1) {
                throw new IllegalArgumentException();
            } else {
                this.percent = percent;
                this.frame = (long) (percent * (double) waveformImageView.getAudioStream().getFrameLength());
            }
        }

        public String toString() {
            return "" + percent + " Frame " + frame + "/" + waveformImageView.getAudioStream().getFrameLength();
        }
    }
}
