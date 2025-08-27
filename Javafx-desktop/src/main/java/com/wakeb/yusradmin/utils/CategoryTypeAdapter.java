package com.wakeb.yusradmin.utils;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.TypeAdapter;
import java.io.IOException;

public class CategoryTypeAdapter extends TypeAdapter<CATEGORY> {

    @Override
    public void write(JsonWriter out, CATEGORY value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.getValue());
    }

    @Override
    public CATEGORY read(JsonReader in) throws IOException {
        String jsonValue = in.nextString();
        for (CATEGORY category : CATEGORY.values()) {
            if (category.getValue().equals(jsonValue)) {
                return category;
            }
        }
        return null;
    }
}