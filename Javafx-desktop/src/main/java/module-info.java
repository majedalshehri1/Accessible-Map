module com.wakeb.yusradmin {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires javafx.graphics;
    requires java.prefs;
    requires com.google.gson;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    opens com.wakeb.yusradmin.controllers to javafx.fxml;
    exports com.wakeb.yusradmin;
}
