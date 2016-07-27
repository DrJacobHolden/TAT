package ui.waveform;

import file_system.Segment;
import javafx.geometry.Insets;
import javafx.scene.image.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import tat.view.Colours;

import javax.imageio.ImageReader;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

/**
 * Created by kalda on 7/07/2016.
 */
public class WaveformSegment extends StackPane {

    private int noFrames;
    private ImageView imageView;

    private final Background SELECTED_BACKGROUND = new Background(new BackgroundFill(Colours.ORANGE, CornerRadii.EMPTY, Insets.EMPTY));
    private final Background ODD_BACKGROUND = new Background(new BackgroundFill(Colours.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
    private final Background EVEN_BACKGROUND = new Background(new BackgroundFill(Colours.TERTIARY_GRAY, CornerRadii.EMPTY, Insets.EMPTY));
    private final Segment segment;

    public double getImageWidth() {
        return imageView.getImage().getWidth();
    }

    public double getImageHeight() { return imageView.getImage().getHeight(); }

    public WaveformSegment(Segment segment) throws IOException, UnsupportedAudioFileException {
        this.segment = segment;
        Image waveformImage = generateImage(segment);
        addImageView(waveformImage);
    }

    private WaveformSegment(Segment segment, Image image, int noFrames) {
        this.noFrames = noFrames;
        this.segment = segment;
        addImageView(image);
    }

    private void addImageView(Image image) {
        imageView = new ImageView(image);
        getChildren().add(imageView);
    }

    private Image generateImage(Segment segment) {
        AudioInputStream audioInputStream = segment.getAudioFile().getStream();
        noFrames = (int) audioInputStream.getFrameLength();
        try {
            WaveformGenerator waveformGenerator = new WaveformGenerator(audioInputStream);
            //Just a guess at a good resolution. Should really changed depending on zoom.
            return waveformGenerator.getWaveformImage(10, 200);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                audioInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Segment getSegment() {
        return segment;
    }

    public void setColourSelected() {
        setBackground(SELECTED_BACKGROUND);
    }

    public void setColourOdd() {
        setBackground(ODD_BACKGROUND);
    }

    public void setColourEven() {
        setBackground(EVEN_BACKGROUND);
    }

    public int getFrameForPosition(double x) {
        return (int) ((x/getWidth()) * noFrames);
    }

    public double getPositionForFrame(int frame) {
        return (double) frame/noFrames * getWidth();
    }

    public WaveformSegment split(Segment newSegment, int frame) {
        Image currentImage = imageView.getImage();
        PixelReader reader = currentImage.getPixelReader();

        int splitPos = (int) getPositionForFrame(frame);
        int height = (int) currentImage.getHeight();
        //x, y, width, height
        Image image1 = new WritableImage(reader, 0, 0, splitPos, height);
        Image image2 = new WritableImage(reader, splitPos, 0, (int)currentImage.getWidth() - splitPos, height);

        //Update this view
        imageView.setImage(image1);
        noFrames = segment.getAudioFile().getNoFrames();
        //Return the new view
        return new WaveformSegment(newSegment, image2, newSegment.getAudioFile().getNoFrames());
    }

    private Image joinImages(Image image1, Image image2) {
        //Create image
        WritableImage joinedImage = new WritableImage((int)(image1.getWidth()+image2.getWidth()), (int)image1.getHeight());
        PixelWriter writer = joinedImage.getPixelWriter();
        //Write image 1
        PixelReader reader1 = image1.getPixelReader();
        writer.setPixels(0, 0, (int)image1.getWidth(), (int)image1.getHeight(), reader1, 0, 0);
        //Write image 2 to the right of image 1
        PixelReader reader2 = image2.getPixelReader();
        writer.setPixels((int) image1.getWidth(), 0, (int)image2.getWidth(), (int)image2.getHeight(), reader2, 0, 0);

        return joinedImage;
    }

    public void join(WaveformSegment secondSegment) {
        Image newImage = joinImages(imageView.getImage(), secondSegment.imageView.getImage());
        imageView.setImage(newImage);
        noFrames = noFrames + secondSegment.noFrames;
    }
}
