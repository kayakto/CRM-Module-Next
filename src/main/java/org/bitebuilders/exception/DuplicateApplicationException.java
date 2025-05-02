package org.bitebuilders.exception;

public class DuplicateApplicationException extends RuntimeException {
    public DuplicateApplicationException() {
        super("Application already exists for this event and email");
    }
}
