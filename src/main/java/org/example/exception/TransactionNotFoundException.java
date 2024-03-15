package org.example.exception;

public class TransactionNotFoundException extends TuumBusinessException{
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
