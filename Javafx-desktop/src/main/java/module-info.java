module com.wakeb.yusradmin {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;
    requires java.prefs;

    // JSON libraries
    requires com.fasterxml.jackson.databind;
    requires com.google.gson;

    // HTTP client
    requires java.net.http;

    // Ikonli Font Icons (if using them)
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    // Allow FXML to access controllers
    opens com.wakeb.yusradmin.controllers to javafx.fxml;

    // Allow Gson to reflect on model classes
    opens com.wakeb.yusradmin.models to com.google.gson;

    // Allow JavaFX to access Application class
    opens com.wakeb.yusradmin to javafx.graphics;

    // Public API exports
    exports com.wakeb.yusradmin;
    exports com.wakeb.yusradmin.controllers;
    exports com.wakeb.yusradmin.models;
    exports com.wakeb.yusradmin.dto;
}