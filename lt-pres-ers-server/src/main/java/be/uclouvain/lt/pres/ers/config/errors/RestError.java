package be.uclouvain.lt.pres.ers.config.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class RestError {

    @Getter
    private final Code code;

    @Getter
    private final String message;

    @Getter
    private final Object context;

    public RestError(final Code code, final String message) {
        this(code, message, null);
    }

    public enum Code {
        CONSTRAINT_VIOLATION;
    }

}
