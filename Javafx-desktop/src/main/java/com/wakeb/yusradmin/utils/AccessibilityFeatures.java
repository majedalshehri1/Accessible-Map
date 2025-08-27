package com.wakeb.yusradmin.utils;

public enum AccessibilityFeatures {
    ELEVATORS("ELEVATORS", "مصاعد"),
    PARKING("PARKING", "مواقف مخصصة"),
    DEDICATED_RESTROOMS("DEDICATED_RESTROOMS", "دورات مياه مخصصة"),
    RAMPS("RAMPS", "سلالم مخصصة"),
    DEDICATED_DINING_TABLES("DEDICATED_DINING_TABLES", "طاولات طعام"),
    AUTOMATIC_DOORS("AUTOMATIC_DOORS", "أبواب أوتوماتيكية");

    private final String value;
    private final String  label;

    AccessibilityFeatures(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public static AccessibilityFeatures fromValue(String value) {
        for (AccessibilityFeatures accessibilityFeatures : AccessibilityFeatures.values()) {
            if (accessibilityFeatures.value.equals(value)) {
                return accessibilityFeatures;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return label;
    }
}
