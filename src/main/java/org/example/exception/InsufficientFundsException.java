package org.example.exception;

public class InsufficientFundsException extends TuumBusinessException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
