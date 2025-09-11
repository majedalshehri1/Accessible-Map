module com.wakeb.yusradmin {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    // JSON libraries
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    requires com.google.gson;
    opens com.wakeb.yusradmin.dto to com.google.gson;
    requires java.net.http;
    requires java.management;

    requires com.sothawo.mapjfx; // MapJFX for map functionalities

    // Ikonli Font Icons (if using them)
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.desktop;
    requires java.prefs;
    requires static lombok;

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
    exports com.wakeb.yusradmin.dto;
}
