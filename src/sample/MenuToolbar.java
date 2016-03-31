package sample;

import icon.Icon;
import icon.IconLoader;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;

/**
 * Created by Tate on 1/04/2016.
 *
 * The menu toolbar which runs along the left hand side of the screen
 */
public class MenuToolbar extends ToolBar {

    public MenuToolbar() {
        super(new MenuButton());
        setOrientation(Orientation.VERTICAL);
        setMinWidth(51);
    }
}

class MenuButton extends Button {
    public MenuButton() {
        super("", new Icon(IconLoader.getInstance().menuIcon));
    }
}
