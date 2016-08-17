package tat.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.util.Optional;

import static tat.Main.p;

/**
 * Created by kalda on 16/08/2016.
 */
public class DialogBox {

    private Alert alert;
    private ButtonType[] buttons;
    private DialogOption[] options;

    public DialogBox(String title, String content) {
        this(title, content, new DialogOption[] {DialogOption.OK});
    }

    public DialogBox(String title, String content, DialogOption[] options) {
        this.options = options;
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.setGraphic(null);

        alert.initOwner(p);

        buttons = new ButtonType[options.length];
        for (int i=0; i<buttons.length; i++) {
            buttons[i] = options[i].getButton();
        }
        alert.getButtonTypes().setAll(buttons);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(ClassLoader.getSystemResource("css/dialog.css").toExternalForm());
    }

    public DialogOption showAndGetResult() {
        Optional<ButtonType> result = alert.showAndWait();
        for (int i=0; i<buttons.length; i++) {
            if (buttons[i] == result.get()) {
                return options[i];
            }
        }
        return null;
    }

    public enum DialogOption {
        YES, NO, CANCEL, OK;

        public ButtonType getButton() {
            if (this == YES) {
                return new ButtonType("Yes");
            } else if (this == NO) {
                return new ButtonType("No");
            } else if (this == OK) {
                return new ButtonType("OK");
            } else {
                return new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            }
        }
    }
}
