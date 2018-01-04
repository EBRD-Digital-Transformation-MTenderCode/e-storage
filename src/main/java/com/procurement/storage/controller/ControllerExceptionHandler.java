package com.procurement.storage.controller;

import com.procurement.storage.exception.GetFileException;
import com.procurement.storage.exception.RegistrationValidationException;
import com.procurement.storage.exception.UploadFileValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseBody
    @ExceptionHandler(RegistrationValidationException.class)
    public ResponseEntity<String> handleErrorInsertException(final RegistrationValidationException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(UploadFileValidationException.class)
    public ResponseEntity<String> handleErrorInsertException(final UploadFileValidationException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(GetFileException.class)
    public ResponseEntity<String> handleErrorInsertException(final GetFileException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
