package org.drools.test.exception;

public class UnexpectedRuleFiredException extends RuntimeException {
    public UnexpectedRuleFiredException() {
        super();
    }

    public UnexpectedRuleFiredException(String message) {
        super(message);
    }

    public UnexpectedRuleFiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedRuleFiredException(Throwable cause) {
        super(cause);
    }

    protected UnexpectedRuleFiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
