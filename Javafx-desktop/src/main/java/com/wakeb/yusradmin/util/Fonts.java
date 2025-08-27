package com.wakeb.yusradmin.util;

import javafx.scene.Scene;
import javafx.scene.text.Font;

import java.util.Objects;

public final class Fonts {
    private static boolean loaded = false;

    /** حمّل ملفات الخط مرة واحدة فقط */
    public static void ensureLoaded() {
        if (loaded) return;
        // تأكد أن المسارات صحيحة: resources/fonts/...
        Font.loadFont(Objects.requireNonNull(
                Fonts.class.getResourceAsStream("/fonts/Cairo-Regular.ttf")), 12);
        Font.loadFont(Objects.requireNonNull(
                Fonts.class.getResourceAsStream("/fonts/Cairo-Bold.ttf")), 12);
        loaded = true;
    }

    /** طبّق الخط والـ CSS على مشهد معيّن */
    public static void applyTo(Scene scene) {
        ensureLoaded();
        // أربط الـ CSS (اختياري لكنه مفيد لباقي الستايلات)
        scene.getStylesheets().add(Objects.requireNonNull(
                Fonts.class.getResource("/css/main.css")).toExternalForm());
        // فرض الخط على الجذر = ينطبق على كل العناصر تحت المشهد
        scene.getRoot().setStyle("-fx-font-family: 'Cairo';");
    }
}
