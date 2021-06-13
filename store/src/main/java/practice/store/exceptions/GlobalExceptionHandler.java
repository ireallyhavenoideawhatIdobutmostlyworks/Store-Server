package practice.store.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import practice.store.utils.values.RandomStringGenerator;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<Object> handleGlobalException() {
        String errorId = RandomStringGenerator.generateRandomUuid() + ". Timestamp: " + LocalDateTime.now();
        return new ResponseEntity<>("Something went wrong. Contact administrator with code " + errorId, HttpStatus.BAD_REQUEST);
    }
}
