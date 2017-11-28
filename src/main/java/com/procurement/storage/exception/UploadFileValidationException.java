package com.procurement.storage.exception;

import lombok.Getter;

@Getter
public class UploadFileValidationException extends RuntimeException {

    private final String message;

    public UploadFileValidationException(final String message) {
        this.message = message;
    }
}
