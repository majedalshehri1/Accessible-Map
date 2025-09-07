package com.wakeb.yusradmin.util;

import javafx.scene.Scene;
import javafx.scene.text.Font;

import java.util.Objects;

public final class Fonts {
    private static boolean loaded = false;


    public static void ensureLoaded() {
        if (loaded) return;

        Font.loadFont(Objects.requireNonNull(
                Fonts.class.getResourceAsStream("/fonts/Cairo-Regular.ttf")), 12);
        Font.loadFont(Objects.requireNonNull(
                Fonts.class.getResourceAsStream("/fonts/Cairo-Bold.ttf")), 12);
        loaded = true;
    }


    public static void applyTo(Scene scene) {
        ensureLoaded();

        scene.getStylesheets().add(Objects.requireNonNull(
                Fonts.class.getResource("/css/main.css")).toExternalForm());

        scene.getRoot().setStyle("-fx-font-family: 'Cairo';");
    }
}
