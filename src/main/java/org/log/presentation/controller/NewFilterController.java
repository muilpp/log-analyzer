package org.log.presentation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.log.application.usecases.FilterCreator;
import org.log.application.usecases.FilterReader;
import org.log.domain.entities.Filter;
import org.log.infrastructure.FilePersistor;

import java.net.URL;
import java.util.List;
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

        FilterReader filterReader = new FilterReader(new FilePersistor());
        List<Filter> filterList = filterReader.readAllFilters();

        boolean filterAlreadyExists = false;
        for (Filter filter : filterList) {
            if (filter.getFilterName().equalsIgnoreCase(filterNameText.getText().trim())) {
                filterAlreadyExists = true;
                break;
            }
        }

        if (filterAlreadyExists) {
            filterNameText.setStyle("-fx-border-color: #FF0000");
            String filterName = filterNameText.getText();
            filterNameText.setText("FILTER NAME: " + filterName + " ALREADY EXISTS, CHANGE IT!");
            System.out.println("Filtre ja existeix, poso fondo vermell!");
        } else {
            FilterCreator filterCreator = new FilterCreator(new FilePersistor());
            filterCreator.create(filterNameText.getText(), filterContentText.getText());
            System.out.println("Tot ok, creo filtre");

            stage.close();
        }
    }

    public void handleCancelButtonClick(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
