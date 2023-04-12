package be.uclouvain.lt.pres.ers.core.exception;

public class PONotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PONotFoundException() {
        super();
    }

    public PONotFoundException(final String message) {
        super(message);
    }

    public PONotFoundException(final Throwable cause) {
        super(cause);
    }

    public PONotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PONotFoundException(final String message, final Throwable cause, final boolean enableSuppression,
                               final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
