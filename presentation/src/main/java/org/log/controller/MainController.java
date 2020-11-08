package org.log.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.log.entities.Filter;
import org.log.persistance.FilePersistor;
import org.log.service.LogFileInteractor;
import org.log.usecases.FilterReader;
import org.log.usecases.LogFileExporterImpl;
import org.log.usecases.LogFileFilterImpl;
import org.log.usecases.LogFileOpenerImpl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

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
    public ListView<String> logFileList;
    @FXML
    public TextArea manualFilterText;

    private List<String> originalList;
    private final List<String> manualFilters = new ArrayList<>();
    private final List<String> selectedFilters = new ArrayList<>();
    private LogFileInteractor logFileInteractor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logFileInteractor = new LogFileInteractor(new LogFileFilterImpl(), new LogFileOpenerImpl(), new LogFileExporterImpl());
        loadFilterMenu();

        manualFilterText.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                keyEvent.consume(); // necessary to prevent event handlers for this event
                handleManualFilterClick(null);
            }
        });
    }

    public void loadFilterMenu() {
        //This is done to reload elements after coming back from the EditFilter menu, would be better to have listeners instead of this
        //https://stackoverflow.com/questions/29639881/javafx-how-to-use-a-method-in-a-controller-from-another-controller
        filtersMenu.getItems().removeIf(item -> (item instanceof CheckMenuItem));
        selectedFilters.clear();
        FilterReader filterReader = new FilterReader(new FilePersistor());
        List<Filter> filterList = filterReader.read();

        System.out.println("Filters found: " + filterList.size());

        filterList.forEach(filter -> {
            final CheckMenuItem checkMenuItem = new CheckMenuItem(filter.getFilterName());
            checkMenuItem.setOnAction(e -> {
                handleFilterMenuClick(checkMenuItem);
            });
            System.out.println("Filter found: " + filter.getFilterName());
            filtersMenu.getItems().add(checkMenuItem);
        });
    }

    public void handleFilterMenuClick(final CheckMenuItem menuItem) {
        FilterReader filterReader = new FilterReader(new FilePersistor());
        List<Filter> filterList = filterReader.read();

        Optional<Filter> foundFilter = filterList.stream().filter(filter -> filter.getFilterName().equalsIgnoreCase(menuItem.getText())).findFirst();

        if (foundFilter.isPresent()) {
            System.out.println("Filter is present");
            String filterData = foundFilter.get().getFilterData();
            String[] dataSplit = filterData.split("\\|");
            if (menuItem.isSelected()) {
                System.out.println("Adding filter: " + filterData);
                selectedFilters.addAll(Arrays.asList(dataSplit));
            } else {
                System.out.println("Removing filter: " + filterData);
                selectedFilters.removeAll(Arrays.asList(dataSplit));
            }
        } else System.out.println("No filter found");

        filterLog();
    }

    private void filterLog() {
        final List<String> allFilters = new ArrayList<>(selectedFilters);
        allFilters.addAll(manualFilters);

        List<String> filteredList = logFileInteractor.filterListBy(originalList, allFilters);
        logFileList.getItems().clear();
        logFileList.getItems().addAll(filteredList);
        System.out.println("List size after filtering: " + logFileList.getItems().size());
    }

    public void handleOpenFileMenuClick() {
        System.out.println("Click on open file!");
        FileChooser fileChooser = new FileChooser();
        File logFile = fileChooser.showOpenDialog(null);

        if (logFile != null) {
            System.out.println("Log file selected: " + logFile.getAbsolutePath());
            openLogFile(logFile.getPath());
        } else {
            System.out.println("Could not open the fucking file!");
        }
    }

    private void openLogFile(String logFilePath) {
        originalList = logFileInteractor.loadLogFile(logFilePath);
        System.out.println("List size after loading: " + originalList.size());
        logFileList.getItems().clear();
        logFileList.getItems().addAll(originalList);
    }

    public void handleExportFileMenuClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export log file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text File", "*.txt*"));
        File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());

        if (file != null) {
            logFileInteractor.exportToLog(file, logFileList.getItems());
        }
    }

    public void handleExitMenuClick(ActionEvent actionEvent) {
        ((Stage)menuBar.getScene().getWindow()).close();
    }

    public void handleOnEditFiltersClick(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("EditFilters.fxml"));
            Parent editScene = fxmlLoader.load();
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

    public void handleManualFilterClick(ActionEvent actionEvent) {
        manualFilters.clear();
        String[] manualFilter = manualFilterText.getText().split("\\|");

        for (String filter : manualFilter) {
            if (!filter.isBlank()) {
                manualFilters.add(filter);
            }
        }

        filterLog();
    }
}