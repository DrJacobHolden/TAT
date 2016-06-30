package tat.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import ui.icon.Icon;

/**
 * Created by Tate on 29/06/2016.
 */
public class IconButton extends Button {

    Icon standard;
    Double size;

    public IconButton() {
        super();
        size = getWidth();
    }

    public void setIcons(Icon standard, Icon pressed) {
        this.standard = standard;
        setContentDisplay(ContentDisplay.CENTER);
        setGraphic(standard.resizeIcon(size, size));

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setGraphic(pressed.resizeIcon(size, size));
            }
        });
        setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setGraphic(standard.resizeIcon(size, size));
            }
        });
        heightProperty().addListener(new ResizeListener(this));
        widthProperty().addListener(new ResizeListener(this));
    }

    class ResizeListener<Double> implements ChangeListener {

        IconButton iconButton;

        protected ResizeListener(IconButton ib) {
            this.iconButton = ib;
        }

        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            size = (double)newValue;
            iconButton.setGraphic(standard.resizeIcon(size, size));
        }
    }
}
