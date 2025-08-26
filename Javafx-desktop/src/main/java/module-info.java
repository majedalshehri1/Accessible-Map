module com.wakeb.yusradmin {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires javafx.graphics;
    requires java.prefs;
    requires com.google.gson;
    requires com.fasterxml.jackson.annotation;
    opens com.wakeb.yusradmin.controllers to javafx.fxml;
    opens com.wakeb.yusradmin.models to com.google.gson;

    exports com.wakeb.yusradmin;



}
