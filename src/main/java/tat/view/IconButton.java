package tat.view;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.MouseEvent;
import tat.TimerHandler;
import tat.view.icon.Icon;

import java.util.TimerTask;

/**
 * Created by Tate on 29/06/2016.
 */
public class IconButton extends Button {

    private Icon standard;
    private Icon pressed;
    private Double size;
    private boolean flashing = false;
    private boolean flash = false;

    public IconButton() {
        super();
        size = getWidth();
    }

    public void setIcons(Icon standard, Icon pressed) {
        this.standard = standard;
        this.pressed = pressed;
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

        //Set up the timer so that icons can flash.
        TimerHandler.getInstance().newTimer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if(flashing) {
                                    if(flash) {
                                        setGraphic(standard.resizeIcon(size, size));
                                        flash = false;
                                    } else {
                                        setGraphic(pressed.resizeIcon(size, size));
                                        flash = true;
                                    }
                                }
                            }
                        });
                    }
                }, 0, 500);
    }

    public void setFlashing(boolean flash) {
        flashing = flash;

        //Ensure button does not remain orange when flashing disabled
        if(!flash)
            setGraphic(standard.resizeIcon(size, size));
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
