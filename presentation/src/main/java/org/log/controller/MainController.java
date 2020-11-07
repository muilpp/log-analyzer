package org.log.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.log.service.LogFileInteractor;
import org.log.usecases.LogFileExporterImpl;
import org.log.usecases.LogFileFilterImpl;
import org.log.usecases.LogFileOpenerImpl;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    public MenuBar menuBar;
    @FXML
    public MenuItem openFileMenu, exportFileMenu, exitFileMenu;
    @FXML
    public MenuItem editFilters;
    @FXML
    public CheckMenuItem connectivityFilterMenu, georedFilterMenu, rptFilterMenu, sipFilterMenu;
    @FXML
    public ListView<String> logFileList;

    private List<String> originalList;
    private final List<String> wordsToFilter = new ArrayList<>();
    private LogFileInteractor logFileInteractor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logFileInteractor = new LogFileInteractor(new LogFileFilterImpl(), new LogFileOpenerImpl(), new LogFileExporterImpl());
    }

    public void handleGeoredFilterClick() {
        if (georedFilterMenu.isSelected()) {
            wordsToFilter.add("geored");
        } else {
            wordsToFilter.remove("geored");
        }
        filterLog();
    }

    public void handleConnectivityFilterClick() {
        if (connectivityFilterMenu.isSelected()) {
            wordsToFilter.add("connectivity");
        } else {
            wordsToFilter.remove("connectivity");
        }
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
    }
}