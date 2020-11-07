module presentation {
    requires javafx.controls;
    requires javafx.fxml;
    requires application;
    requires domain;
    requires infrastructure;

    opens org.log to javafx.fxml;
    exports org.log;
    exports org.log.controller;
}