package com.wakeb.yusradmin.services;


import com.fasterxml.jackson.core.type.TypeReference;
import com.wakeb.yusradmin.dto.PlaceMapDTO;
import java.util.List;

public class MapService {
    private final ApiClient api;
    public MapService(ApiClient api) { this.api = api; }

    public List<PlaceMapDTO> listForMap() throws Exception {
        return api.get("/api/place/all", new TypeReference<List<PlaceMapDTO>>() {});
    }
}