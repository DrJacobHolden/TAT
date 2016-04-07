package ui.icon;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by Tate on 1/04/2016.
 */
public class Icon extends ImageView {

    private final int DEFAULT_X = 25;
    private final int DEFAULT_Y = 25;

    public Icon(Image icon) {
        setImage(icon);
        setFitHeight(DEFAULT_Y);
        setFitWidth(DEFAULT_X);
    }

    public Icon resizeIcon(int x, int y) {
        setFitHeight(y);
        setFitWidth(x);
        return this;
    }

}
