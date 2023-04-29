package be.uclouvain.lt.pres.ers.server.delegates;

import be.uclouvain.lt.pres.ers.server.model.DsbInternationalStringType;
import be.uclouvain.lt.pres.ers.server.model.DsbResultType;
import be.uclouvain.lt.pres.ers.server.model.PresResponseType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ValidationResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException e, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        PresResponseType body = new PresResponseType();
        DsbResultType result = new DsbResultType();
        result.setMaj(DsbResultType.MajEnum.RESULTMAJOR_REQUESTERERROR);
        DsbInternationalStringType msg = new DsbInternationalStringType();
        msg.setValue("Request body does not implement API JSON schema specification (ETSI TS 119 512).");
        msg.setLang("EN");
        result.setMsg(msg);
        body.setResult(result);

        return handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
