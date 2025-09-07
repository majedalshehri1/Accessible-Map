package com.wakeb.yusradmin.utils;

import javafx.application.HostServices;

public final class HostServicesSinglton {
    private static HostServices hostServices;

    public static void setHostServices(HostServices hs) {
        if (hostServices != null) {
            throw new IllegalStateException("HostServices has already been set.");
        }
        hostServices = hs;
    }

    public static HostServices getHostServices() {
        if (hostServices == null) {
            throw new IllegalStateException("HostServices has not been set.");
        }
        return hostServices;
    }
}
