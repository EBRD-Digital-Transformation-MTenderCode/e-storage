package com.procurement.storage.controller;

import com.procurement.storage.exception.ReservFileValidationException;
import com.procurement.storage.exception.UploadFileValidationException;
import com.procurement.storage.model.dto.reservation.ReservResponseDto;
import com.procurement.storage.model.dto.upload.LoadResponseDto;
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
    @ExceptionHandler(ReservFileValidationException.class)
    public ReservResponseDto handleErrorInsertException(final ReservFileValidationException e) {
        return new ReservResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UploadFileValidationException.class)
    public LoadResponseDto handleErrorInsertException(final UploadFileValidationException e) {
        return new LoadResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }
}
