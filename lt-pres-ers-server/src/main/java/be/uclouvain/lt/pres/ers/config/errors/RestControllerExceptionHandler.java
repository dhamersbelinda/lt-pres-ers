package be.uclouvain.lt.pres.ers.config.errors;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.NoArgsConstructor;

/**
 * Rest Controller Exception Handler to handle exceptions and transform them
 * into JSON.
 */
@RestControllerAdvice
@NoArgsConstructor
public class RestControllerExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestError handleConstraintViolationException(final ConstraintViolationException exception) {
        return new RestError(RestError.Code.CONSTRAINT_VIOLATION, exception.getMessage());
    }
}
