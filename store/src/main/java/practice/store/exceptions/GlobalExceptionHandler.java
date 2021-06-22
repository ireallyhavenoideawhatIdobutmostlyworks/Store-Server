package practice.store.exceptions;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import practice.store.utils.values.RandomStringGenerator;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@AllArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final RandomStringGenerator randomStringGenerator;


    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<Object> handleGlobalException() {
        return new ResponseEntity<>(createExceptionBody(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    protected ResponseEntity<Object> entityNotFoundGlobalException() {
        return new ResponseEntity<>(createExceptionBody(), HttpStatus.NOT_FOUND);
    }


    private String createExceptionBody() {
        return String.format("Something went wrong. Contact administrator with code %s", createErrorID());
    }

    private String createErrorID() {
        return String.format("%s. Timestamp: %s", randomStringGenerator.generateRandomUuid(), LocalDateTime.now());
    }
}
