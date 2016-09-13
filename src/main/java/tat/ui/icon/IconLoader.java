package tat.ui.icon;

import javafx.scene.image.Image;

/**
 * Created by Tate on 1/04/2016.
 */
public class IconLoader {

    private static IconLoader instance = null;
    protected IconLoader() {}

    public static IconLoader getInstance() {
        if(instance == null)
            instance = new IconLoader();
        return instance;
    }

    //Program Icon
    public Image logoIcon = new Image(getClass().getResourceAsStream("/icons/logo_raw.png"));

    ///NEW ICONS - IN ALPHABETICAL ORDER///
    public Image alignIcon = new Image(getClass().getResourceAsStream("/icons/Align_Released.png"));
    public Image alignIconPressed = new Image(getClass().getResourceAsStream("/icons/Align_Pressed.png"));

    public Image mainFileIcon = new Image(getClass().getResourceAsStream("/icons/Save_Released.png"));
    public Image mainFileIconPressed = new Image(getClass().getResourceAsStream("/icons/Save_Pressed.png"));

    public Image joinIcon = new Image(getClass().getResourceAsStream("/icons/Join_Released.png"));
    public Image joinIconPressed = new Image(getClass().getResourceAsStream("/icons/Join_Pressed.png"));

    public Image openCorpusIcon = new Image(getClass().getResourceAsStream("/icons/Browse_Released.png"));
    public Image openCorpusIconPressed = new Image(getClass().getResourceAsStream("/icons/Browse_Pressed.png"));

    public Image prevIcon = new Image(getClass().getResourceAsStream("/icons/Left_Released.png"));
    public Image prevIconPressed = new Image(getClass().getResourceAsStream("/icons/Left_Pressed.png"));

    public Image pauseIcon = new Image(getClass().getResourceAsStream("/icons/Pause_Released.png"));
    public Image pauseIconPressed = new Image(getClass().getResourceAsStream("/icons/Pause_Pressed.png"));

    public Image playIcon = new Image(getClass().getResourceAsStream("/icons/Play_Released.png"));
    public Image playIconPressed = new Image(getClass().getResourceAsStream("/icons/Play_Pressed.png"));

    public Image nextIcon = new Image(getClass().getResourceAsStream("/icons/Right_Released.png"));
    public Image nextIconPressed = new Image(getClass().getResourceAsStream("/icons/Right_Pressed.png"));

    public Image saveIcon = new Image(getClass().getResourceAsStream("/icons/Save_Floppy_Released.png"));
    public Image saveIconPressed = new Image(getClass().getResourceAsStream("/icons/Save_Floppy_Pressed.png"));

    public Image settingsIcon = new Image(getClass().getResourceAsStream("/icons/Settings_Released.png"));
    public Image settingsIconPressed = new Image(getClass().getResourceAsStream("/icons/Settings_Pressed.png"));

    public Image splitIcon = new Image(getClass().getResourceAsStream("/icons/Split_Released.png"));
    public Image splitIconPressed = new Image(getClass().getResourceAsStream("/icons/Split_Pressed.png"));

    public Image stopIcon = new Image(getClass().getResourceAsStream("/icons/Stop_Released.png"));
    public Image stopIconPressed = new Image(getClass().getResourceAsStream("/icons/Stop_Pressed.png"));

    public Image zoomInIcon = new Image(getClass().getResourceAsStream("/icons/Zoom_In_Released.png"));
    public Image zoomInIconPressed = new Image(getClass().getResourceAsStream("/icons/Zoom_In_Pressed.png"));

    public Image zoomOutIcon = new Image(getClass().getResourceAsStream("/icons/Zoom_Out_Released.png"));
    public Image zoomOutIconPressed = new Image(getClass().getResourceAsStream("/icons/Zoom_Out_Pressed.png"));

}
