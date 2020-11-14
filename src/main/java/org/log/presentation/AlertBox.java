package org.log.presentation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {

    public static boolean display(String title, String text) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setHeight(150);
        window.setWidth(500);
        window.setTitle(title);
        window.setAlwaysOnTop(true);

        HBox buttonBox = new HBox(40);
        buttonBox.setAlignment(Pos.BOTTOM_CENTER);
        buttonBox.prefHeight(100);
        buttonBox.prefWidth(200);
        Button confirm = new Button("Ok");
        confirm.setOnAction(actionEvent -> {
            window.close();
        });

        VBox layout = new VBox(30);
        layout.setPadding(new Insets(15, 0, 0, 0));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.prefHeight(200);
        layout.prefWidth(300);
        buttonBox.getChildren().add(confirm);
        Label confirmLabel = new Label(text);
        layout.getChildren().addAll(confirmLabel, buttonBox);

        window.setScene(new Scene(layout));
        window.show();

        return true;
    }
}
