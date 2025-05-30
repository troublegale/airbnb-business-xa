package itmo.tg.airbnb_xa.business.exception;

import itmo.tg.airbnb_xa.business.exception.exceptions.NotAllowedException;
import itmo.tg.airbnb_xa.business.exception.exceptions.TicketAlreadyPublishedException;
import itmo.tg.airbnb_xa.business.exception.exceptions.TransactionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalBusinessExceptionHandler {

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<String> handleNotAllowedException(NotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        var fieldErrors = ex.getFieldErrors();
        var fieldNames = fieldErrors.stream().map(FieldError::getField).distinct().toList();

        var sb = new StringBuilder();

        for (var field : fieldNames) {

            var messages = fieldErrors.stream()
                    .filter(e -> e.getField().equals(field))
                    .map(FieldError::getDefaultMessage).toList();

            sb.append(field).append(' ');

            var iter = messages.iterator();
            while (iter.hasNext()) {
                sb.append(iter.next());
                if (iter.hasNext()) {
                    sb.append(", ");
                }
            }

            sb.append('\n');

        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sb.toString());

    }

    @ExceptionHandler(TicketAlreadyPublishedException.class)
    public ResponseEntity<String> handleTicketAlreadyPublishedException(TicketAlreadyPublishedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ignored) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad HTTP request, possibly invalid field values");
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<String> handleTransactionException(TransactionException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

}
