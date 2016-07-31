package tat.view;

import file_system.Recording;
import file_system.Segment;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import tat.PositionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.awt.Color.orange;
import static java.awt.SystemColor.menu;

/**
 * Created by Tate on 1/08/2016.
 */
public class RightClickMenu extends ContextMenu implements PositionListener {

    private final Recording r;
    private final EditorMenuController e;
    private Segment activeSegment;

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

    MenuItem enableOrDisable;
    MenuItem rename;

    Menu speakerMenu;

    public RightClickMenu(Recording r, EditorMenuController e) {

        this.r = r;
        this.e = e;

        //Default to the first segment
        activeSegment = r.getSegment(1);

        enableOrDisable = createMenuItem("Disable", false, () -> {

        });
        rename = createMenuItem("Rename", false, () -> {

        });

        SeparatorMenuItem sep = new SeparatorMenuItem();

        speakerMenu = createSpeakerMenu();

        this.getItems().addAll(enableOrDisable, rename, sep, speakerMenu);

    }

    private Menu createSpeakerMenu() {
        Menu speaker = new Menu();
        speaker.setGraphic(createLabel("Speaker", false));


        //Make the speakers list every time the menu is requested
        speaker.setOnShowing((a) -> {

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
                System.out.println("Active Segment Speaker Id: " + activeSegment.getSpeakerId());
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
        });

        //Create the new button
        speaker.getItems().add(createMenuItem("New", false, () -> {
            //Set the speaker id on the segment to be the next speakerId
            activeSegment.setSpeakerId(speaker.getItems().size()-1);
        }));



        return speaker;
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
}
