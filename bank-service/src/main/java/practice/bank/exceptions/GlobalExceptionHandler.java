package practice.bank.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import practice.bank.utils.GenerateRandomString;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    private final GenerateRandomString generateRandomString;


    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<Object> handleGlobalException() {
        return new ResponseEntity<>(createCommonExceptionBody(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    protected ResponseEntity<Object> entityNotFoundGlobalException() {
        return new ResponseEntity<>(createCommonExceptionBody(), HttpStatus.NOT_FOUND);
    }


    private String createCommonExceptionBody() {
        return String.format("Something went wrong. Contact administrator with code: %s.", createErrorID());
    }

    private String createErrorID() {
        return String.format("%s. Timestamp: %s.", generateRandomString.generateRandomUuid(), LocalDateTime.now());
    }
}
