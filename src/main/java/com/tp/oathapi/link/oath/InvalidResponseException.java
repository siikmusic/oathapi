package com.tp.oathapi.link.oath;

public class InvalidResponseException extends Exception {
    public InvalidResponseException(String message) {
        super(message);
    }
}
