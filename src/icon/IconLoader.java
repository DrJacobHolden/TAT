package icon;

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
    public Image prevIcon = new Image(getClass().getResourceAsStream("/icon/icons/previous-track.png"));
    public Image playIcon = new Image(getClass().getResourceAsStream("/icon/icons/play-button.png"));
    public Image nextIcon = new Image(getClass().getResourceAsStream("/icon/icons/next-track-button.png"));
    public Image pauseIcon = new Image(getClass().getResourceAsStream("/icon/icons/pause-button.png"));
    public Image stopIcon = new Image(getClass().getResourceAsStream("/icon/icons/stop.png"));

    //Zoom controls
    public Image zoomInIcon = new Image(getClass().getResourceAsStream("/icon/icons/add.png"));
    public Image zoomOutIcon = new Image(getClass().getResourceAsStream("/icon/icons/remove.png"));

    //Menu Icons
    public Image menuIcon = new Image(getClass().getResourceAsStream("/icon/icons/menu-1.png"));

    //Split Join Icons
    public Image splitIcon = new Image(getClass().getResourceAsStream("/icon/icons/arrows.png"));
    public Image joinIcon = new Image(getClass().getResourceAsStream("/icon/icons/arrows_inv.png"));

}
