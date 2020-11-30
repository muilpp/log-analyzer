package org.log.presentation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.log.application.service.LogFileInteractor;
import org.log.application.usecases.FilterReader;
import org.log.application.usecases.LogFileExporterImpl;
import org.log.application.usecases.LogFileFilterImpl;
import org.log.application.usecases.LogFileOpenerImpl;
import org.log.domain.entities.Filter;
import org.log.infrastructure.FilePersistor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    @FXML
    public MenuBar menuBar;
    @FXML
    public Menu filtersMenu;
    @FXML
    public MenuItem openFileMenu, exportFileMenu, exitFileMenu;
    @FXML
    public MenuItem editFilters;
    @FXML
    public TabPane logTabPane;
    @FXML
    public VBox mainVerticalBox;

    private LogFileInteractor logFileInteractor;
    private Tab currentTab;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logFileInteractor = new LogFileInteractor(new LogFileFilterImpl(), new LogFileOpenerImpl(), new LogFileExporterImpl());
        loadFilterMenu();

        logTabPane.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, tab, t1) -> setCurrentTab(t1)
        );
    }

    public void loadFilterMenu() {
        //This is done to reload elements after coming back from the EditFilter menu, would be better to have listeners instead of this
        //https://stackoverflow.com/questions/29639881/javafx-how-to-use-a-method-in-a-controller-from-another-controller
        filtersMenu.getItems().removeIf(item -> (item instanceof CheckMenuItem));
        FilterReader filterReader = new FilterReader(new FilePersistor());
        List<Filter> filterList = filterReader.readAllFilters();

        System.out.println("Filters found: " + filterList.size());

        filterList.forEach(filter -> {
            final CheckMenuItem checkMenuItem = new CheckMenuItem(filter.getFilterName());
            checkMenuItem.setOnAction(e -> handleFilterMenuClick(checkMenuItem));
            System.out.println("Filter found: " + filter.getFilterName());
            filtersMenu.getItems().add(checkMenuItem);
        });
    }

    public void handleFilterMenuClick(final CheckMenuItem menuItem) {
        FilterReader filterReader = new FilterReader(new FilePersistor());
        List<Filter> filterList = filterReader.readAllFilters();

        Optional<Filter> foundFilter = filterList.stream().filter(filter -> filter.getFilterName().equalsIgnoreCase(menuItem.getText())).findFirst();

        if (foundFilter.isPresent()) {
            System.out.println("Filter is present");
            String filterData = foundFilter.get().getFilterData();
            String[] dataSplit = filterData.split("\\|");
            if (menuItem.isSelected()) {
                System.out.println("Adding filter: " + filterData);
                //TODO Apply filter!
                //selectedFilters.addAll(Arrays.asList(dataSplit));
            } else {
                System.out.println("Removing filter: " + filterData);
                //TODO Apply filter!
                //selectedFilters.removeAll(Arrays.asList(dataSplit));
            }
        } else System.out.println("No filter found");

        //TODO Filter current log after applying filters!
    }

    public void handleOpenFileMenuClick() {
        System.out.println("Click on open file!");
        FileChooser fileChooser = new FileChooser();
        File logFile = fileChooser.showOpenDialog(null);

        if (logFile != null) {
            System.out.println("Log file selected: " + logFile.getAbsolutePath());
            Stage stage = (Stage) menuBar.getScene().getWindow();
            stage.setTitle(logFile.getName());
            openLogFile(logFile);
        } else {
            System.out.println("Could not open the file!");
        }
    }

    private void openLogFile(File logFile) {
        try {
            Tab newTab = new Tab(logFile.getName());
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/TabFilter.fxml"));
            newTab.setContent(loader.load());
            logTabPane.getTabs().add(newTab);
            TabController tabController = loader.getController();
            tabController.setData(logFileInteractor.loadLogFile(logFile.getPath()));
            logTabPane.getSelectionModel().select(newTab);
        } catch (IOException e) {
            System.out.println("Could not load file to filter: " + e);
        }
    }

    private List<MenuItem> getSelectedMenuFilters() {
        return filtersMenu.getItems().stream().filter(f -> ((CheckMenuItem)f).isSelected()).collect(Collectors.toList());
    }

    public void handleExportFileMenuClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export log file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text File", "*.txt*"));
        File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());

        if (file != null) {
            ListView<String> sortedListView = (ListView<String>)currentTab.getContent().getScene().lookup("#sortedLogFileList");
            logFileInteractor.exportToLog(file, sortedListView.getItems());
        }
    }

    public void handleExitMenuClick(ActionEvent actionEvent) {
        ((Stage)menuBar.getScene().getWindow()).close();
    }

    public void handleOnEditFiltersClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/EditFilters.fxml"));
            Parent editScene = loader.load();

            Stage stage = new Stage();
            stage.initOwner((menuBar.getScene().getWindow()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setTitle("Edit Filters");
            stage.setScene(new Scene(editScene));
            //when closing edit filter, reload menu
            stage.setOnHidden(e -> loadFilterMenu());
            stage.show();
        } catch (IOException e) {
            System.out.println("Could not load edit stage: " + e);
        }
    }

    public void setCurrentTab(Tab currentTab) {
        this.currentTab = currentTab;
    }
}