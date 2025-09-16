package com.wakeb.yusradmin.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakeb.yusradmin.models.SurveyRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SurveyService {

    private final ApiClient api; // you already have this class

    public SurveyService(String baseUrl) {
        this.api = new ApiClient(baseUrl);
    }

    // GET /api/admin/survey/all
    public List<SurveyRow> fetchAll() throws Exception {
        // We don't have the frontend DTO class for SurveyResponseDTO,
        // so parse into a generic List<Map> then map fields safely.
        List<Map<String, Object>> raw = api.get(
                "/api/admin/survey/all",
                new TypeReference<List<Map<String, Object>>>() {}
        );
        List<SurveyRow> rows = new ArrayList<>();
        for (Map<String, Object> m : raw) {
            // backend DTO uses Lombok @Data with field "Id" but getter "getId()" → JSON "id"
            Long id = toLong(m.get("id"));
            if (id == null && m.containsKey("Id")) { // just in case
                id = toLong(m.get("Id"));
            }
            Long userId = toLong(m.get("userId"));
            String description = (String) m.getOrDefault("description", "");
            Integer rating = toInt(m.get("rating"));
            String userName = (String) (m.get("userName") != null ? m.get("userName") : m.get("username"));
            SurveyRow row = new SurveyRow(id, userId, description, rating);
            row.setUserName(userName != null ? userName : "");
            Object r = m.get("read");
            if (r instanceof Boolean) row.setRead((Boolean) r);
            rows.add(row);
        }
        return rows;
    }

    // DELETE /api/admin/survey/delete/{id}
    public void deleteById(Long id) throws Exception {
        api.delete("/api/admin/survey/delete/" + id);
    }

    private Long toLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).longValue();
        try { return Long.parseLong(o.toString()); } catch (Exception e) { return null; }
    }

    private Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).intValue();
        try { return Integer.parseInt(o.toString()); } catch (Exception e) { return null; }
    }

    public void updateRead(Long id, boolean read) throws Exception {
        api.put("/api/admin/survey/" + id + "/read",
                java.util.Map.of("read", read),
                null);
    }
}
