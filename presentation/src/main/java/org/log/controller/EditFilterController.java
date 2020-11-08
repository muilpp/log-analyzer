package org.log.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.log.entities.Filter;
import org.log.persistance.FilePersistor;
import org.log.usecases.FilterEraser;
import org.log.usecases.FilterReader;
import org.log.usecases.FilterUpdater;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EditFilterController implements Initializable{

    @FXML
    public ListView<String> filterListView;
    @FXML
    public TextArea filterContentText;
    @FXML
    public Button createFilterButton;
    @FXML
    public Button deleteFilterButton;
    @FXML
    public Button saveFilterButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateFilters();
    }

    private void populateFilters() {
        FilterReader filterReader = new FilterReader(new FilePersistor());
        List<Filter> filterList = filterReader.read();
        filterListView.getItems().clear();

        filterList.forEach(filter -> {
            System.out.println("Filter found -> " + filter.getFilterName());
            filterListView.getItems().add(filter.getFilterName());
        });

        filterListView.setOnMouseClicked(mouseEvent -> {
            String filterName = filterListView.getSelectionModel().getSelectedItem();
            System.out.println("clicked on " + filterName);

            filterList.forEach(filter -> {
                if (filter.getFilterName().equalsIgnoreCase(filterName)) {
                    filterContentText.setText(filter.getFilterData());
                }
            });
        });
    }

    public void handleOnSaveFilterClick(ActionEvent actionEvent) {
        FilterUpdater filterUpdater = new FilterUpdater(new FilePersistor());

        String filterName = filterListView.getSelectionModel().getSelectedItem();
        int filterListIndex = filterListView.getSelectionModel().getSelectedIndex();
        String filterData = filterContentText.getText();

        boolean isUpdated = filterUpdater.update(filterName, filterData);
        if (isUpdated) {
            System.out.println("Updated!");
            populateFilters();
        } else System.out.println("Not updated!");
        filterListView.getSelectionModel().select(filterListIndex);
    }

    public void handleOnCreateFilterClick(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("NewFilterDialog.fxml"));
            Parent newFilterScene = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setTitle("New Filter");
            stage.setScene(new Scene(newFilterScene));
            stage.setAlwaysOnTop(true);
            //when closing edit filter, reload filter list
            stage.setOnHidden(e -> populateFilters());
            stage.show();
        } catch (IOException e) {
            System.out.println("Could not load new filter stage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleOnDeleteFilterClick(ActionEvent actionEvent) {
        FilterEraser filterEraser = new FilterEraser(new FilePersistor());
        String filterName = filterListView.getSelectionModel().getSelectedItem();

        boolean isDeleted = filterEraser.delete(filterName);
        if (isDeleted) {
            System.out.println("Deleted!");
            populateFilters();
            filterContentText.setText("");
        } else System.out.println("Not deleted!");
    }
}
