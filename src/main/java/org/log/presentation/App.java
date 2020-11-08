package org.log.presentation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

public class App extends Application {

    private Stage window;

    @Override
    public void start(Stage stage) throws IOException {
        window = stage;

        URL url = Paths.get("./src/main/resources/Main.fxml").toUri().toURL();
        Parent root = FXMLLoader.load(url);
        //Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("/Main.fxml"));
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

    private void closeProgram() {
        boolean exit = ConfirmBox.display("Exit", "Are you sure you want to leave?");
        if (exit) {
            System.out.println("File is saved!");
            window.close();
        } else {
            System.out.println("Good decision");
        }
    }

    public static void main(String[] args) {
        Application.launch();
    }
}