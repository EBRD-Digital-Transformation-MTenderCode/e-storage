package com.procurement.storage.exception;

import lombok.Getter;

@Getter
public class UploadFileValidationException extends RuntimeException {

    private String message;

    public UploadFileValidationException(String message) {
        this.message = message;
    }
}
