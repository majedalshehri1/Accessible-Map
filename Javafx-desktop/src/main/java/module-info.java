module com.wakeb.yusradmin {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires javafx.graphics;
    requires java.prefs;
    requires com.google.gson;
    opens com.wakeb.yusradmin.models to com.google.gson;
    opens com.wakeb.yusradmin.dto to com.google.gson;
    opens com.wakeb.yusradmin.controllers to javafx.fxml;
    exports com.wakeb.yusradmin;
}
