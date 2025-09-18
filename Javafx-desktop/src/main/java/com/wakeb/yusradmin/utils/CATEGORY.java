package com.wakeb.yusradmin.utils;

public enum CATEGORY {
    ALL("ALL", "الكل"),
    RESTAURANT("RESTAURANT", "المطاعم"),
    COFFEE("COFFEE", "المقاهي"),
    PARK("PARK", "المنتزهات"),
    HOSPITAL("HOSPITAL", "المستشفيات"),
    MALL("MALL", "أسواق");

    private final String value;
    private final String label;

    CATEGORY(String value, String label) {
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
