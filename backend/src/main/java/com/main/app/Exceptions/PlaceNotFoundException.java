package com.main.app.Exceptions;

public class PlaceNotFoundException extends RuntimeException {
    public PlaceNotFoundException(Long id) {
        super("Place not found: " + id);
    }
    public PlaceNotFoundException(String name) {
        super("Place not found with name: " + name);
    }
}
