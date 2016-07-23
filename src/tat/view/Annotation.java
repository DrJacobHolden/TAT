package tat.view;

import file_system.Recording;
import file_system.Segment;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import tat.Position;
import tat.PositionListener;
import tat.resources.TextWidths;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tate on 23/07/2016.
 */
public class Annotation implements PositionListener {

    private String annotation;
    private Segment segment;
    private List<TextField> textFields = new ArrayList<>();

    private Position position;
    private Recording recording;
    private AnnotationDisplay ad;

    private boolean selected = false;

    public Annotation(String annotation, Segment segment, Recording recording, Position position, AnnotationDisplay ad) {
        this.annotation = annotation;
        this.segment = segment;
        this.recording = recording;

        this.ad = ad;

        this.position = position;
        position.addSelectedListener(this);
    }

    public void refreshAnnotations(double firstWidth, double width) {
        textFields.clear();
        List<String> annotations = getAnnotations(firstWidth, width);
        for(String s : annotations) {
            textFields.add(createTextField(s));
        }
        if(selected)
            setColours(true);
        else
            setColours(false);
    }

    private List<String> getAnnotations(double firstWidth, double width) {
        List<String> annotations = new ArrayList();
        String temp = "";
        double currentLength = 0;
        boolean first = true;

        for(int i = 0; i < annotation.length(); i++) {
            char c = annotation.toLowerCase().charAt(i);
            if(first) {
                if(currentLength < firstWidth-35 && i != annotation.length()-1) {
                    if(TextWidths.CHARACTER_WIDTH_MAP.get(""+c) != null) {
                        currentLength += TextWidths.CHARACTER_WIDTH_MAP.get("" + c);
                    } else {
                        currentLength += com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(""+c, ad.DEFAULT_FONT);
                        System.out.println("Char: " + c + " missing from map. Has width: "
                                + com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(""+c, ad.DEFAULT_FONT));
                    }
                    temp = temp + c;
                } else {
                    temp = temp + c;
                    first = false;
                    annotations.add(temp);
                    currentLength = 0;
                    temp = "";
                }
            } else {
                if(currentLength < width-35 && i != annotation.length()-1) {
                    if(TextWidths.CHARACTER_WIDTH_MAP.get(""+c) != null) {
                        currentLength += TextWidths.CHARACTER_WIDTH_MAP.get("" + c);
                    } else {
                        currentLength += com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(""+c, ad.DEFAULT_FONT);
                        System.out.println("Char: " + c + " missing from map. Has width: "
                                + com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(""+c, ad.DEFAULT_FONT));
                    }
                    temp = temp + c;
                } else {
                    temp = temp + c;
                    annotations.add(temp);
                    currentLength = 0;
                    temp = "";
                }
            }
        }
        return annotations;
    }

    public void resizeAnnotation() {
        Platform.runLater(() -> {
            for(TextField t : textFields)
                autosizeTextField(t);
        });
    }

    public List<TextField> getTextFields() {
        return textFields;
    }
    public String getAnnotation() { return annotation; }

    private TextField createTextField(String text) {
        TextField t = new TextField(text);
        t.setFont(AnnotationDisplay.DEFAULT_FONT);
        t.setPadding(new Insets(0,0,0,0));

        t.textProperty().addListener((ov, prevText, currText) -> {
            Platform.runLater(() -> {
                autosizeTextField(t);


                //Update the annotation
                String masterText = "";
                for (TextField t2 : textFields) {
                    masterText = masterText + t2.getText();
                }
                segment.getAnnotationFile().setString(masterText);
            });
        });

        t.setOnMouseClicked(event -> {
            position.setSelected(segment, 0, this);
        });
        return t;
    }

    public void setColours(boolean orange) {
        for(TextField t : textFields) {
            if (orange) {
                t.setStyle("-fx-text-fill: #ff7c00; -fx-background-color: #2b2934;");
                selected = true;
            } else {
                selected = false;
                if (segment.getSegmentNumber() % 2 == 0) {
                    t.setStyle("-fx-text-fill: #5c5a67; -fx-background-color: #2b2934;");
                } else {
                    t.setStyle("-fx-text-fill: #e4e1f0; -fx-background-color: #2b2934;");
                }
            }
        }
    }

    @Override
    public void positionChanged(Segment segment, double frame, Object initiator) {
        if(segment != this.segment) {
            setColours(false);
        } else {
            setColours(true);
        }
    }

    private void autosizeTextField(TextField t) {
        //Source: http://stackoverflow.com/questions/12737829/javafx-textfield-resize-to-text-length
        Text text = new Text(t.getText());
        text.setFont(t.getFont()); // Set the same font, so the size is the same
        double width = text.getLayoutBounds().getWidth() // This big is the Text in the TextField
                + t.getPadding().getLeft() + t.getPadding().getRight() // Add the padding of the TextField
                + 2d; // Add some spacing
        t.setPrefWidth(width); // Set the width
        t.positionCaret(t.getCaretPosition()); // If you remove this line, it flashes a little bit
    }

}
