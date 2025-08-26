module com.wakeb.yusradmin {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires java.net.http;
    requires java.prefs;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    requires com.google.gson;

    opens com.wakeb.yusradmin.controllers to javafx.fxml;
    opens com.wakeb.yusradmin.models to com.fasterxml.jackson.databind, javafx.base;

    exports com.wakeb.yusradmin;
    exports com.wakeb.yusradmin.controllers;
    exports com.wakeb.yusradmin.models;
}
