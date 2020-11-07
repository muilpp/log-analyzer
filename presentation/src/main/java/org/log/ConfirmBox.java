package org.log;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmBox {

    private static boolean answer;

    public static boolean display(String title, String question) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setHeight(300);
        window.setWidth(300);
        window.setTitle(title);

        Label confirmLabel = new Label(question);
        Button yesButton = new Button("Yes");
        yesButton.setOnAction(actionEvent -> {
            answer = true;
            window.close();
        });

        Button noButton = new Button("No");
        noButton.setOnAction(actionEvent -> {
            answer = false;
            window.close();
        });

        VBox layout = new VBox();
        layout.getChildren().addAll(confirmLabel, yesButton, noButton);

        window.setScene(new Scene(layout));
        window.showAndWait();
        return answer;
    }
}
