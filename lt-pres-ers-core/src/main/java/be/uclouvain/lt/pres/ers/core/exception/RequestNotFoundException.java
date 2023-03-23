package be.uclouvain.lt.pres.ers.core.exception;

public class RequestNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RequestNotFoundException() {
        super();
    }

    public RequestNotFoundException(final String message) {
        super(message);
    }

    public RequestNotFoundException(final Throwable cause) {
        super(cause);
    }

    public RequestNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RequestNotFoundException(final String message, final Throwable cause, final boolean enableSuppression,
                                    final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
