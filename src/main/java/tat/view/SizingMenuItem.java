package tat.view;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Font;

/**
 * Provided by Peter Brouwer
 * Code source: https://community.oracle.com/thread/2480168?tstart=0
 */
public class SizingMenuItem extends MenuItem{

    static final double BUFFER_WIDTH = 29; // to account for the width of the padding around menu items.
    public SizingMenuItem(final MenuButton menuButton, String string, boolean selected) {
        super();
        Label label=new Label("  " + string);
        label.setFont(new Font("Levenim MT", 26.0));
        if(selected) {
            label.setTextFill(Colours.SECONDARY_GRAY);
        } else {
            label.setTextFill(Colours.WHITE);
        }
        label.prefWidthProperty().bind(new DoubleBinding(){

            {
                super.bind(menuButton.widthProperty());
            }

            @Override
            protected double computeValue() {
                return menuButton.widthProperty().get()-BUFFER_WIDTH;
            }
        });
        label.setContentDisplay(ContentDisplay.RIGHT);
        label.setGraphicTextGap(0);
        setGraphic(label);
    }
}
