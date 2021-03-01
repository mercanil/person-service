package com.mercan.person.handler;

import com.mercan.person.exception.ObjectNotFound;
import com.mercan.person.pojo.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

import static com.mercan.person.constants.PersonServiceConstants.ERROR_MESSAGE_RESOURCE_NOT_FOUND;

@Component
@RestControllerAdvice
public class PersonWebExceptionHandler {

    @ExceptionHandler({ObjectNotFound.class})
    public ResponseEntity<ApiError> handleObjectNotFound(ObjectNotFound objectNotFound) {
        List<String> errors = new ArrayList<>();
        errors.add(String.format(ERROR_MESSAGE_RESOURCE_NOT_FOUND, objectNotFound.getCollection(), objectNotFound.getId()));
        ApiError build = ApiError.builder()
                .reasonCode(HttpStatus.NOT_FOUND.name())
                .errors(errors)
                .build();
        return new ResponseEntity<>(build, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiError> handleNotValidException(MethodArgumentNotValidException notValidException) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : notValidException.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        ApiError build = ApiError.builder()
                .reasonCode(HttpStatus.BAD_REQUEST.name())
                .errors(errors)
                .build();
        return new ResponseEntity<>(build, HttpStatus.BAD_REQUEST);
    }
}
