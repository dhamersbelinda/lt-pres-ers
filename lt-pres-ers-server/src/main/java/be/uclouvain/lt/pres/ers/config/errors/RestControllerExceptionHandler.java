package be.uclouvain.lt.pres.ers.config.errors;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Rest Controller Exception Handler to handle exceptions and transform them
 * into JSON.
 */
@RestControllerAdvice
public class RestControllerExceptionHandler {

    public RestControllerExceptionHandler() {
        super();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestError handleConstraintViolationException(final ConstraintViolationException exception) {
        return new RestError(RestError.Code.CONSTRAINT_VIOLATION, exception.getMessage());
    }
}
