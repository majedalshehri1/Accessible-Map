package com.wakeb.yusradmin.services;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;

public class StatsServiceHttp {
    private final ApiClient api;
    public StatsServiceHttp(ApiClient api) { this.api = api; }

    //for calling Backend (GET)
    public Map<String, Integer> placesByCategory() throws Exception {

        List<List<Object>> rows = api.get(
                "/api/admin/count/placeCategory",
                new TypeReference<List<List<Object>>>() {}
        );

        Map<String, Integer> out = new LinkedHashMap<>();
        if (rows != null) {
            for (List<Object> row : rows) {
                if (row == null || row.size() < 2) continue;

                String category = String.valueOf(row.get(0));
                Object n = row.get(1);
                int count = (n instanceof Number)
                        ? ((Number) n).intValue()
                        : Integer.parseInt(String.valueOf(n));

                out.put(category, count);
            }
        }
        return out;
    }
}
