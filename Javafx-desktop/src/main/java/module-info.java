module com.wakeb.yusradmin {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.net.http;
    requires java.prefs;
    requires com.google.gson;

    // Allow FXML to access controllers
    opens com.wakeb.yusradmin.controllers to javafx.fxml;

    // Allow Gson to reflect on model classes
    opens com.wakeb.yusradmin.models to com.google.gson;

    // Allow JavaFX to access Application class (if in root package)
    opens com.wakeb.yusradmin to javafx.graphics;

    // Public API exports
    exports com.wakeb.yusradmin;
    exports com.wakeb.yusradmin.controllers;
}
