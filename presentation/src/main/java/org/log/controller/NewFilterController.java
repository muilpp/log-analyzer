package org.log.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.log.persistance.FilePersistor;
import org.log.usecases.FilterCreator;

import java.net.URL;
import java.util.ResourceBundle;

public class NewFilterController implements Initializable {
    public TextField filterNameText;
    public TextArea filterContentText;
    public Button cancelButton;
    public Button createFilterButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void handleCreateFilterButtonClick(ActionEvent actionEvent) {
        Stage stage = (Stage) createFilterButton.getScene().getWindow();
        if (filterNameText.getText().isBlank() || filterContentText.getText().isBlank()) {
            stage.close();
        }

        FilterCreator filterCreator = new FilterCreator(new FilePersistor());
        filterCreator.create(filterNameText.getText(), filterContentText.getText());

        stage.close();
    }

    public void handleCancelButtonClick(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
