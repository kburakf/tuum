package org.example.exception;

public class BalanceNotFoundException extends TuumBusinessException{
    public BalanceNotFoundException(String message) {
        super(message);
    }
}
