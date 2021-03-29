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
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.log.application.service.LogFileInteractor;
import org.log.application.usecases.FilterReader;
import org.log.application.usecases.LogFileExporterImpl;
import org.log.application.usecases.LogFileFilterImpl;
import org.log.application.usecases.LogFileOpenerImpl;
import org.log.infrastructure.FilePersistor;
import org.log.presentation.box.FindBox;

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
    public ListView<Text> sortedLogFileList, originalLogFileList;
    @FXML
    public HBox filterCheckBoxes;
    @FXML
    public BorderPane borderPaneTabFilter;
    @FXML
    public Button findPrevious, findNext;

    private final List<String> manualFiltersToInclude = new ArrayList<>();
    private final List<String> manualFiltersToExclude = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(TabController.class);

    private LogFileInteractor logFileInteractor;
    private List<Text> originalList = new ArrayList<>();
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

    public void setData(List<Text> list) {
        this.originalList = new ArrayList<>(list);

        originalLogFileList.getItems().addAll(originalList);
        sortedLogFileList.getItems().addAll(originalList);
    }

    public void handleManualFilterClick(ActionEvent actionEvent) {
        updateManualFilters();
        filterLog();
    }

    private void filterLog() {
        final Text selectedSortedListElement = sortedLogFileList.getSelectionModel().getSelectedItem();
        final List<String> filtersToInclude = new ArrayList<>(selectedMenuFilters);
        filtersToInclude.addAll(manualFiltersToInclude);

        originalList = setTextListStyle(originalList, "");

        List<Text> filteredList = logFileInteractor.filterListBy(originalList, filtersToInclude, manualFiltersToExclude);
        if (!filtersToInclude.isEmpty()) {
            filteredList = setTextListStyle(filteredList, "-fx-fill: #60499F;-fx-font-weight:bold;");
        }

        sortedLogFileList.getItems().clear();
        sortedLogFileList.getItems().addAll(filteredList);

        filterMatchesText.setText(sortedLogFileList.getItems().size() + " matches found.");
        logger.debug("List size after filtering: " + sortedLogFileList.getItems().size());

        if(sortLogCheckBox.isSelected()) {
            sortLogFileByDate();
        }
        if (removeNoDateLogCheckBox.isSelected()) {
            removeNoDateLog();
        }

        sortedLogFileList.getSelectionModel().select(selectedSortedListElement);
        sortedLogFileList.scrollTo(selectedSortedListElement);
    }

    private List<Text> setTextListStyle(List<Text> list, String style) {
        return list.stream().map(line -> {
            line.setStyle(style);
            return line;
        }).collect(Collectors.toList());
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
        sortedLogFileList.setOnKeyPressed(handleKeyEventPressed(sortedLogFileList));
        originalLogFileList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        originalLogFileList.setOnKeyPressed(handleKeyEventPressed(originalLogFileList));

        sortedLogFileList.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Text> call(ListView<Text> stringListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Text s, boolean b) {
                        super.updateItem(s, b);
                        if (s == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(s.getText());

                            if (s.getText().toLowerCase().contains("warning".toLowerCase())) {
                                setStyle("-fx-background-color: yellow;");
                            } else if (s.getText().toLowerCase().contains("error".toLowerCase())) {
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

        var vBoxRef = new Object() {
            VBox vBox = new VBox();
        };

        var hBoxRef = new Object() {
            HBox hBox = new HBox();
        };
        hBoxRef.hBox.setId(UUID.randomUUID().toString());
        hBoxRef.hBox.setSpacing(10);
        hBoxRef.hBox.setPadding(new Insets(10));

        filterReader.readAllFilters().forEach(f -> {
            CheckBox filterCheckBox = new CheckBox(f.getFilterName());
            filterCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean isSelected) {
                    String[] dataSplit = f.getFilterData().split("\\|");
                    if (isSelected) {
                        logger.debug("Adding filter: " + f.getFilterData());
                        selectedMenuFilters.addAll(Arrays.asList(dataSplit));
                    } else {
                        logger.debug("Removing filter: " + f.getFilterData());
                        selectedMenuFilters.removeAll(Arrays.asList(dataSplit));
                    }
                    filterLog();
                }
            });

            VBox vBox = new VBox(filterCheckBox);
            vBox.setAlignment(Pos.CENTER_LEFT);
            vBox.setSpacing(20);
            vBox.setPadding(new Insets(0, 15, 0, 0));

            if (hBoxRef.hBox.getChildren().size() <= 8) {
                hBoxRef.hBox.getChildren().add(vBox);
            } else {
                filterCheckBoxes.getChildren().add(0, hBoxRef.hBox);
                vBoxRef.vBox.getChildren().addAll(hBoxRef.hBox);
                hBoxRef.hBox = new HBox();
                hBoxRef.hBox.setPadding(new Insets(10));
                hBoxRef.hBox.setId(UUID.randomUUID().toString());
            }
        });
        vBoxRef.vBox.getChildren().addAll(hBoxRef.hBox);
        filterCheckBoxes.getChildren().add(0, vBoxRef.vBox);
    }

    private EventHandler<? super KeyEvent> handleKeyEventPressed(final ListView<Text> logFileList) {
        return event -> {
            if (keyCombinationControlC.match(event)) {
                handleLineCopyEvent(logFileList);
            } else if (keyCombinationControlF.match(event)) {
                handleFindTextEvent(logFileList);
            }
        };
    }

    private void handleLineCopyEvent(final ListView<Text> logFileList) {
        List<Text> logList = logFileList.getSelectionModel().getSelectedItems();

        final ClipboardContent content = new ClipboardContent();
        StringBuilder stringBuilder = new StringBuilder();
        for (Text log : logList) {
            logger.debug("Found selected: " + log);
            stringBuilder.append(log).append("\n");
        }
        stringBuilder = stringBuilder.replace(stringBuilder.lastIndexOf("\n"), stringBuilder.lastIndexOf("\n")+2, "");
        logger.debug("Added to clipboard: " + stringBuilder.toString());
        content.putString(stringBuilder.toString());
        Clipboard.getSystemClipboard().setContent(content);
    }

    private void handleFindTextEvent(final ListView<Text> logFileList) {
        String textToFind = FindBox.showFindBox();
        logger.debug("Text to find: " + textToFind);

        if (textToFind == null || Strings.isEmpty(textToFind))
            return;

        clearSortedListSelections();
        AtomicInteger matchesFound = new AtomicInteger(0);
        final List<Integer> matchIndexPositionList = new ArrayList<>();
        for (int i = 0; i < logFileList.getItems().size(); i++) {
            if (logFileList.getItems().get(i).getText().toLowerCase().contains(textToFind.toLowerCase())) {
                logger.debug("Found selected in: " + logFileList.getItems().get(i));
                logFileList.getSelectionModel().select(i);
                logFileList.getFocusModel().focus(i);
                matchIndexPositionList.add(i);
                matchesFound.incrementAndGet();
            }
        }

        logger.debug("Matches found: " + matchesFound);
        if (matchesFound.get() > 0) {
            searchFoundMatches.setText("1/"+matchesFound);
            findPrevious.setOnMouseClicked(mouseEvent -> {

                List<Integer> reverseMatchesList = new ArrayList<>(matchIndexPositionList);
                Collections.reverse(reverseMatchesList);
                logger.debug("Matches indexes: " + reverseMatchesList.toString());

                for (int i = 0; i < reverseMatchesList.size(); i++) {
                    int currentSelectedIndex = logFileList.getSelectionModel().getSelectedIndex();
                    logger.debug("Currently selected index: " + currentSelectedIndex);
                    int matchIndex = reverseMatchesList.get(i);

                    if (currentSelectedIndex <= reverseMatchesList.get(reverseMatchesList.size()-1)) {
                        clearSortedListSelections();
                        logger.debug("Current index too big, going to last position again");
                        logFileList.scrollTo(reverseMatchesList.get(0));
                        logFileList.getSelectionModel().select(reverseMatchesList.get(0));
                        searchFoundMatches.setText(reverseMatchesList.size() + "/" + matchesFound.get());
                        break;
                    }

                    if (currentSelectedIndex > matchIndex) {
                        clearSortedListSelections();
                        logger.debug("Selected index bigger, going to: " + matchIndex);
                        logFileList.scrollTo(matchIndex);
                        logFileList.getSelectionModel().select(matchIndex);
                        searchFoundMatches.setText(reverseMatchesList.size()-i + "/" + matchesFound.get());
                        break;
                    }
                }
            });

            findNext.setOnMouseClicked(mouseEvent -> {
                logger.debug("Matches indexes: " + matchIndexPositionList.toString());

                for (int i = 0; i < matchIndexPositionList.size(); i++) {
                    int currentSelectedIndex = logFileList.getSelectionModel().getSelectedIndex();
                    logger.debug("Currently selected index: " + currentSelectedIndex);
                    int matchIndex = matchIndexPositionList.get(i);

                    if (currentSelectedIndex >= matchIndexPositionList.get(matchIndexPositionList.size()-1)) {
                        clearSortedListSelections();
                        logger.debug("Current index too big, going to first position again");
                        logFileList.scrollTo(matchIndexPositionList.get(0));
                        logFileList.getSelectionModel().select(0);
                        searchFoundMatches.setText(1 + "/" + matchesFound.get());
                        break;
                    }

                    if (matchIndex > currentSelectedIndex) {
                        clearSortedListSelections();
                        logger.debug("Selected index bigger, going to: " + matchIndex);
                        logFileList.scrollTo(matchIndex);
                        logFileList.getSelectionModel().select(matchIndex);
                        searchFoundMatches.setText(i+1 + "/" + matchesFound.get());
                        break;
                    }
                }
            });
        } else {
            findPrevious.setOnMouseClicked(null);
            findNext.setOnMouseClicked(null);
            searchFoundMatches.setText("No matches found");
        }
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
        final Text selectedSortedListElement = sortedLogFileList.getSelectionModel().getSelectedItem();
        List<Text> sortedLogList = new ArrayList<>(sortedLogFileList.getItems());
        sortedLogList.sort(Comparator.comparing(Text::getText));
        sortedLogList.removeIf(text -> text.getText().isEmpty());
        sortedLogFileList.getItems().clear();
        sortedLogFileList.getItems().addAll(sortedLogList);

        sortedLogFileList.getSelectionModel().select(selectedSortedListElement);
        sortedLogFileList.scrollTo(selectedSortedListElement);
    }

    private void removeNoDateLog() {
        final Text selectedSortedListElement = sortedLogFileList.getSelectionModel().getSelectedItem();
        List<Text> sortedLogList = new ArrayList<>(sortedLogFileList.getItems());
        List<Text> timestampFilteredList = logFileInteractor.removeLogsWithoutTimestamp(sortedLogList);

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
        final Text selectedSortedListElement = sortedLogFileList.getSelectionModel().getSelectedItem();

        clearListSelections(originalLogFileList);
        originalLogFileList.getSelectionModel().select(selectedSortedListElement);
        //originalLogFileList.getFocusModel().focus(selectedSortedListElement);
        originalLogFileList.scrollTo(selectedSortedListElement);
    }

    public void handleSortedListKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.DOWN) || keyEvent.getCode().equals(KeyCode.UP)) {
            keyEvent.consume(); // necessary to prevent event handlers for this event

            final Text selectedSortedListElement = sortedLogFileList.getSelectionModel().getSelectedItem();

            clearListSelections(originalLogFileList);
            originalLogFileList.getSelectionModel().select(selectedSortedListElement);
            originalLogFileList.scrollTo(selectedSortedListElement);
        }
    }

    public void handleMouseOriginalListClick(MouseEvent mouseEvent) {
        final Text selectedOriginalListElement = originalLogFileList.getSelectionModel().getSelectedItem();

        if (sortedLogFileList.getItems().contains(selectedOriginalListElement)) {
            clearListSelections(sortedLogFileList);
            sortedLogFileList.getSelectionModel().select(selectedOriginalListElement);
            sortedLogFileList.scrollTo(selectedOriginalListElement);
        }
    }

    public void handleOriginalListKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.DOWN) || keyEvent.getCode().equals(KeyCode.UP)) {
            keyEvent.consume(); // necessary to prevent event handlers for this event

            final Text originalSortedListElement = originalLogFileList.getSelectionModel().getSelectedItem();

            if (sortedLogFileList.getItems().contains(originalSortedListElement)) {
                clearListSelections(sortedLogFileList);
                sortedLogFileList.getSelectionModel().select(originalSortedListElement);
                sortedLogFileList.scrollTo(originalSortedListElement);
            }
        }
    }

    private void clearSortedListSelections() {
        sortedLogFileList.getSelectionModel().clearAndSelect(-1);
    }

    private void clearListSelections(ListView<Text> list) {
        list.getSelectionModel().clearAndSelect(-1);
    }
}