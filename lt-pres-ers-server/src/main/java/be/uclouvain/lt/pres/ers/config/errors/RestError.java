package be.uclouvain.lt.pres.ers.config.errors;

public class RestError {

    private final Code code;

    private final String message;

    private final Object context;

    public RestError(final Code code, final String message) {
        this(code, message, null);
    }

    public RestError(final Code code, final String message, final Object context) {
        this.code = code;
        this.message = message;
        this.context = context;
    }

    public Code getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public Object getContext() {
        return this.context;
    }

    public enum Code {
        CONSTRAINT_VIOLATION;
    }

}
