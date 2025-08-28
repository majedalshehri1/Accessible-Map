package com.wakeb.yusradmin.dto;

public class PlaceMapDTO {
    private long id;
    private String name;
    private String category;
    private double lat;
    private double lng;

    public PlaceMapDTO() {}
    public PlaceMapDTO(long id, String name, String category, double lat, double lng) {
        this.id = id; this.name = name; this.category = category; this.lat = lat; this.lng = lng;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
}
