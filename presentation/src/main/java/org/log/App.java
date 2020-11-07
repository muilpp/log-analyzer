package org.log;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private Scene mainScene, alternativeScene;
    private Stage window;

    @Override
    public void start(Stage stage) throws IOException {
        window = stage;

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Main.fxml"));
        window.setTitle("Log Analyzer");
        Scene scene = new Scene(root);
        //scene.getRoot().setStyle("-fx-base:black");
        window.setScene(scene);
        window.setMaximized(true);
        window.show();
        window.setOnCloseRequest(windowEvent -> {
            windowEvent.consume(); //we will consume this event, meaning we take over from here with our code
            closeProgram();
        });
    }

    private Button createAlternativeScene() {
        StackPane stackPane = new StackPane();
        Button button2 = new Button("Go back to scene 1");
        button2.setOnAction(actionEvent -> window.setScene(mainScene));
        stackPane.getChildren().addAll(button2);
        alternativeScene = new Scene(stackPane);

        Button buttonAlternativeScene = new Button("Go to scene 2");
        buttonAlternativeScene.setOnAction(actionEvent -> window.setScene(alternativeScene));
        return buttonAlternativeScene;
    }

    private void closeProgram() {
        boolean exit = ConfirmBox.display("Exit!", "Are you sure you wanna leave?");
        if (exit) {
            System.out.println("File is saved!");
            window.close();
        } else {
            System.out.println("Good decision");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}