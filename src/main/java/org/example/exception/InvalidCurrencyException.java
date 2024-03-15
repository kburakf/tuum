package org.example.exception;

public class InvalidCurrencyException extends TuumBusinessException {
    public InvalidCurrencyException(String message) {
        super(message);
    }
}
