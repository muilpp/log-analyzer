package org.log.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.log.entities.Filter;
import org.log.persistance.FilePersistor;
import org.log.service.LogFileInteractor;
import org.log.usecases.*;

import java.io.File;
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

    private List<String> originalList;
    private final List<String> wordsToFilter = new ArrayList<>();
    private LogFileInteractor logFileInteractor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logFileInteractor = new LogFileInteractor(new LogFileFilterImpl(), new LogFileOpenerImpl(), new LogFileExporterImpl());
        initFilterMenu();
    }

    private void initFilterMenu() {
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
                wordsToFilter.addAll(Arrays.asList(dataSplit));
            } else {
                System.out.println("Removing filter: " + filterData);
                wordsToFilter.removeAll(Arrays.asList(dataSplit));
            }
        } else System.out.println("No filter found");

        filterLog();
    }

    private void filterLog() {
        List<String> filteredList = logFileInteractor.filterListBy(originalList, wordsToFilter);
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
        FilterCreator filterCreator = new FilterCreator(new FilePersistor());
        System.out.println(filterCreator.create("filterName", "filters!!"));

        FilterReader filterReader = new FilterReader(new FilePersistor());
        List<Filter> filterList = filterReader.read();

        filterList.forEach(System.out::println);
    }
}