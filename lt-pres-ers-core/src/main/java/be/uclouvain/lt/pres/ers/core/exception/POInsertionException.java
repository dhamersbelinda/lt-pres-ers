package be.uclouvain.lt.pres.ers.core.exception;

public class POInsertionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public POInsertionException() {
        super();
    }

    public POInsertionException(final String message) {
        super(message);
    }

    public POInsertionException(final Throwable cause) {
        super(cause);
    }

    public POInsertionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public POInsertionException(final String message, final Throwable cause, final boolean enableSuppression,
                                final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
