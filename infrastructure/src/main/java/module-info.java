module infrastructure {
    requires application;
    requires domain;
    requires commons.csv;

    exports org.log.persistance;
}