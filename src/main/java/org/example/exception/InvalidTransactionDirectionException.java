package org.example.exception;

public class InvalidTransactionDirectionException extends TuumBusinessException {
    public InvalidTransactionDirectionException(String message) {
        super(message);
    }
}
