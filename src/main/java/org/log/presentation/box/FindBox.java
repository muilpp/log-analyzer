package org.log.presentation.box;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FindBox {

    private static String textToFind;

    public static String showFindBox() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setHeight(150);
        window.setWidth(800);
        window.setTitle("Find");
        window.setAlwaysOnTop(true);

        HBox findBox = new HBox(40);
        findBox.setAlignment(Pos.BOTTOM_CENTER);
        findBox.prefHeight(100);
        findBox.prefWidth(700);

        TextField textField = new TextField();
        textField.setMinWidth(600);
        textField.setAlignment(Pos.CENTER);

        textField.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                textToFind = textField.getText();
                window.close();
            }
        });

        Button findButton = new Button("Find");
        findButton.setOnAction(actionEvent -> {
            textToFind = textField.getText();
            window.close();
        });

        VBox layout = new VBox(30);
        layout.setPadding(new Insets(15, 0, 0, 0));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.prefHeight(200);
        layout.prefWidth(800);
        findBox.getChildren().addAll(textField, findButton);
        layout.getChildren().addAll(findBox);

        window.setScene(new Scene(layout));
        window.showAndWait();
        return textToFind;
    }
}
