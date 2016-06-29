package tat.view;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * Created by Tate on 29/06/2016.
 */
public class IconButton extends Button {

    public IconButton() {
        super();
    }

    public void setIcons(ImageView standard, ImageView pressed) {
        setGraphic(standard);

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setGraphic(pressed);
            }
        });
        setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setGraphic(standard);
            }
        });
    }
}
