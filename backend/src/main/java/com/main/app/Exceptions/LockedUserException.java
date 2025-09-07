package com.main.app.Exceptions;

public class LockedUserException extends RuntimeException {
    public LockedUserException(String username) {
        super("User is Blocked : " + username);
    }
}