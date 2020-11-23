package org.log.presentation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
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
    public ListView<String> sortedLogFileList, originalLogFileList;
    @FXML
    public TextArea manualFilterIncludeText, manualFilterExcludeText;
    @FXML
    public CheckBox sortLogCheckBox;
    @FXML
    public TextField filterMatchesText;
    @FXML
    public TabPane logTabPane;
    @FXML
    public VBox mainVerticalBox;

    private List<String> originalList;
    private final List<String> manualFiltersToInclude = new ArrayList<>();
    private final List<String> manualFiltersToExclude = new ArrayList<>();
    private final List<String> selectedFilters = new ArrayList<>();
    private LogFileInteractor logFileInteractor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logFileInteractor = new LogFileInteractor(new LogFileFilterImpl(), new LogFileOpenerImpl(), new LogFileExporterImpl());
        loadFilterMenu();
    }

    public void loadFilterMenu() {
        //This is done to reload elements after coming back from the EditFilter menu, would be better to have listeners instead of this
        //https://stackoverflow.com/questions/29639881/javafx-how-to-use-a-method-in-a-controller-from-another-controller
        filtersMenu.getItems().removeIf(item -> (item instanceof CheckMenuItem));
        selectedFilters.clear();
        FilterReader filterReader = new FilterReader(new FilePersistor());
        List<Filter> filterList = filterReader.readAllFilters();

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
        List<Filter> filterList = filterReader.readAllFilters();

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
        final List<String> filtersToInclude = new ArrayList<>(selectedFilters);
        filtersToInclude.addAll(manualFiltersToInclude);

        List<String> filteredList = logFileInteractor.filterListBy(originalList, filtersToInclude, manualFiltersToExclude);
        sortedLogFileList.getItems().clear();
        sortedLogFileList.getItems().addAll(filteredList);
        filterMatchesText.setText(sortedLogFileList.getItems().size() + " matches found.");
        System.out.println("List size after filtering: " + sortedLogFileList.getItems().size());
    }

    public void handleOpenFileMenuClick() {
        System.out.println("Click on open file!");
        FileChooser fileChooser = new FileChooser();
        File logFile = fileChooser.showOpenDialog(null);

        if (logFile != null) {
            System.out.println("Log file selected: " + logFile.getAbsolutePath());
            Stage stage = (Stage) menuBar.getScene().getWindow();
            stage.setTitle(logFile.getName());
            openLogFile(logFile.getPath());

            //Tab tabPane = new Tab(logFile.getName());
            //logTabPane.getTabs().add(tabPane);
        } else {
            System.out.println("Could not open the fucking file!");
        }
    }

    private void openLogFile(String logFilePath) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/TabFilter.fxml"));

        try {
            TabPane newLoadedPane =  FXMLLoader.load(getClass().getResource("/TabFilter.fxml"));
            mainVerticalBox.getChildren().add(newLoadedPane);
            initializeTabElements();
        } catch (IOException e) {
            System.out.println("Could load file to filter: " + e);
        }

        originalList = logFileInteractor.loadLogFile(logFilePath);
        System.out.println("List size after loading: " + originalList.size());
        sortedLogFileList.getItems().clear();
        sortedLogFileList.getItems().addAll(originalList);
        originalLogFileList.getItems().clear();
        originalLogFileList.getItems().addAll(originalList);
    }

    private void initializeTabElements() {
        manualFilterIncludeText.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                keyEvent.consume(); // necessary to prevent event handlers for this event
                handleManualFilterClick(null);
            }
        });

        manualFilterExcludeText.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                keyEvent.consume(); // necessary to prevent event handlers for this event
                handleManualFilterClick(null);
            }
        });

        sortedLogFileList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        sortedLogFileList.setOnKeyPressed(keyEvent -> {
            List<String> logList = sortedLogFileList.getSelectionModel().getSelectedItems();

            final ClipboardContent content = new ClipboardContent();
            StringBuilder stringBuilder = new StringBuilder();
            for (String log : logList) {
                System.out.println("Found selected: " + log);
                stringBuilder.append(log).append("\n");
            }
            System.out.println("Added to clipboard: " + stringBuilder.toString());
            content.putString(stringBuilder.toString());
            Clipboard.getSystemClipboard().setContent(content);
        });
    }

    public void handleExportFileMenuClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export log file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text File", "*.txt*"));
        File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());

        if (file != null) {
            logFileInteractor.exportToLog(file, sortedLogFileList.getItems());
        }
    }

    public void handleExitMenuClick(ActionEvent actionEvent) {
        ((Stage)menuBar.getScene().getWindow()).close();
    }

    public void handleOnEditFiltersClick(ActionEvent actionEvent) {
        try {
            //URL url = Paths.get("./src/main/resources/EditFilters.fxml").toUri().toURL();
            //Parent editScene = FXMLLoader.load(url);

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

    public void handleManualFilterClick(ActionEvent actionEvent) {
        updateManualFilters();
        filterLog();
    }

    public void handleSortLogFileClick(ActionEvent actionEvent) {
        if (sortLogCheckBox.isSelected()) {
            List<String> sortedLogList = new ArrayList<>(sortedLogFileList.getItems());
            Collections.sort(sortedLogList);
            sortedLogList.removeAll(Arrays.asList("", null));
            sortedLogFileList.getItems().clear();
            sortedLogFileList.getItems().addAll(sortedLogList);
        } else {
            filterLog();
        }
    }

    private void updateManualFilters() {
        updateManualIncludeFilter();
        updateManualExcludeFilter();
    }

    private void updateManualIncludeFilter() {
        manualFiltersToInclude.clear();
        String[] manualFilterInclude = manualFilterIncludeText.getText().split("\\|");

        for (String filterToInclude : manualFilterInclude) {
            if (!filterToInclude.isBlank()) {
                manualFiltersToInclude.add(filterToInclude);
            }
        }
    }

    private void updateManualExcludeFilter() {
        manualFiltersToExclude.clear();
        String[] manualFilterExclude = manualFilterExcludeText.getText().split("\\|");

        for (String filterToExclude : manualFilterExclude) {
            if (!filterToExclude.isBlank()) {
                manualFiltersToExclude.add(filterToExclude);
            }
        }
    }

    public void handleMouseSortedListClick(MouseEvent mouseEvent) {
        String selectedSortedListElement = sortedLogFileList.getSelectionModel().getSelectedItem();
        originalLogFileList.getSelectionModel().select(selectedSortedListElement);
        //originalLogFileList.getFocusModel().focus(selectedSortedListElement);
        originalLogFileList.scrollTo(selectedSortedListElement);
    }
}