package com.wakeb.yusradmin.utils;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.TypeAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AccessibilityFeaturesTypeAdapter extends TypeAdapter<AccessibilityFeatures[]> {

    @Override
    public void write(JsonWriter out, AccessibilityFeatures[] values) throws IOException {
        if (values == null) {
            out.nullValue();
            return;
        }

        out.beginArray();
        for (AccessibilityFeatures value : values) {
            out.value(value.getValue());
        }
        out.endArray();
    }

    @Override
    public AccessibilityFeatures[] read(JsonReader in) throws IOException {
        List<AccessibilityFeatures> categories = new ArrayList<>();
        in.beginArray();
        while (in.hasNext()) {
            String jsonValue = in.nextString();
            AccessibilityFeatures category = AccessibilityFeatures.fromValue(jsonValue);
            categories.add(category);
        }
        in.endArray();
        return categories.toArray(new AccessibilityFeatures[0]);
    }
}
