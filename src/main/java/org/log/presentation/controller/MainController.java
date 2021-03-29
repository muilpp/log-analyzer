package org.log.presentation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.log.application.service.LogFileInteractor;
import org.log.application.usecases.LogFileExporterImpl;
import org.log.application.usecases.LogFileFilterImpl;
import org.log.application.usecases.LogFileOpenerImpl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
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

    private static final Logger logger = LogManager.getLogger(MainController.class);
    private LogFileInteractor logFileInteractor;
    private Tab currentTab;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logFileInteractor = new LogFileInteractor(new LogFileFilterImpl(), new LogFileOpenerImpl(), new LogFileExporterImpl());

        logTabPane.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, tab, t1) -> setCurrentTab(t1)
        );

        setupDragDrop();
    }

    private void setupDragDrop() {
        mainVerticalBox.setOnDragOver(event -> {
            if (event.getGestureSource() != mainVerticalBox
                    && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        mainVerticalBox.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
            }
            openFiles(db.getFiles());
            event.setDropCompleted(success);

            event.consume();
        });
    }

    public void handleOpenFileMenuClick() {
        logger.debug("Click on open file!");
        FileChooser fileChooser = new FileChooser();
        List<File> logFileList = fileChooser.showOpenMultipleDialog(null);

        openFiles(logFileList);
    }

    private void openFiles(List<File> files) {
        files.forEach(file -> {
            if (file != null) {
                logger.debug("Opening log file: " + file.getAbsolutePath());
                Stage stage = (Stage) menuBar.getScene().getWindow();
                stage.setTitle(file.getName());
                openLogFile(file);
            } else {
                logger.error("MainController.handleOpenFileMenuClick:: Could not open the file because it's null");
            }
        });
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
            logger.error("MainController.openLogFile:: Could not load file to filter: " + logFile);
            e.printStackTrace();
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
            logger.error("MainController.handleOnEditFiltersClick:: Could not load edit stage: " + e);
        }
    }

    public void setCurrentTab(Tab currentTab) {
        this.currentTab = currentTab;
    }
}