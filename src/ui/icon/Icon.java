package ui.icon;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by Tate on 1/04/2016.
 */
public class Icon extends ImageView {

    private final int DEFAULT_X = 125;
    private final int DEFAULT_Y = 125;

    public Icon(Image icon) {
        setImage(icon);
        setFitHeight(DEFAULT_Y);
        setFitWidth(DEFAULT_X);
    }

    public Icon resizeIcon(double x, double y) {
        setFitHeight(y);
        setFitWidth(x);
        return this;
    }

}
