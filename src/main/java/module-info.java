module LogAnalyzer {
    requires commons.csv;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;

    opens org.log.presentation to javafx.fxml;
    exports org.log.presentation;
    exports org.log.presentation.controller;
}