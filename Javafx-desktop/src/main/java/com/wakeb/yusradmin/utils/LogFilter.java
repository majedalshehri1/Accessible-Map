package com.wakeb.yusradmin.utils;

public enum LogFilter {
    ALL("ALL", "الكل"),
    PLACE("PLACE","المكان"),
    REVIEW("REVIEW", "التقييم")


    ;
    private final String value;
    private final String label;

     LogFilter(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
