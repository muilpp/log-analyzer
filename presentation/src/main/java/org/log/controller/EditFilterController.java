package org.log.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import org.log.entities.Filter;
import org.log.persistance.FilePersistor;
import org.log.usecases.FilterReader;

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
        //FilterCreator filterCreator = new FilterCreator(new FilePersistor());
        //filterCreator.create("filterName", "filters!!");
//
    }

    private void populateFilters() {
        FilterReader filterReader = new FilterReader(new FilePersistor());
        List<Filter> filterList = filterReader.read();

        filterList.forEach(filter -> {
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

//
//        filterList.forEach(filter -> {
//            System.out.println(filter.getFilterName());
//            System.out.println(filter.getFilterData());
//            System.out.println("--------------------");
//        });
    }
}
