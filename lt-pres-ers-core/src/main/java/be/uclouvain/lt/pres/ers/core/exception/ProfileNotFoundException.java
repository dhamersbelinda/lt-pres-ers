package be.uclouvain.lt.pres.ers.core.exception;

public class ProfileNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ProfileNotFoundException() {
        super();
    }

    public ProfileNotFoundException(final String message) {
        super(message);
    }

    public ProfileNotFoundException(final Throwable cause) {
        super(cause);
    }

    public ProfileNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ProfileNotFoundException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
