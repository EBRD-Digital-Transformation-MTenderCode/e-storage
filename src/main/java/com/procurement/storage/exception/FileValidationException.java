package com.procurement.storage.exception;

import lombok.Getter;

@Getter
public class FileValidationException extends RuntimeException {

    private String message;

    public FileValidationException(String message) {
        this.message = message;
    }
}
