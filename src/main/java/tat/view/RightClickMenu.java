package tat.view;

import file_system.Recording;
import file_system.Segment;
import file_system.attribute.CustomAttribute;
import file_system.attribute.CustomAttributeInstance;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import tat.Position;
import tat.PositionListener;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.awt.Color.orange;
import static java.awt.SystemColor.menu;
import static tat.Main.p;

/**
 * Created by Tate on 1/08/2016.
 */
public class RightClickMenu extends ContextMenu implements PositionListener {

    private final Recording r;
    private final EditorMenuController e;
    private Segment activeSegment;

    private final int NUM_CONSTANT_ITEMS = 4;

    //TODO:
    /**
     * Map speaker ids to an English name (Should be extensible as will be used by all custom fields)
     * Tooltips on Context Menu items
     *
     *  * = In Progress
     *  / = Done
     *
     * - Enable/Disable segment (*)
     * - Rename (*)
     *   - Shows popup menu with a textfield ( )
     * - Set Speaker (/)
     *   - List of Speakers
     *     - Secondary_Gray == Current Speaker
     *   - Add Speaker (/)
     * - Set "Custom Field Name"
     *   - List of "Custom Field Name"
     *   - Add "Custom Field Name"
     * - Add Custom Field
     */

    private MenuItem enableOrDisable;
    private MenuItem rename;

    private Menu speakerMenu;

    public RightClickMenu(Recording r, EditorMenuController e, Position p) {

        this.r = r;
        this.e = e;

        //Default to the first segment
        activeSegment = r.getSegment(1);

        p.addSelectedListener(this);

        enableOrDisable = createMenuItem("Disable", false, () -> {

        });
        rename = createMenuItem("Rename", false, () -> {

        });

        SeparatorMenuItem sep = new SeparatorMenuItem();

        speakerMenu = createSpeakerMenu();

        this.getItems().addAll(enableOrDisable, rename, sep, speakerMenu);

        this.setOnShowing((a) -> {
            refreshCustomAttributes();
        });

    }

    private void refreshCustomAttributes() {

        //Clear the custom attribute menus
        getItems().remove(NUM_CONSTANT_ITEMS, getItems().size());

        //Build a list of all the available custom attributes for the recording
        List<CustomAttributeInstance> attributes = new ArrayList();
        for(Segment s : r) {
            for (CustomAttributeInstance c : s.customAttributes) {
                if(!attributes.contains(c)) {
                    attributes.add(c);
                }
            }
        }

        List<Menu> customAttributeMenus = new ArrayList();
        for(CustomAttributeInstance c : attributes) {
            customAttributeMenus.add(createCustomAttributeMenu(c));
        }

        MenuItem newCustomAttribute = createMenuItem("New Attribute", false, () -> {
            CustomAttribute custom = createAttribute();
            if(custom != null) {
                activeSegment.customAttributes.add(custom.newCustomAttributeInstance());
            }
            //Do nothing if null == cancel/close dialog
        });

        //Add the custom attribute menus
        this.getItems().addAll(customAttributeMenus);
        this.getItems().add(newCustomAttribute);
    }

    private Menu createSpeakerMenu() {
        Menu speaker = new Menu();
        speaker.setGraphic(createLabel("Speaker", false));

        //Make the speakers list every time the menu is requested
        speaker.setOnShowing((a) -> {
            refreshSpeakerMenu(speaker);
        });

        //Create the new button
        speaker.getItems().add(createMenuItem("New", false, () -> {
            //Set the speaker id on the segment to be the next speakerId
            activeSegment.setSpeakerId(speaker.getItems().size()-1);
        }));

        return speaker;
    }

    private Menu createCustomAttributeMenu(CustomAttributeInstance c) {
        Menu attribute = new Menu();
        attribute.setGraphic(createLabel(c.getName(), false));

        //Make the attribute value list every time the menu is requested
        attribute.setOnShowing((a) -> {
            refreshAttributeMenu(attribute, c);
        });

        //Create the new button
        attribute.getItems().add(createMenuItem("New", false, () -> {
            activeSegment.customAttributes.get(activeSegment.customAttributes.indexOf(c)).setValue(getNewAttributeValue());
        }));

        return attribute;
    }

    private void refreshSpeakerMenu(Menu speaker) {
        //Find the highest used speaker id
        int highest = 0;
        for(Segment s : r.getSegments().values()) {
            if(highest < s.getSpeakerId())
                highest = s.getSpeakerId();
        }

        //Create the menu items
        List<MenuItem> items = new ArrayList();
        for(int i = 0; i <= highest; i++) {
            final int j = i;
            //Colour the active segment
            if(i == activeSegment.getSpeakerId()) {
                items.add(createMenuItem(Integer.toString(i), true, () -> {
                    //Do nothing as already set
                }));
            } else {
                items.add(createMenuItem(Integer.toString(i), false, () -> {
                    //Set the speaker id on the segment
                    activeSegment.setSpeakerId(j);
                }));
            }
        }
        //Remove all items except the new button
        speaker.getItems().remove(1, speaker.getItems().size());

        speaker.getItems().addAll(items);
    }

