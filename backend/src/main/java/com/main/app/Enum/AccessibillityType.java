package com.main.app.Enum;

public enum AccessibillityType {
    PARKING,
    ELEVATORS,
    AUTOMATIC_DOORS,
    RAMPS,
    DEDICATED_DINING_TABLES,
    DEDICATED_RESTROOMS;

    public static AccessibillityType getByName(String type) {
        switch (type) {
            case "PARKING": return AccessibillityType.PARKING;
            case "ELEVATORS": return AccessibillityType.ELEVATORS;
            case "AUTOMATIC_DOORS": return AccessibillityType.AUTOMATIC_DOORS;
            case "RAMPS": return AccessibillityType.RAMPS;
            case "DEDICATED_DINING_TABLES": return AccessibillityType.DEDICATED_DINING_TABLES;
            case "DEDICATED_RESTROOMS": return AccessibillityType.DEDICATED_RESTROOMS;
            default:  return AccessibillityType.PARKING;
        }
    }
}
