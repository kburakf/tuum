package org.example.exception;

public class InvalidAmountException extends TuumBusinessException{
    public InvalidAmountException(String message) {
        super(message);
    }
}
