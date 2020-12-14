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
import org.log.application.usecases.LogFileExporterImpl;
import org.log.application.usecases.LogFileFilterImpl;
import org.log.application.usecases.LogFileOpenerImpl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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

        logTabPane.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, tab, t1) -> setCurrentTab(t1)
        );
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
            //stage.setOnHidden(e -> loadFilterMenu());
            stage.show();
        } catch (IOException e) {
            System.out.println("Could not load edit stage: " + e);
        }
    }

    public void setCurrentTab(Tab currentTab) {
        this.currentTab = currentTab;
    }
}