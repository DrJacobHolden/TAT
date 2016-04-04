package ui.waveform;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import ui.icon.Icon;
import ui.icon.IconLoader;

/**
 * Created by Tate on 5/04/2016.
 *
 * A box containing buttons to zoom in and zoom out.
 */
public class ZoomButtons extends HBox {

    //The zoomableWaveformPane which is to receive the zoom in/out commands
    private ZoomableWaveformPane zoomableWaveformPane;

    public ZoomButtons(ZoomableWaveformPane zoomableWaveformPane) {
        this.zoomableWaveformPane = zoomableWaveformPane;

        //Allows this to be on the top layer of a stackpane without preventing
        //click actions for layers below it
        setPickOnBounds(false);

        Button zoomIn = new ZoomInButton("", new Icon(IconLoader.getInstance().zoomInIcon));
        Button zoomOut = new ZoomOutButton("", new Icon(IconLoader.getInstance().zoomOutIcon));

        getChildren().addAll(zoomIn, zoomOut);

    }

    /**
     * The zoom in button, calls the zoomIn method of the zoomableWaveformPane
     */
    protected class ZoomInButton extends Button {
        ZoomInButton(String text, ImageView iv1) {
            super(text, iv1);
            setOnAction(event -> zoomableWaveformPane.zoomIn());
        }
    }

    /**
     * The zoom out button, calls the zoomOut method of the zoomableWaveformPane
     */
    protected class ZoomOutButton extends Button {
        ZoomOutButton(String text, ImageView iv1) {
            super(text, iv1);
            setOnAction(event -> zoomableWaveformPane.zoomOut());
        }
    }
}
