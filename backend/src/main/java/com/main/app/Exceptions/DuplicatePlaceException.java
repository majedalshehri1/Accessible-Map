package com.main.app.Exceptions;

public class DuplicatePlaceException extends RuntimeException {
    public DuplicatePlaceException(String name) {
        super("Place with the same name already exists: " + name);
    }
}
