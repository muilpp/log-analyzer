package org.log.presentation.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.log.application.service.LogFileInteractor;
import org.log.application.usecases.FilterReader;
import org.log.application.usecases.LogFileExporterImpl;
import org.log.application.usecases.LogFileFilterImpl;
import org.log.application.usecases.LogFileOpenerImpl;
import org.log.infrastructure.FilePersistor;

import java.net.URL;
import java.util.*;

public class TabController implements Initializable {
    @FXML
    public TextArea manualFilterIncludeText, manualFilterExcludeText;
    @FXML
    public CheckBox sortLogCheckBox;
    @FXML
    public TextField filterMatchesText;
    @FXML
    public ListView<String> sortedLogFileList, originalLogFileList;
    @FXML
    public HBox filterCheckBoxes;

    private final List<String> manualFiltersToInclude = new ArrayList<>();
    private final List<String> manualFiltersToExclude = new ArrayList<>();

    private LogFileInteractor logFileInteractor;
    private List<String> originalList = new ArrayList<>();
    private final List<String> selectedMenuFilters = new ArrayList<>();
    private final KeyCombination keyCombinationShiftC = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
    private final FilterReader filterReader = new FilterReader(new FilePersistor());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logFileInteractor = new LogFileInteractor(new LogFileFilterImpl(), new LogFileOpenerImpl(), new LogFileExporterImpl());
        selectedMenuFilters.clear();
        initializeTabElements();
    }

    public void setData(List<String> list) {
        this.originalList = new ArrayList<>(list);

        originalLogFileList.getItems().addAll(originalList);
        sortedLogFileList.getItems().addAll(originalList);
    }

    public void handleManualFilterClick(ActionEvent actionEvent) {
        updateManualFilters();
        filterLog();
    }

    private void filterLog() {
        final List<String> filtersToInclude = new ArrayList<>(selectedMenuFilters);
        filtersToInclude.addAll(manualFiltersToInclude);

        List<String> filteredList = logFileInteractor.filterListBy(originalList, filtersToInclude, manualFiltersToExclude);
        sortedLogFileList.getItems().clear();
        sortedLogFileList.getItems().addAll(filteredList);
        filterMatchesText.setText(sortedLogFileList.getItems().size() + " matches found.");
        System.out.println("List size after filtering: " + sortedLogFileList.getItems().size());

        if(sortLogCheckBox.isSelected()) {
            sortLogFileByDate();
        }
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
        sortedLogFileList.setOnKeyPressed(copyKeyEventHandler(sortedLogFileList));
        originalLogFileList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        originalLogFileList.setOnKeyPressed(copyKeyEventHandler(originalLogFileList));

        loadCheckBoxFilters();
    }

    public void loadCheckBoxFilters() {
        filterReader.readAllFilters().forEach(f -> {
            CheckBox filterCheckBox = new CheckBox(f.getFilterName());
            filterCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean isSelected) {
                    String[] dataSplit = f.getFilterData().split("\\|");
                    if (isSelected) {
                        System.out.println("Adding filter: " + f.getFilterData());
                        selectedMenuFilters.addAll(Arrays.asList(dataSplit));
                    } else {
                        System.out.println("Removing filter: " + f.getFilterData());
                        selectedMenuFilters.removeAll(Arrays.asList(dataSplit));
                    }
                    filterLog();
                }
            });

            VBox vBox = new VBox(filterCheckBox);
            vBox.setAlignment(Pos.CENTER_LEFT);
            vBox.setSpacing(20);
            vBox.setPadding(new Insets(0, 15, 0, 0));
            filterCheckBoxes.getChildren().add(0, vBox);
        });
    }

    private EventHandler<? super KeyEvent> copyKeyEventHandler(final ListView<String> logFileList) {
        return event -> {
            if (keyCombinationShiftC.match(event)) {
                List<String> logList = logFileList.getSelectionModel().getSelectedItems();

                final ClipboardContent content = new ClipboardContent();
                StringBuilder stringBuilder = new StringBuilder();
                for (String log : logList) {
                    System.out.println("Found selected: " + log);
                    stringBuilder.append(log).append("\n");
                }
                stringBuilder = stringBuilder.replace(stringBuilder.lastIndexOf("\n"), stringBuilder.lastIndexOf("\n")+2, "");
                System.out.println("Added to clipboard: " + stringBuilder.toString());
                content.putString(stringBuilder.toString());
                Clipboard.getSystemClipboard().setContent(content);
            }
        };
    }

    public void handleSortLogFileClick(ActionEvent actionEvent) {
        if (sortLogCheckBox.isSelected()) {
            sortLogFileByDate();
        } else {
            filterLog();
        }
    }

    private void sortLogFileByDate() {
        List<String> sortedLogList = new ArrayList<>(sortedLogFileList.getItems());
        Collections.sort(sortedLogList);
        sortedLogList.removeAll(Arrays.asList("", null));
        sortedLogFileList.getItems().clear();
        sortedLogFileList.getItems().addAll(sortedLogList);
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