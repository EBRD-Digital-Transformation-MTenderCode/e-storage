package com.procurement.storage.controller;

import com.procurement.storage.exception.FileValidationException;
import com.procurement.storage.model.dto.reservation.ReservResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionHandler {

//    @ResponseBody
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(ValidationException.class)
//    public ValidationErrorResponse handleValidationContractProcessPeriod(
//        final ValidationException e) {
//        String message = "Houston we have a problem";
//        return new ValidationErrorResponse(
//            message,
//            e.getErrors().getFieldErrors().stream()
//             .map(f -> new ValidationErrorResponse.ErrorPoint(
//                 f.getField(),
//                 f.getDefaultMessage(),
//                 f.getCode()))
//             .collect(Collectors.toList()));
//    }
//
//    @ResponseBody
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(JsonMappingException.class)
//    public MappingErrorResponse handleJsonMappingExceptionException(final JsonMappingException e) {
//        String message = "Houston we have a problem";
//        return new MappingErrorResponse(message, e);
//    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FileValidationException.class)
    public ReservResponseDto handleErrorInsertException(final FileValidationException e) {
        return new ReservResponseDto("400", e.getMessage(), null);
    }
}
