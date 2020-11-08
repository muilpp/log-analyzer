module LogAnalyzer {
    requires commons.csv;
    requires javafx.controls;
    requires javafx.fxml;

    opens org.log.presentation to javafx.fxml;
    exports org.log.presentation;
    exports org.log.presentation.controller;
}