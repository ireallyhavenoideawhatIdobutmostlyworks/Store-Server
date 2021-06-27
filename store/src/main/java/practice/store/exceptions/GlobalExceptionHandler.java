package practice.store.exceptions;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import practice.store.utils.values.RandomStringGenerator;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@AllArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    private final RandomStringGenerator randomStringGenerator;

    private final String CONTACT_ADMINISTRATOR = "Contact administrator with code";


    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<Object> handleGlobalException() {
        return new ResponseEntity<>(createCommonExceptionBody(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    protected ResponseEntity<Object> entityNotFoundGlobalException() {
        return new ResponseEntity<>("dupa dupa dupa", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = InternalAuthenticationServiceException.class)
    protected ResponseEntity<Object> internalAuthenticationServiceExceptionGlobalException() {
        return new ResponseEntity<>(createAuthenticationExceptionBody(), HttpStatus.UNAUTHORIZED);
    }


    private String createCommonExceptionBody() {
        return String.format("Something went wrong. %s %s.", CONTACT_ADMINISTRATOR, createErrorID());
    }

    private String createAuthenticationExceptionBody() {
        return String.format("Incorrect credentials or user not exist. %s %s.", CONTACT_ADMINISTRATOR, createErrorID());
    }

    private String createErrorID() {
        return String.format("%s. Timestamp: %s.", randomStringGenerator.generateRandomUuid(), LocalDateTime.now());
    }
}
