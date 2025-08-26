package com.wakeb.yusradmin.util;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

public class FXUtil {

    public static void error(String title, String msg) {
        var a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    public static boolean confirm(String title, String msg) {
        var a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        return a.showAndWait().filter(b -> b.getButtonData().isDefaultButton()).isPresent();
    }

    public record Loaded<T>(Parent root, T controller) {}
}

