package org.drools.test.exception;

public class RuleNotFiredException extends RuntimeException {
    public RuleNotFiredException() {
        super();
    }

    public RuleNotFiredException(String message) {
        super(message);
    }

    public RuleNotFiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuleNotFiredException(Throwable cause) {
        super(cause);
    }

    protected RuleNotFiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
