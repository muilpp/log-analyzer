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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.log.application.service.LogFileInteractor;
import org.log.application.usecases.FilterReader;
import org.log.application.usecases.LogFileExporterImpl;
import org.log.application.usecases.LogFileFilterImpl;
import org.log.application.usecases.LogFileOpenerImpl;
import org.log.application.util.TimestampLogPredicate;
import org.log.infrastructure.FilePersistor;
import org.log.presentation.FindBox;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TabController implements Initializable {
    @FXML
    public TextArea manualFilterIncludeText, manualFilterExcludeText;
    @FXML
    public CheckBox sortLogCheckBox, removeNoDateLogCheckBox;
    @FXML
    public TextField filterMatchesText, searchFoundMatches;
    @FXML
    public ListView<String> sortedLogFileList, originalLogFileList;
    @FXML
    public HBox filterCheckBoxes;
    @FXML
    public BorderPane borderPaneTabFilter;
    @FXML
    public Button findPrevious, findNext;

    private final List<String> manualFiltersToInclude = new ArrayList<>();
    private final List<String> manualFiltersToExclude = new ArrayList<>();

    private LogFileInteractor logFileInteractor;
    private List<String> originalList = new ArrayList<>();
    private final List<String> selectedMenuFilters = new ArrayList<>();
    private final KeyCombination keyCombinationControlC = new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN);
    private final KeyCombination keyCombinationControlF = new KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN);
    private final FilterReader filterReader = new FilterReader(new FilePersistor());
    //private static final String HIGHLIGHT_CLASS = "search-highlight";
    private static final String STANDARD_STYLES = "-fx-background-color: -fx-selection-bar;-fx-background-color: -fx-focus-color, -fx-cell-focus-inner-border, -fx-selection-bar;-fx-background-color: lightgray;-fx-background-color: -fx-cell-hover-color;-fx-background-color: -fx-focus-color, -fx-cell-focus-inner-border, -fx-cell-hover-color;-fx-background-color: linear-gradient(to right, derive(-fx-accent,-7%), derive(-fx-accent,-25%));";

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
        final String selectedSortedListElement = sortedLogFileList.getSelectionModel().getSelectedItem();
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
        if (removeNoDateLogCheckBox.isSelected()) {
            removeNoDateLog();
        }

        sortedLogFileList.getSelectionModel().select(selectedSortedListElement);
        sortedLogFileList.scrollTo(selectedSortedListElement);
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
        borderPaneTabFilter.setOnKeyPressed(findKeyEventHandler(sortedLogFileList));
        originalLogFileList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        originalLogFileList.setOnKeyPressed(copyKeyEventHandler(originalLogFileList));

        //sortedLogFileList.getStylesheets().add(getClass().getResource("/highlighter.css").toExternalForm());

        sortedLogFileList.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView) {
                return new ListCell<>(){
                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);
                        if (s == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(s);

                            if (s.toLowerCase().contains("warning".toLowerCase())){
                                setStyle("-fx-background-color: yellow;");
                            } else if (s.toLowerCase().contains("error".toLowerCase())) {
                                setStyle("-fx-background-color: tomato;");
                            } else {
                                //leave it like it is, if not it gets the same styles as above
                                setStyle("");
                            }
                        }
                    }
                };
            }
        });

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
            if (keyCombinationControlC.match(event)) {
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

    private EventHandler<? super KeyEvent> findKeyEventHandler(final ListView<String> logFileList) {
        return event -> {
            if (keyCombinationControlF.match(event)) {
                String textToFind = FindBox.showFindBox();
                System.out.println("Text to find: " + textToFind);

                AtomicInteger matchesFound = new AtomicInteger(0);
                final List<Integer> indexList = new ArrayList<>();
                for (int i = 0; i < logFileList.getItems().size(); i++) {
                    if (logFileList.getItems().get(i).contains(textToFind.toLowerCase())) {
                        System.out.println("Found selected in: " + logFileList.getItems().get(i));
                        logFileList.getSelectionModel().select(i);
                        indexList.add(i);
                        matchesFound.incrementAndGet();
                    }
                }
                System.out.println("Matches found: " + matchesFound);
                searchFoundMatches.setText("0/"+matchesFound);

                AtomicInteger currentIndex = new AtomicInteger(0);
                findPrevious.setOnMouseClicked(mouseEvent -> {

                    for (int index : indexList) {
                        if (currentIndex.getAndIncrement() > index) {
                            logFileList.scrollTo(index);
                            searchFoundMatches.setText(currentIndex.get() + "/" + matchesFound.get());
                        }
                    }
                });

                findNext.setOnMouseClicked(mouseEvent -> {
                    int selectedIndex = logFileList.getSelectionModel().getSelectedIndex();

                    for (int index : indexList) {
                        if (selectedIndex > index) {
                            logFileList.scrollTo(index);
                            currentIndex.incrementAndGet();
                            searchFoundMatches.setText(currentIndex.get() + "/" + matchesFound.get());
                        }
                    }
                });
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

    public void handleRemoveNoDateLogClick(ActionEvent actionEvent) {
        if (removeNoDateLogCheckBox.isSelected()) {
            removeNoDateLog();
        } else {
            filterLog();
        }
    }

    private void sortLogFileByDate() {
        final String selectedSortedListElement = sortedLogFileList.getSelectionModel().getSelectedItem();
        List<String> sortedLogList = new ArrayList<>(sortedLogFileList.getItems());
        Collections.sort(sortedLogList);
        sortedLogList.removeAll(Arrays.asList("", null));
        sortedLogFileList.getItems().clear();
        sortedLogFileList.getItems().addAll(sortedLogList);

        sortedLogFileList.getSelectionModel().select(selectedSortedListElement);
        sortedLogFileList.scrollTo(selectedSortedListElement);
    }

    private void removeNoDateLog() {
        final String selectedSortedListElement = sortedLogFileList.getSelectionModel().getSelectedItem();
        List<String> sortedLogList = new ArrayList<>(sortedLogFileList.getItems());
        List<String> timestampFilteredList = sortedLogList.stream().filter(new TimestampLogPredicate()).collect(Collectors.toList());
        sortedLogFileList.getItems().clear();
        sortedLogFileList.getItems().addAll(timestampFilteredList);

        sortedLogFileList.getSelectionModel().select(selectedSortedListElement);
        sortedLogFileList.scrollTo(selectedSortedListElement);
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
        final String selectedSortedListElement = sortedLogFileList.getSelectionModel().getSelectedItem();
        originalLogFileList.getSelectionModel().select(selectedSortedListElement);
        //originalLogFileList.getFocusModel().focus(selectedSortedListElement);
        originalLogFileList.scrollTo(selectedSortedListElement);
    }

    public void handleSortedListKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.DOWN) || keyEvent.getCode().equals(KeyCode.UP)) {
            keyEvent.consume(); // necessary to prevent event handlers for this event
            final String selectedSortedListElement = sortedLogFileList.getSelectionModel().getSelectedItem();
            originalLogFileList.getSelectionModel().select(selectedSortedListElement);
            originalLogFileList.scrollTo(selectedSortedListElement);
        }
    }
}