    private void refreshAttributeMenu(Menu attribute, CustomAttributeInstance c) {

        //Find the values available for attribute
        List<String> values = new ArrayList();
        values.add(c.customAttribute.getDefaultValue());
        for(Segment s : r.getSegments().values()) {
            if(s.customAttributes.contains(c)) {
                String value = s.customAttributes.get(s.customAttributes.indexOf(c)).getValue();
                //Add missing values
                if(!values.contains(value)) {
                    values.add(value);
                }
            }
        }

        //Create the menu items
        List<MenuItem> items = new ArrayList();
        for(String v : values) {
            //Colour the active segment
            if(v.equals(c.customAttribute.getValue(activeSegment))) {
                items.add(createMenuItem(v, true, () -> {
                    //Do nothing as already set
                }));
            } else {
                items.add(createMenuItem(v, false, () -> {
                    //Set the value of the custom attribute on the segment
                    activeSegment.customAttributes.get(activeSegment.customAttributes.indexOf(c)).setValue(v);
                }));
            }
        }
        //Remove all items except the new button
        attribute.getItems().remove(1, attribute.getItems().size());

        attribute.getItems().addAll(items);
    }

    private MenuItem createMenuItem(String text, boolean selected, CustomCall c) {
        MenuItem mi = new MenuItem();
        mi.setGraphic(createLabel(text, selected));

        mi.setOnAction((a) -> c.act());

        return mi;
    }

    private Label createLabel(String text, boolean selected) {
        Label label = new Label(text);
        label.setFont(Font.font("Levenim MT", 16));

        if(selected) {
            label.setTextFill(Colours.SECONDARY_GRAY);
        } else {
            label.setTextFill(Colours.WHITE);
        }

        label.setContentDisplay(ContentDisplay.RIGHT);
        label.setGraphicTextGap(0);
        return label;
    }

    @Override
    public void positionChanged(Segment segment, int frame, Object initiator) {
        activeSegment = segment;
    }

    private interface CustomCall {
        void act();
    }

    private CustomAttribute createAttribute() {
        Dialog<String[]> alert = new Dialog();
        alert.setTitle("Create New Attribute");
        alert.setHeaderText("Add a custom attribute to the recording");
        alert.setContentText(null);
        alert.setGraphic(null);

        //Reference the main stage Main.p
        alert.initOwner(p);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(ClassLoader.getSystemResource("css/dialog.css").toExternalForm());

        ButtonType buttonOkay = new ButtonType("Save", ButtonBar.ButtonData.APPLY);

        dialogPane.getButtonTypes().addAll(buttonOkay, ButtonType.CANCEL);


        final TextField name = new TextField();
        name.setPromptText("Attribute Name");

        final TextField defaultValue = new TextField();
        defaultValue.setPromptText("Attribute Default Value");

        final TextField token = new TextField();
        token.setPromptText("Attribute Path Token");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        grid.add(createLabel("Name:", false), 0, 0);
        grid.add(name, 1, 0);
        grid.add(createLabel("Default Value:", false), 0, 1);
        grid.add(defaultValue, 1, 1);
        grid.add(createLabel("Path Token:", false), 0, 2);
        grid.add(token, 1, 2);

        // Enable/Disable login button depending on whether token and name are entered.
        Node okayButton = dialogPane.lookupButton(buttonOkay);
        okayButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!token.getText().trim().equals("")) {
                okayButton.setDisable(newValue.trim().isEmpty());
            } else {
                okayButton.setDisable(true);
            }
        });

        // Do some validation (using the Java 8 lambda syntax).
        token.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!name.getText().trim().equals("")) {
                okayButton.setDisable(newValue.trim().isEmpty());
            } else {
                okayButton.setDisable(true);
            }
        });

        dialogPane.setContent(grid);

        alert.setResultConverter(dialogButton -> {
            if(dialogButton == buttonOkay) {
                return new String[]{name.getText(), defaultValue.getText(), token.getText()};
            }
            return null;
        });

        Optional<String[]> result = alert.showAndWait();
        if(result.isPresent())
            return new CustomAttribute(result.get()[0], result.get()[1], result.get()[2]);
        return null;
    }

    private String getNewAttributeValue() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Set Attribute Value");
        dialog.setHeaderText(null);
        dialog.setContentText("New Value:");
        dialog.setGraphic(null);

        //Reference the main stage Main.p
        dialog.initOwner(p);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(ClassLoader.getSystemResource("css/dialog.css").toExternalForm());

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            return result.get();
        }
        return null;
    }
}
