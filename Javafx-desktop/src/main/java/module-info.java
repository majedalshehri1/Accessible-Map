module com.wakeb.yusradmin {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.prefs;


    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    requires com.google.gson;
    requires java.net.http;

    // Allow FXML to access controllers

    opens com.wakeb.yusradmin.controllers to javafx.fxml;

    // Allow Gson to reflect on model classes
    opens com.wakeb.yusradmin.models to com.google.gson;

    // Allow JavaFX to access Application class (if in root package)
    opens com.wakeb.yusradmin to javafx.graphics;

    // Public API exports

    exports com.wakeb.yusradmin;
    exports com.wakeb.yusradmin.controllers;
    exports com.wakeb.yusradmin.models;
}
