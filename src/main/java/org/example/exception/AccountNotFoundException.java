package org.example.exception;

public class AccountNotFoundException extends TuumBusinessException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
