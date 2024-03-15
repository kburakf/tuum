package org.example.exception;

public class DescriptionMissingException extends TuumBusinessException{
    public DescriptionMissingException(String message) {
        super(message);
    }
}
