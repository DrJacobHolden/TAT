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
        super(new fileButton(), new saveButton(), new configButton(), new uploadButton());
        setOrientation(Orientation.VERTICAL);
        setMinWidth(51);
    }
}

class fileButton extends Button {
    public fileButton() {
        super("", new Icon(IconLoader.getInstance().fileIcon));
    }
}

class saveButton extends Button {
    public saveButton() {
        super("", new Icon(IconLoader.getInstance().saveIcon));
    }
}

class configButton extends Button {
    public configButton() {
        super("", new Icon(IconLoader.getInstance().configIcon));
    }
}

class uploadButton extends Button {
    public uploadButton() {
        super("", new Icon(IconLoader.getInstance().uploadIcon));
    }
}
