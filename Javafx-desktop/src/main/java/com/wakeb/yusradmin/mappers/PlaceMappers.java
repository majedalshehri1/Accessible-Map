package com.wakeb.yusradmin.mappers;

import com.wakeb.yusradmin.dto.PlaceMapDTO;
import com.wakeb.yusradmin.models.Place;

import java.util.Optional;

public final class PlaceMappers {
    private PlaceMappers() {}


    public static Optional<PlaceMapDTO> toMapDTO(Place p) {
        if (p == null) return Optional.empty();

        Double lat = parseCoordinate(p.getLatitude());
        Double lng = parseCoordinate(p.getLongitude());
        if (lat == null || lng == null) {
            return Optional.empty();
        }

        String title = p.getPlaceName() != null ? p.getPlaceName() : ("Place #" + p.getId());
        String category = p.getCategory() != null ? p.getCategory().name() : "";

        return Optional.of(new PlaceMapDTO(p.getId(), title, category, lat, lng));
    }


    private static Double parseCoordinate(String s) {
        if (s == null) return null;
        s = s.trim().replace(',', '.');
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
