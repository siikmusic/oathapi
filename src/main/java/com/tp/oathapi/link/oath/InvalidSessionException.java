package com.tp.oathapi.link.oath;

public class InvalidSessionException extends Exception {
    public InvalidSessionException(String message) {
        super(message);
    }
}
