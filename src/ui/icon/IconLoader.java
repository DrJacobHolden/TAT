package ui.icon;

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

    //Playback controls
    public Image prevIcon = new Image(getClass().getResourceAsStream("/ui/icon/icons/previous-track.png"));
    public Image playIcon = new Image(getClass().getResourceAsStream("/ui/icon/icons/play-button.png"));
    public Image nextIcon = new Image(getClass().getResourceAsStream("/ui/icon/icons/next-track-button.png"));
    public Image pauseIcon = new Image(getClass().getResourceAsStream("/ui/icon/icons/pause-button.png"));
    public Image stopIcon = new Image(getClass().getResourceAsStream("/ui/icon/icons/stop.png"));

    //Zoom controls
    public Image zoomInIcon = new Image(getClass().getResourceAsStream("/ui/icon/icons/add.png"));
    public Image zoomOutIcon = new Image(getClass().getResourceAsStream("/ui/icon/icons/remove.png"));

    //Menu Icons
    public Image fileIcon = new Image(getClass().getResourceAsStream("/ui/icon/icons/file.png"));
    public Image saveIcon = new Image(getClass().getResourceAsStream("/ui/icon/icons/save_2.png"));
    public Image uploadIcon = new Image(getClass().getResourceAsStream("/ui/icon/icons/upload.png"));
    public Image configIcon = new Image(getClass().getResourceAsStream("/ui/icon/icons/config.png"));

    //Split Join Icons
    public Image splitIcon = new Image(getClass().getResourceAsStream("/ui/icon/icons/arrows.png"));
    public Image joinIcon = new Image(getClass().getResourceAsStream("/ui/icon/icons/arrows_inv.png"));

    //Program Icon
    public Image logoIcon = new Image(getClass().getResourceAsStream("/ui/icon/icons/logo_raw.png"));

    ///NEW ICONS///
    public Image mainFileIcon = new Image(getClass().getResourceAsStream("/ui/icon/icons/Browse_Released.png"));
    public Image mainFileIconPressed = new Image(getClass().getResourceAsStream("/ui/icon/icons/Browse_Pressed.png"));

}
