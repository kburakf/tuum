package org.example.exception;

public class AccountMissingException extends TuumBusinessException{
    public AccountMissingException(String message) {
        super(message);
    }
}
