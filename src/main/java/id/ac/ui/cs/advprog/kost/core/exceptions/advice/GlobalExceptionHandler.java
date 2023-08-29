package id.ac.ui.cs.advprog.kost.core.exceptions.advice;

import id.ac.ui.cs.advprog.kost.occupancy.exceptions.OccupancyFilterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import id.ac.ui.cs.advprog.kost.bundle.exceptions.BundleDoesNotExistException;
import id.ac.ui.cs.advprog.kost.bundle.exceptions.BundleOutOfStockException;
import id.ac.ui.cs.advprog.kost.core.exceptions.ErrorTemplate;
import id.ac.ui.cs.advprog.kost.core.exceptions.InvalidDateException;
import id.ac.ui.cs.advprog.kost.order.exceptions.BundleOrderDoesNotExistException;
import id.ac.ui.cs.advprog.kost.rent.exceptions.InvalidTenantException;
import id.ac.ui.cs.advprog.kost.rent.exceptions.KostRentDoesNotExistException;
import id.ac.ui.cs.advprog.kost.room.exceptions.KostRoomDoesNotExistException;
import id.ac.ui.cs.advprog.kost.room.exceptions.KostRoomOutOfStockException;
import id.ac.ui.cs.advprog.kost.occupancy.exceptions.OccupancyFutureException;
import id.ac.ui.cs.advprog.kost.rental.exceptions.RentalFutureException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = { KostRoomDoesNotExistException.class, KostRentDoesNotExistException.class,
            BundleDoesNotExistException.class, BundleOrderDoesNotExistException.class, OccupancyFilterException.class
    })
    public ResponseEntity<Object> kostandBundleNotAvailable(Exception exception) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        var baseException = new ErrorTemplate(
                exception.getMessage(),
                status,
                ZonedDateTime.now(ZoneId.of("Z")));

        return new ResponseEntity<>(baseException, status);
    }

    @ExceptionHandler(value = { KostRoomOutOfStockException.class, BundleOutOfStockException.class
    })
    public ResponseEntity<Object> kostRoomOutOfStock(Exception exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        var baseException = new ErrorTemplate(
                exception.getMessage(),
                status,
                ZonedDateTime.now(ZoneId.of("Z")));

        return new ResponseEntity<>(baseException, status);
    }

    @ExceptionHandler(value = { InvalidTenantException.class
    })
    public ResponseEntity<Object> invalidTenant(Exception exception) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        var baseException = new ErrorTemplate(
                exception.getMessage(),
                status,
                ZonedDateTime.now(ZoneId.of("Z")));

        return new ResponseEntity<>(baseException, status);
    }

    @ExceptionHandler(value = { OccupancyFutureException.class, RentalFutureException.class
    })
    public ResponseEntity<Object> completableFutureException(Exception exception) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorTemplate baseException = new ErrorTemplate(
                exception.getMessage(),
                status,
                ZonedDateTime.now(ZoneId.of("Z")));

        return new ResponseEntity<>(baseException, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> customValidationErrorHandling(MethodArgumentNotValidException exception) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.BAD_REQUEST);

        Map<String, String> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        body.put("fieldErrors", fieldErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorTemplate baseException = new ErrorTemplate(
                exception.getMessage(),
                status,
                ZonedDateTime.now(ZoneId.of("Z")));

        return new ResponseEntity<>(baseException, status);
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<Object> handleInvalidDateRequest(InvalidDateException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorTemplate baseException = new ErrorTemplate(
                exception.getMessage(),
                status,
                ZonedDateTime.now(ZoneId.of("Z")));

        return new ResponseEntity<>(baseException, status);
    }

}
