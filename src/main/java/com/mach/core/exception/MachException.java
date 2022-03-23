package com.mach.core.exception;

public class MachException extends RuntimeException {

    private static final long serialVersionUID = 8718828512143269858L;

    public MachException() {
        super();
    }

    public MachException(String errorMessage) {
        super(errorMessage);
    }

    public MachException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public MachException(Throwable cause) {
        super(cause);
    }
}
